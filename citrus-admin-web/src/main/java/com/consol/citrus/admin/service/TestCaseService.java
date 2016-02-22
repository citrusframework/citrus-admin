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

package com.consol.citrus.admin.service;

import com.consol.citrus.admin.converter.action.ActionConverter;
import com.consol.citrus.admin.converter.action.TestActionConverter;
import com.consol.citrus.admin.exception.ApplicationRuntimeException;
import com.consol.citrus.admin.marshal.XmlTestMarshaller;
import com.consol.citrus.admin.model.*;
import com.consol.citrus.admin.model.spring.SpringBeans;
import com.consol.citrus.model.testcase.core.TestcaseDefinition;
import com.consol.citrus.model.testcase.core.VariablesDefinition;
import com.consol.citrus.util.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.xml.transform.StringSource;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.*;
import java.util.*;

/**
 * @author Christoph Deppisch
 */
@Service
public class TestCaseService {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(TestCaseService.class);

    @Autowired
    private List<TestActionConverter<?, ? extends com.consol.citrus.TestAction>> actionConverter;

    /**
     * Lists all available Citrus test cases grouped in test packages.
     * @param project
     * @return
     */
    public List<TestPackage> getTestPackages(Project project) {
        Map<String, TestPackage> testPackages = new HashMap<>();
        List<Test> tests = new ArrayList<>();

        List<File> testFiles = FileUtils.getTestFiles(getTestDirectory(project));
        for (File file : testFiles) {
            String testName = FilenameUtils.getBaseName(file.getName());
            String testPackageName = file.getPath().substring(getTestDirectory(project).length(), file.getPath().length() - file.getName().length())
                    .replace(File.separatorChar, '.');

            if (testPackageName.endsWith(".")) {
                testPackageName = testPackageName.substring(0, testPackageName.length() - 1);
            }

            Test test = new Test();
            test.setType(TestType.XML);
            test.setName(testName);
            test.setPackageName(testPackageName);

            tests.add(test);
        }

        //TODO load Java tests

        for (Test test : tests) {
            if (!testPackages.containsKey(test.getPackageName())) {
                TestPackage testPackage = new TestPackage();
                testPackage.setName(test.getPackageName());
                testPackages.put(test.getPackageName(), testPackage);
            }

            testPackages.get(test.getPackageName()).getTests().add(test);
        }

        return Arrays.asList(testPackages.values().toArray(new TestPackage[testPackages.size()]));
    }

    /**
     * Gets test case details such as status, description, author.
     * @return
     */
    public TestDetail getTestDetail(Project project, String packageName, String testName, TestType type) {
        TestDetail testDetail = new TestDetail();
        testDetail.setName(testName);
        testDetail.setPackageName(packageName);
        testDetail.setType(type);

        TestcaseDefinition testModel = getTestModel(project, packageName, testName, type);

        if (testModel.getVariables() != null) {
            for (VariablesDefinition.Variable variable : testModel.getVariables().getVariables()) {
                testDetail.getVariables().put(variable.getName(), variable.getValue());
            }
        }

        testDetail.setDescription(testModel.getDescription().trim().replaceAll(" +", " ").replaceAll("\t", ""));
        testDetail.setAuthor(testModel.getMetaInfo().getAuthor());
        testDetail.setLastModified(testModel.getMetaInfo().getLastUpdatedOn().getTimeInMillis());

        testDetail.setFile(getTestDirectory(project) + packageName.replace('.', File.separatorChar) + File.separator + FilenameUtils.getBaseName(testName));

        for (Object actionType : testModel.getActions().getActionsAndSendsAndReceives()) {
            TestAction model = null;
            for (TestActionConverter converter : actionConverter) {
                if (converter.getModelClass().isInstance(actionType)) {
                    model = converter.convert(actionType);
                    break;
                }
            }

            if (model == null) {
                model = new ActionConverter(actionType.getClass().getAnnotation(XmlRootElement.class).name()).convert(actionType);
            }

            testDetail.getActions().add(model);
        }

        return testDetail;
    }

    /**
     * Gets the source code for the given test.
     * @param project
     * @param packageName
     * @param name
     * @param type
     * @return
     */
    public String getSourceCode(Project project, String packageName, String name, TestType type) {
        String dir = type.equals(TestType.JAVA) ? getJavaDirectory(project) : getTestDirectory(project);

        try {
            String sourceFilePath = dir + packageName.replace('.', File.separatorChar) + File.separator + name + "." + type.name().toLowerCase();

            if (new File(sourceFilePath).exists()) {
                return FileUtils.readToString(new FileInputStream(sourceFilePath));
            } else {
                log.warn("Unable to find source code for path: " + sourceFilePath);
                return "No sources available!";
            }
        } catch (IOException e) {
            throw new ApplicationRuntimeException("Failed to load test case source code", e);
        }
    }

    /**
     * Reads either XML or Java test definition to model class.
     * @param project
     * @param packageName
     * @param testName
     * @param type
     * @return
     */
    private TestcaseDefinition getTestModel(Project project, String packageName, String testName, TestType type) {
        if (type.equals(TestType.XML)) {
            return getXmlTestModel(project, packageName, testName);
        } else if (type.equals(TestType.JAVA)) {
            return getJavaTestModel(packageName, testName);
        } else {
            throw new ApplicationRuntimeException("Unsupported test case type: " + type);
        }
    }

    /**
     * Get test case model from XML source code.
     * @param packageName
     * @param testName
     * @return
     */
    private TestcaseDefinition getXmlTestModel(Project project, String packageName, String testName) {
        String xmlSource = getSourceCode(project, packageName, testName, TestType.XML);

        if (!StringUtils.hasText(xmlSource)) {
            throw new ApplicationRuntimeException("Failed to get XML source code for test: " + packageName + "." + testName);
        }

        return ((SpringBeans) new XmlTestMarshaller().unmarshal(new StringSource(xmlSource))).getTestcase();
    }

    /**
     * Get test case model from Java source code.
     * @param packageName
     * @param testName
     * @return
     */
    private TestcaseDefinition getJavaTestModel(String packageName, String testName) {
        String methodName = null;
        String testClassName;

        int methodSeparatorIndex = testName.indexOf('.');
        if (methodSeparatorIndex > 0) {
            methodName = testName.substring(methodSeparatorIndex + 1);
            testClassName = testName.substring(0, methodSeparatorIndex);
        } else {
            testClassName = testName;
        }

        //TODO load Java test logic into model

        TestcaseDefinition testModel = new TestcaseDefinition();
        testModel.setName(StringUtils.hasText(methodName) ? methodName : testClassName);
        return testModel;
    }

    /**
     * Gets the current test directory based on project home and default test directory.
     * @return
     */
    private String getTestDirectory(Project project) {
        return new File(project.getProjectHome()).getAbsolutePath() + File.separator + project.getSrcDirectory() + "resources" + File.separator;
    }

    /**
     * Gets the current test directory based on project home and default test directory.
     * @return
     */
    private String getJavaDirectory(Project project) {
        return new File(project.getProjectHome()).getAbsolutePath() + File.separator + project.getSrcDirectory() + "java" + File.separator;
    }
}
