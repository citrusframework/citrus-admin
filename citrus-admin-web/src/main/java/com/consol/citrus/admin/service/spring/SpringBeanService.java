/*
 * Copyright 2006-2016 the original author or authors.
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

package com.consol.citrus.admin.service.spring;

import com.consol.citrus.admin.exception.ApplicationRuntimeException;
import com.consol.citrus.admin.marshal.SpringBeanMarshaller;
import com.consol.citrus.admin.model.spring.SpringBean;
import com.consol.citrus.admin.service.ProjectService;
import com.consol.citrus.admin.service.spring.filter.*;
import com.consol.citrus.util.FileUtils;
import com.consol.citrus.util.XMLUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.xml.transform.StringResult;
import org.springframework.xml.transform.StringSource;
import org.w3c.dom.Element;
import org.w3c.dom.ls.LSParser;
import org.w3c.dom.ls.LSSerializer;

import javax.annotation.PostConstruct;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchema;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.*;

/**
 * @author Christoph Deppisch
 */
@Service
public class SpringBeanService {

    public static final String FAILED_TO_UPDATE_BEAN_DEFINITION = "Failed to update bean definition";
    public static final String UNABLE_TO_READ_TRANSFORMATION_SOURCE = "Unable to read update bean definition transformation source";

    @Autowired
    private SpringBeanMarshaller springBeanMarshaller;

    @Autowired
    private ProjectService projectService;

    /** XSLT transformer factory */
    private TransformerFactory transformerFactory = TransformerFactory.newInstance();

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(SpringBeanService.class);

    @PostConstruct
    protected void init() {
        transformerFactory.setURIResolver(new URIResolver() {
            @Override
            public Source resolve(String href, String base) throws TransformerException {
                try {
                    return new StreamSource(new ClassPathResource("transform/" + href).getInputStream());
                } catch (IOException e) {
                    throw new TransformerException("Failed to resolve uri: " + href, e);
                }
            }
        });
    }

    /**
     * Reads file import locations from Spring bean application context.
     * @param configFile
     * @return
     */
    public List<File> getConfigImports(File configFile) {
        LSParser parser = XMLUtils.createLSParser();

        GetSpringImportsFilter filter = new GetSpringImportsFilter(configFile);
        parser.setFilter(filter);
        parser.parseURI(configFile.toURI().toString());

        return filter.getImportedFiles();
    }

    /**
     * Finds bean definition element by id and type in Spring application context and
     * performs unmarshalling in order to return JaxB object.
     * @param configFile
     * @param id
     * @param type
     * @return
     */
    public <T> T getBeanDefinition(File configFile, String id, Class<T> type) {
        LSParser parser = XMLUtils.createLSParser();

        GetSpringBeanFilter filter = new GetSpringBeanFilter(id, type);
        parser.setFilter(filter);

        List<File> configFiles = new ArrayList<File>();
        configFiles.add(configFile);
        configFiles.addAll(getConfigImports(configFile));

        for (File file : configFiles) {
            parser.parseURI(file.toURI().toString());

            if (filter.getBeanDefinition() != null) {
                return createJaxbObjectFromElement(filter.getBeanDefinition());
            }
        }

        return null;
    }

    /**
     * Finds all bean definition elements by type in Spring application context and
     * performs unmarshalling in order to return a list of JaxB object.
     * @param configFile
     * @param type
     * @return
     */
    public <T> List<T> getBeanDefinitions(File configFile, Class<T> type) {
        return getBeanDefinitions(configFile, type, null);
    }

    /**
     * Finds all bean definition elements by type and attribute values in Spring application context and
     * performs unmarshalling in order to return a list of JaxB object.
     * @param configFile
     * @param type
     * @param attributes
     * @return
     */
    public <T> List<T> getBeanDefinitions(File configFile, Class<T> type, Map<String, String> attributes) {
        List<T> beanDefinitions = new ArrayList<T>();

        List<File> importedFiles = getConfigImports(configFile);
        for (File importLocation : importedFiles) {
            beanDefinitions.addAll(getBeanDefinitions(importLocation, type, attributes));
        }

        LSParser parser = XMLUtils.createLSParser();

        GetSpringBeansFilter filter = new GetSpringBeansFilter(type, attributes);
        parser.setFilter(filter);
        parser.parseURI(configFile.toURI().toString());

        for (Element element : filter.getBeanDefinitions()) {
            beanDefinitions.add(createJaxbObjectFromElement(element));
        }

        return beanDefinitions;
    }

    /**
     * Find all Spring bean definitions in application context for given bean type.
     * @param configFile
     * @param beanType
     * @return
     */
    public List<String> getBeanNames(File configFile, String beanType) {
        List<SpringBean> beanDefinitions = getBeanDefinitions(configFile, SpringBean.class, Collections.singletonMap("class", beanType));

        List<String> beanNames = new ArrayList<String>();
        for (SpringBean beanDefinition : beanDefinitions) {
            beanNames.add(beanDefinition.getId());
        }

        return beanNames;
    }

    /**
     * Method adds a new Spring bean definition to the XML application context file.
     * @param configFile
     * @param jaxbElement
     */
    public void addBeanDefinition(File configFile, Object jaxbElement) {
        Source xsltSource;
        Source xmlSource;
        try {
            xsltSource = new StreamSource(new ClassPathResource("transform/add-bean.xsl").getInputStream());
            xsltSource.setSystemId("add-bean");
            xmlSource = new StringSource(FileUtils.readToString(new FileInputStream(configFile)));

            //create transformer
            Transformer transformer = transformerFactory.newTransformer(xsltSource);
            transformer.setParameter("bean_content", getXmlContent(jaxbElement)
                    .replaceAll("(?m)^(.)", getTabs(1) + "$1"));

            //transform
            StringResult result = new StringResult();
            transformer.transform(xmlSource, result);
            FileUtils.writeToFile(format(result.toString()), configFile);
            return;
        } catch (IOException e) {
            throw new ApplicationRuntimeException(UNABLE_TO_READ_TRANSFORMATION_SOURCE, e);
        } catch (TransformerException e) {
            throw new ApplicationRuntimeException(FAILED_TO_UPDATE_BEAN_DEFINITION, e);
        }
    }

    /**
     * Method removes a Spring bean definition from the XML application context file. Bean definition is
     * identified by its id or bean name.
     * @param configFile
     * @param id
     */
    public void removeBeanDefinition(File configFile, String id) {
        Source xsltSource;
        Source xmlSource;
        try {
            xsltSource = new StreamSource(new ClassPathResource("transform/delete-bean.xsl").getInputStream());
            xsltSource.setSystemId("delete-bean");

            List<File> configFiles = new ArrayList<File>();
            configFiles.add(configFile);
            configFiles.addAll(getConfigImports(configFile));

            for (File file : configFiles) {
                xmlSource = new StringSource(FileUtils.readToString(new FileInputStream(configFile)));

                //create transformer
                Transformer transformer = transformerFactory.newTransformer(xsltSource);
                transformer.setParameter("bean_id", id);

                //transform
                StringResult result = new StringResult();
                transformer.transform(xmlSource, result);
                FileUtils.writeToFile(format(result.toString()), file);
                return;
            }
        } catch (IOException e) {
            throw new ApplicationRuntimeException(UNABLE_TO_READ_TRANSFORMATION_SOURCE, e);
        } catch (TransformerException e) {
            throw new ApplicationRuntimeException(FAILED_TO_UPDATE_BEAN_DEFINITION, e);
        }
    }

    /**
     * Method removes all Spring bean definitions of given type from the XML application context file.
     * @param configFile
     * @param type
     */
    public void removeBeanDefinitions(File configFile, Class<?> type) {
        Source xsltSource;
        Source xmlSource;
        try {
            xsltSource = new StreamSource(new ClassPathResource("transform/delete-bean-type.xsl").getInputStream());
            xsltSource.setSystemId("delete-bean");

            List<File> configFiles = new ArrayList<File>();
            configFiles.add(configFile);
            configFiles.addAll(getConfigImports(configFile));

            for (File file : configFiles) {
                xmlSource = new StringSource(FileUtils.readToString(new FileInputStream(configFile)));

                String beanElement = type.getAnnotation(XmlRootElement.class).name();
                String beanNamespace = type.getPackage().getAnnotation(XmlSchema.class).namespace();

                //create transformer
                Transformer transformer = transformerFactory.newTransformer(xsltSource);
                transformer.setParameter("bean_element", beanElement);
                transformer.setParameter("bean_namespace", beanNamespace);

                //transform
                StringResult result = new StringResult();
                transformer.transform(xmlSource, result);
                FileUtils.writeToFile(format(result.toString()), file);
                return;
            }
        } catch (IOException e) {
            throw new ApplicationRuntimeException(UNABLE_TO_READ_TRANSFORMATION_SOURCE, e);
        } catch (TransformerException e) {
            throw new ApplicationRuntimeException(FAILED_TO_UPDATE_BEAN_DEFINITION, e);
        }
    }

    /**
     * Method updates an existing Spring bean definition in a XML application context file. Bean definition is
     * identified by its id or bean name.
     * @param configFile
     * @param id
     * @param jaxbElement
     */
    public void updateBeanDefinition(File configFile, String id, Object jaxbElement) {
        Source xsltSource;
        Source xmlSource;
        try {
            xsltSource = new StreamSource(new ClassPathResource("transform/update-bean.xsl").getInputStream());
            xsltSource.setSystemId("update-bean");

            List<File> configFiles = new ArrayList<File>();
            configFiles.add(configFile);
            configFiles.addAll(getConfigImports(configFile));

            LSParser parser = XMLUtils.createLSParser();
            GetSpringBeanFilter getBeanFilter = new GetSpringBeanFilter(id, jaxbElement.getClass());
            parser.setFilter(getBeanFilter);

            for (File file : configFiles) {
                parser.parseURI(file.toURI().toString());
                if (getBeanFilter.getBeanDefinition() != null) {
                    xmlSource = new StringSource(FileUtils.readToString(new FileInputStream(file)));

                    //create transformer
                    Transformer transformer = transformerFactory.newTransformer(xsltSource);
                    transformer.setParameter("bean_id", id);
                    transformer.setParameter("bean_content", getXmlContent(jaxbElement)
                            .replaceAll("(?m)^(\\s<)", getTabs(1) + "$1")
                            .replaceAll("(?m)^(</)", getTabs(1) + "$1"));

                    //transform
                    StringResult result = new StringResult();
                    transformer.transform(xmlSource, result);
                    FileUtils.writeToFile(format(result.toString()), file);
                    return;
                }
            }
        } catch (IOException e) {
            throw new ApplicationRuntimeException(UNABLE_TO_READ_TRANSFORMATION_SOURCE, e);
        } catch (TransformerException e) {
            throw new ApplicationRuntimeException(FAILED_TO_UPDATE_BEAN_DEFINITION, e);
        }
    }

    /**
     * Method updates existing Spring bean definitions in a XML application context file. Bean definition is
     * identified by its type defining class.
     *
     * @param configFile
     * @param type
     * @param jaxbElement
     */
    public void updateBeanDefinitions(File configFile, Class<?> type, Object jaxbElement) {
        Source xsltSource;
        Source xmlSource;
        try {
            xsltSource = new StreamSource(new ClassPathResource("transform/update-bean-type.xsl").getInputStream());
            xsltSource.setSystemId("update-bean");

            List<File> configFiles = new ArrayList<File>();
            configFiles.add(configFile);
            configFiles.addAll(getConfigImports(configFile));

            LSParser parser = XMLUtils.createLSParser();
            GetSpringBeansFilter getBeanFilter = new GetSpringBeansFilter(type, null);
            parser.setFilter(getBeanFilter);

            for (File file : configFiles) {
                parser.parseURI(file.toURI().toString());
                if (!CollectionUtils.isEmpty(getBeanFilter.getBeanDefinitions())) {
                    xmlSource = new StringSource(FileUtils.readToString(new FileInputStream(file)));

                    String beanElement = type.getAnnotation(XmlRootElement.class).name();
                    String beanNamespace = type.getPackage().getAnnotation(XmlSchema.class).namespace();

                    //create transformer
                    Transformer transformer = transformerFactory.newTransformer(xsltSource);
                    transformer.setParameter("bean_element", beanElement);
                    transformer.setParameter("bean_namespace", beanNamespace);
                    transformer.setParameter("bean_content", getXmlContent(jaxbElement)
                            .replaceAll("(?m)^(\\s<)", getTabs(1) + "$1")
                            .replaceAll("(?m)^(</)", getTabs(1) + "$1"));

                    //transform
                    StringResult result = new StringResult();
                    transformer.transform(xmlSource, result);
                    FileUtils.writeToFile(format(result.toString()), file);
                    return;
                }
            }
        } catch (IOException e) {
            throw new ApplicationRuntimeException(UNABLE_TO_READ_TRANSFORMATION_SOURCE, e);
        } catch (TransformerException e) {
            throw new ApplicationRuntimeException(FAILED_TO_UPDATE_BEAN_DEFINITION, e);
        }
    }

    /**
     * Marshal jaxb element and try to perform basic formatting like namespace clean up
     * and attribute formatting with xsl transformation.
     * @param jaxbElement
     * @return
     */
    private String getXmlContent(Object jaxbElement) {
        StringResult jaxbContent = new StringResult();

        springBeanMarshaller.marshal(jaxbElement, jaxbContent);

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
            throw new ApplicationRuntimeException(FAILED_TO_UPDATE_BEAN_DEFINITION, e);
        }
    }

    /**
     * Do final formatting with Spring bean XML configuration content.
     * Removes empty double lines and formats schemaLocation attribute value with new lines.
     * @param content
     * @return
     */
    private String format(String content) {
        return content.replaceAll("(?m)(^\\s*$)+", "").replaceAll("\\.xsd\\s", ".xsd\n" + getTabs(3));
    }

    /**
     * Construct tabs according to project settings.
     * @return
     */
    private String getTabs(int amount) {
        StringBuilder tabs = new StringBuilder();

        for (int i = 1; i <= projectService.getActiveProject().getSettings().getTabSize(); i++) {
            for (int k = 1; k <= amount; k++) {
                tabs.append(" ");
            }
        }

        return tabs.toString();
    }

    /**
     * Creates a DOM element node from JAXB element.
     * @param element
     * @return
     */
    private <T> T createJaxbObjectFromElement(Element element) {
        LSSerializer serializer = XMLUtils.createLSSerializer();
        return (T) springBeanMarshaller.unmarshal(new StreamSource(new StringReader(serializer.writeToString(element))));
    }

    /**
     * Sets the springBeanMarshaller property.
     *
     * @param springBeanMarshaller
     */
    public void setSpringBeanMarshaller(SpringBeanMarshaller springBeanMarshaller) {
        this.springBeanMarshaller = springBeanMarshaller;
    }

    /**
     * Sets the projectService property.
     *
     * @param projectService
     */
    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }
}
