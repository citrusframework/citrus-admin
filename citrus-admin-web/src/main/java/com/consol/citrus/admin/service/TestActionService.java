/*
 * Copyright 2006-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.consol.citrus.admin.service;

import com.consol.citrus.admin.exception.ApplicationRuntimeException;
import com.consol.citrus.admin.marshal.TestActionMarshaller;
import com.consol.citrus.admin.model.Project;
import com.consol.citrus.util.FileUtils;
import com.consol.citrus.util.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.xml.transform.StringResult;
import org.springframework.xml.transform.StringSource;
import org.w3c.dom.ls.LSParser;

import javax.annotation.PostConstruct;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Christoph Deppisch
 */
@Service
public class TestActionService {

    public static final String FAILED_TO_UPDATE_TEST_ACTION = "Failed to update test action";
    public static final String UNABLE_TO_READ_TRANSFORMATION_SOURCE = "Unable to read update test action transformation source";

    @Autowired
    private TestActionMarshaller testActionMarshaller;

    /** XSLT transformer factory */
    private TransformerFactory transformerFactory = TransformerFactory.newInstance();

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(TestActionService.class);

    @PostConstruct
    protected void init() {
        transformerFactory.setURIResolver((href, base) -> {
            try {
                return new StreamSource(new ClassPathResource("transform/" + href).getInputStream());
            } catch (IOException e) {
                throw new TransformerException("Failed to resolve uri: " + href, e);
            }
        });
    }

    /**
     * Method adds a new test action definition to the XML testcase file.
     * @param testFile
     * @param project
     * @param position
     * @param jaxbElement
     */
    public void addTestAction(File testFile, Project project, int position, Object jaxbElement) {
        Source xsltSource;
        Source xmlSource;
        try {
            xsltSource = new StreamSource(new ClassPathResource("transform/add-action.xsl").getInputStream());
            xsltSource.setSystemId("add-action");
            String source = FileUtils.readToString(new FileInputStream(testFile));
            xmlSource = new StringSource(source);

            //create transformer
            Transformer transformer = transformerFactory.newTransformer(xsltSource);
            transformer.setParameter("action_index", position + 1);
            transformer.setParameter("action_content", getXmlContent(jaxbElement)
                    .replaceAll("(?m)^(.)", getTabs(1, project.getSettings().getTabSize()) + "$1"));

            //transform
            StringResult result = new StringResult();
            transformer.transform(xmlSource, result);
            FileUtils.writeToFile(format(postProcess(source, result.toString()), project.getSettings().getTabSize()), testFile);
        } catch (IOException e) {
            throw new ApplicationRuntimeException(UNABLE_TO_READ_TRANSFORMATION_SOURCE, e);
        } catch (TransformerException e) {
            throw new ApplicationRuntimeException(FAILED_TO_UPDATE_TEST_ACTION, e);
        }
    }

    /**
     * Method removes a test action definition from the XML testcase file. Action definition is
     * identified by its position in the list of actions.
     * @param testFile
     * @param project
     * @param position
     */
    public void removeTestAction(File testFile, Project project, int position) {
        Source xsltSource;
        Source xmlSource;
        try {
            xsltSource = new StreamSource(new ClassPathResource("transform/delete-action.xsl").getInputStream());
            xsltSource.setSystemId("delete-action");

            String source = FileUtils.readToString(new FileInputStream(testFile));
            xmlSource = new StringSource(source);

            //create transformer
            Transformer transformer = transformerFactory.newTransformer(xsltSource);
            transformer.setParameter("action_index", position + 1);

            //transform
            StringResult result = new StringResult();
            transformer.transform(xmlSource, result);
            FileUtils.writeToFile(format(postProcess(source, result.toString()), project.getSettings().getTabSize()), testFile);
        } catch (IOException e) {
            throw new ApplicationRuntimeException(UNABLE_TO_READ_TRANSFORMATION_SOURCE, e);
        } catch (TransformerException e) {
            throw new ApplicationRuntimeException(FAILED_TO_UPDATE_TEST_ACTION, e);
        }
    }

    /**
     * Method updates an existing test action definition in a XML testcase file. Action definition is
     * identified by its position in the list of actions.
     * @param testFile
     * @param project
     * @param position
     * @param jaxbElement
     */
    public void updateTestAction(File testFile, Project project, int position, Object jaxbElement) {
        Source xsltSource;
        Source xmlSource;
        try {
            xsltSource = new StreamSource(new ClassPathResource("transform/update-action.xsl").getInputStream());
            xsltSource.setSystemId("update-action");

            LSParser parser = XMLUtils.createLSParser();
            parser.parseURI(testFile.toURI().toString());
            String source = FileUtils.readToString(new FileInputStream(testFile));
            xmlSource = new StringSource(source);

            //create transformer
            Transformer transformer = transformerFactory.newTransformer(xsltSource);
            transformer.setParameter("action_index", position + 1);
            transformer.setParameter("action_content", getXmlContent(jaxbElement)
                    .replaceAll("(?m)^(\\s<)", getTabs(1, project.getSettings().getTabSize()) + "$1")
                    .replaceAll("(?m)^(</)", getTabs(1, project.getSettings().getTabSize()) + "$1"));

            //transform
            StringResult result = new StringResult();
            transformer.transform(xmlSource, result);
            FileUtils.writeToFile(format(postProcess(source, result.toString()), project.getSettings().getTabSize()), testFile);
        } catch (IOException e) {
            throw new ApplicationRuntimeException(UNABLE_TO_READ_TRANSFORMATION_SOURCE, e);
        } catch (TransformerException e) {
            throw new ApplicationRuntimeException(FAILED_TO_UPDATE_TEST_ACTION, e);
        }
    }

    /**
     * Post process transformed data with CDATA sections preserved.
     * @param source
     * @param result
     * @return
     */
    private String postProcess(String source, String result) {
        Pattern cdata = Pattern.compile("^\\s*<!\\[CDATA\\[(?<text>(?>[^]]+|](?!]>))*)]]>", Pattern.MULTILINE);
        Matcher cdataMatcher = cdata.matcher(source);

        while (cdataMatcher.find()) {
            Pattern nocdata = Pattern.compile("^[\\s\\n\\r]*" + cdataMatcher.group(1) + "[\\s\\n\\r]*$", Pattern.MULTILINE);
            Matcher nocdataMatcher = nocdata.matcher(result);
            if (nocdataMatcher.find()) {
                result = nocdataMatcher.replaceFirst(cdataMatcher.group());
            }
        }

        return result;
    }

    /**
     * Marshal jaxb element and try to perform basic formatting like namespace clean up
     * and attribute formatting with xsl transformation.
     * @param jaxbElement
     * @return
     */
    private String getXmlContent(Object jaxbElement) {
        StringResult jaxbContent = new StringResult();

        testActionMarshaller.marshal(jaxbElement, jaxbContent);

        Source xsltSource;
        try {
            xsltSource = new StreamSource(new ClassPathResource("transform/format-bean.xsl").getInputStream());
            Transformer transformer = transformerFactory.newTransformer(xsltSource);

            //transform
            StringResult result = new StringResult();
            transformer.transform(new StringSource(jaxbContent.toString()), result);

            if (log.isDebugEnabled()) {
                log.debug("Created bean definition:\n" + result.toString());
            }

            return result.toString();
        } catch (IOException e) {
            throw new ApplicationRuntimeException(UNABLE_TO_READ_TRANSFORMATION_SOURCE, e);
        } catch (TransformerException e) {
            throw new ApplicationRuntimeException(FAILED_TO_UPDATE_TEST_ACTION, e);
        }
    }

    /**
     * Do final formatting with test bean XML configuration content.
     * Removes empty double lines and formats schemaLocation attribute value with new lines.
     * @param content
     * @param tabSize
     * @return
     */
    private String format(String content, int tabSize) {
        for (Map.Entry<String, String> namespaceEntry : testActionMarshaller.getNamespacePrefixMapper().getNamespaceMappings().entrySet()) {
            if (content.contains(String.format("<%s:", namespaceEntry.getValue())) && !content.contains("xmlns:" + namespaceEntry.getValue())) {
                String namespaceMarker = "xmlns:citrus=";
                int position = content.indexOf(namespaceMarker);

                if (position < 0) {
                    position = content.indexOf("xmlns=");
                }

                if (position < 0) {
                    throw new ApplicationRuntimeException("Invalid test file - missing default namespace declaration");
                }
                
                content = content.substring(0, position) + String.format("xmlns:%s=\"%s\" %n%s", namespaceEntry.getValue(), namespaceEntry.getKey(), getTabs(5, tabSize)) + content.substring(position);
            }
        }

        return content.replaceAll("(?m)(^\\s*$)+", "").replaceAll("\\.xsd\\s", ".xsd\n" + getTabs(3, tabSize));
    }

    /**
     * Construct tabs according to project settings.
     * @param amount
     * @param tabSize
     * @return
     */
    private String getTabs(int amount, int tabSize) {
        StringBuilder tabs = new StringBuilder();

        for (int i = 1; i <= amount; i++) {
            for (int k = 1; k <= tabSize; k++) {
                tabs.append(" ");
            }
        }

        return tabs.toString();
    }

    /**
     * Sets the testActionMarshaller property.
     *
     * @param testActionMarshaller
     */
    public void setTestActionMarshaller(TestActionMarshaller testActionMarshaller) {
        this.testActionMarshaller = testActionMarshaller;
    }
}
