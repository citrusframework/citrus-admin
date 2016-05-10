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

import com.consol.citrus.TestCase;
import com.consol.citrus.admin.converter.action.ActionConverter;
import com.consol.citrus.admin.converter.action.TestActionConverter;
import com.consol.citrus.admin.exception.ApplicationRuntimeException;
import com.consol.citrus.admin.marshal.XmlTestMarshaller;
import com.consol.citrus.admin.mock.Mocks;
import com.consol.citrus.admin.model.*;
import com.consol.citrus.admin.model.spring.SpringBeans;
import com.consol.citrus.dsl.junit.JUnit4CitrusTestDesigner;
import com.consol.citrus.dsl.junit.JUnit4CitrusTestRunner;
import com.consol.citrus.dsl.testng.TestNGCitrusTestDesigner;
import com.consol.citrus.dsl.testng.TestNGCitrusTestRunner;
import com.consol.citrus.model.testcase.core.*;
import com.consol.citrus.util.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.xml.transform.StringSource;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        List<File> sourceFiles = FileUtils.findFiles(getJavaDirectory(project), StringUtils.commaDelimitedListToSet(project.getSettings().getJavaFilePattern()));
        for (File sourceFile : sourceFiles) {
            String className = FilenameUtils.getBaseName(sourceFile.getName());
            String testPackageName = sourceFile.getPath().substring(getJavaDirectory(project).length(), sourceFile.getPath().length() - sourceFile.getName().length())
                    .replace(File.separatorChar, '.');

            if (testPackageName.endsWith(".")) {
                testPackageName = testPackageName.substring(0, testPackageName.length() - 1);
            }

            tests.addAll(findTests(sourceFile, testPackageName, className));
        }

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
     * Find all tests in give source file.
     * @param sourceFile
     * @param packageName
     * @param className
     * @return
     */
    private List<Test> findTests(File sourceFile, String packageName, String className) {
        List<Test> tests = new ArrayList<>();

        try {
            String sourceCode = FileUtils.readToString(new FileSystemResource(sourceFile));

            Matcher matcher = Pattern.compile("@CitrusTest").matcher(sourceCode);
            while (matcher.find()) {
                Test test = new Test();
                test.setType(TestType.JAVA);
                test.setClassName(className);
                test.setPackageName(packageName);

                String snippet = StringUtils.trimAllWhitespace(sourceCode.substring(matcher.start(), sourceCode.indexOf('{', matcher.start())));
                String methodName = snippet.substring(snippet.indexOf("publicvoid") + 10);
                methodName = methodName.substring(0, methodName.indexOf("("));
                test.setMethodName(methodName);

                if (snippet.contains("@CitrusTest(name=")) {
                    String explicitName = snippet.substring(snippet.indexOf("name=\"") + 6);
                    explicitName = explicitName.substring(0, explicitName.indexOf("\""));
                    test.setName(explicitName);
                } else {
                    test.setName(className + "." + methodName);
                }

                tests.add(test);
            }

            matcher = Pattern.compile("@CitrusXmlTest").matcher(sourceCode);
            while (matcher.find()) {
                Test test = new Test();
                test.setType(TestType.XML);
                test.setClassName(className);
                test.setPackageName(packageName);

                String snippet = StringUtils.trimAllWhitespace(sourceCode.substring(matcher.start(), sourceCode.indexOf('{', matcher.start())));
                String methodName = snippet.substring(snippet.indexOf("publicvoid") + 10);
                methodName = methodName.substring(0, methodName.indexOf("("));
                test.setMethodName(methodName);

                if (snippet.contains("@CitrusXmlTest(name=\"")) {
                    String explicitName = snippet.substring(snippet.indexOf("name=\"") + 6);
                    explicitName = explicitName.substring(0, explicitName.indexOf("\""));
                    test.setName(explicitName);
                } else if (snippet.contains("@CitrusXmlTest(name={\"")) {
                    String explicitName = snippet.substring(snippet.indexOf("name={\"") + 7);
                    explicitName = explicitName.substring(0, explicitName.indexOf("\""));
                    test.setName(explicitName);
                } else {
                    test.setName(methodName);
                }

                tests.add(test);
            }
        } catch (IOException e) {
            log.error("Failed to read test source file", e);
        }

        return tests;
    }

    /**
     * Gets test case details such as status, description, author.
     * @param project
     * @param test
     * @return
     */
    public TestDetail getTestDetail(Project project, Test test) {
        TestDetail testDetail = new TestDetail(test);
        TestcaseModel testModel = getTestModel(project, testDetail);

        if (testModel.getVariables() != null) {
            for (VariablesModel.Variable variable : testModel.getVariables().getVariables()) {
                testDetail.getVariables().put(variable.getName(), variable.getValue());
            }
        }

        if (testModel.getDescription() != null) {
            testDetail.setDescription(testModel.getDescription().trim().replaceAll(" +", " ").replaceAll("\t", ""));
        }

        if (testModel.getMetaInfo() != null) {
            testDetail.setAuthor(testModel.getMetaInfo().getAuthor());
            testDetail.setLastModified(testModel.getMetaInfo().getLastUpdatedOn().getTimeInMillis());
        }

        if (test.getType().equals(TestType.JAVA)) {
            testDetail.setFile(getJavaDirectory(project) + test.getPackageName().replace('.', File.separatorChar) + File.separator + test.getClassName());
        } else {
            testDetail.setFile(getTestDirectory(project) + test.getPackageName().replace('.', File.separatorChar) + File.separator + FilenameUtils.getBaseName(test.getName()));
        }

        if (testModel.getActions() != null) {
            for (Object actionType : testModel.getActions().getActionsAndSendsAndReceives()) {
                TestAction model = null;
                for (TestActionConverter converter : actionConverter) {
                    if (converter.getSourceModelClass().isInstance(actionType)) {
                        model = converter.convert(actionType);
                        break;
                    }
                }

                if (model == null) {
                    if (actionType.getClass().getAnnotation(XmlRootElement.class) == null) {
                        log.info(actionType.getClass().getName());
                    } else {
                        model = new ActionConverter(actionType.getClass().getAnnotation(XmlRootElement.class).name()).convert(actionType);
                    }
                }

                if (model != null) {
                    testDetail.getActions().add(model);
                }
            }
        }

        return testDetail;
    }

    /**
     * Gets the source code for the given test.
     * @param project
     * @param detail
     * @param type
     * @return
     */
    public String getSourceCode(Project project, TestDetail detail, TestType type) {
        String sourceFilePath;
        if (type.equals(TestType.JAVA)) {
            sourceFilePath = getJavaDirectory(project) + detail.getPackageName().replace('.', File.separatorChar) + File.separator + detail.getClassName() + ".java";
        } else {
            sourceFilePath = getTestDirectory(project) + detail.getPackageName().replace('.', File.separatorChar) + File.separator + detail.getName() + ".xml";
        }

        try {
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
     * @return
     */
    private TestcaseModel getTestModel(Project project, TestDetail detail) {
        if (detail.getType().equals(TestType.XML)) {
            return getXmlTestModel(project, detail);
        } else if (detail.getType().equals(TestType.JAVA)) {
            return getJavaTestModel(project, detail);
        } else {
            throw new ApplicationRuntimeException("Unsupported test case type: " + detail.getType());
        }
    }

    /**
     * Get test case model from XML source code.
     * @param project
     * @param detail
     * @return
     */
    private TestcaseModel getXmlTestModel(Project project, TestDetail detail) {
        String xmlSource = getSourceCode(project, detail, TestType.XML);

        if (!StringUtils.hasText(xmlSource)) {
            throw new ApplicationRuntimeException("Failed to get XML source code for test: " + detail.getPackageName() + "." + detail.getName());
        }

        return ((SpringBeans) new XmlTestMarshaller().unmarshal(new StringSource(xmlSource))).getTestcase();
    }

    /**
     * Get test case model from Java source code.
     * @param project
     * @param detail
     * @return
     */
    private TestcaseModel getJavaTestModel(Project project, TestDetail detail) {
        if (project.isMavenProject()) {
            try {
                ClassLoader classLoader = URLClassLoader.newInstance(new URL[]{
                        new FileSystemResource(project.getProjectHome() + File.separator + "target" + File.separator + "classes").getURL(),
                        new FileSystemResource(project.getProjectHome() + File.separator + "target" + File.separator + "test-classes").getURL()
                });

                Class testClass = classLoader.loadClass(detail.getPackageName() + "." + detail.getClassName());

                if (TestNGCitrusTestDesigner.class.isAssignableFrom(testClass)) {
                    TestNGCitrusTestDesigner testInstance = (TestNGCitrusTestDesigner) testClass.newInstance();
                    Method testMethod = ReflectionUtils.findMethod(testClass, detail.getMethodName());
                    testInstance.setApplicationContext(Mocks.getApplicationContextMock());
                    testInstance.simulate(testMethod, Mocks.getTestContextMock());
                    testMethod.invoke(testInstance);

                    return getTestcaseModel(testInstance.getTestCase());
                } else if (TestNGCitrusTestRunner.class.isAssignableFrom(testClass)) {
                    TestNGCitrusTestRunner testInstance = (TestNGCitrusTestRunner) testClass.newInstance();
                    Method testMethod = ReflectionUtils.findMethod(testClass, detail.getMethodName());
                    testInstance.setApplicationContext(Mocks.getApplicationContextMock());
                    testInstance.simulate(testMethod, Mocks.getTestContextMock());
                    testMethod.invoke(testInstance);

                    return getTestcaseModel(testInstance.getTestCase());
                } else if (JUnit4CitrusTestDesigner.class.isAssignableFrom(testClass)) {
                    JUnit4CitrusTestDesigner testInstance = (JUnit4CitrusTestDesigner) testClass.newInstance();
                    Method testMethod = ReflectionUtils.findMethod(testClass, detail.getMethodName());
                    testInstance.setApplicationContext(Mocks.getApplicationContextMock());
                    testInstance.simulate(testMethod, Mocks.getTestContextMock());
                    testMethod.invoke(testInstance);

                    return getTestcaseModel(testInstance.getTestCase());
                } else if (JUnit4CitrusTestRunner.class.isAssignableFrom(testClass)) {
                    JUnit4CitrusTestRunner testInstance = (JUnit4CitrusTestRunner) testClass.newInstance();
                    Method testMethod = ReflectionUtils.findMethod(testClass, detail.getMethodName());
                    testInstance.setApplicationContext(Mocks.getApplicationContextMock());
                    testInstance.simulate(testMethod, Mocks.getTestContextMock());
                    testMethod.invoke(testInstance);

                    return getTestcaseModel(testInstance.getTestCase());
                } else {
                    throw new ApplicationRuntimeException("Unsupported test case type: " + testClass);
                }
            } catch (MalformedURLException e) {
                throw new ApplicationRuntimeException("Failed to access Java classes output folder", e);
            } catch (ClassNotFoundException e) {
                throw new ApplicationRuntimeException("Failed to load Java test class", e);
            } catch (IOException e) {
                throw new ApplicationRuntimeException("Failed to access project output folder", e);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new ApplicationRuntimeException("Failed to create test class instance", e);
            } catch (InvocationTargetException e) {
                throw new ApplicationRuntimeException("Failed to invoke test method", e);
            }
        }

        TestcaseModel testModel = new TestcaseModel();
        testModel.setName(detail.getClassName() + "." + detail.getMethodName());
        return testModel;
    }

    private TestcaseModel getTestcaseModel(TestCase testCase) {
        TestcaseModel testModel = new TestcaseModel();
        testModel.setName(testCase.getName());

        VariablesModel variablesModel = new VariablesModel();
        for (Map.Entry<String, Object> entry : testCase.getVariableDefinitions().entrySet()) {
            VariablesModel.Variable variable = new VariablesModel.Variable();
            variable.setName(entry.getKey());
            variable.setValue(entry.getValue().toString());
            variablesModel.getVariables().add(variable);
        }
        testModel.setVariables(variablesModel);

        TestActionsType actions = new TestActionsType();
        for (com.consol.citrus.TestAction action : testCase.getActions()) {
            actions.getActionsAndSendsAndReceives().add(getActionModel(action));
        }

        testModel.setActions(actions);
        return testModel;
    }

    private Object getActionModel(com.consol.citrus.TestAction action) {
        for (TestActionConverter converter : actionConverter) {
            if (converter.getActionModelClass().isInstance(action)) {
                return converter.convertModel(action);
            }
        }

        return new ActionConverter(action.getName()).convertModel(action);
    }

    /**
     * Gets the current test directory based on project home and default test directory.
     * @return
     */
    private String getTestDirectory(Project project) {
        return new File(project.getProjectHome()).getAbsolutePath() + File.separator +
                project.getSettings().getXmlSrcDirectory();
    }

    /**
     * Gets the current test directory based on project home and default test directory.
     * @return
     */
    private String getJavaDirectory(Project project) {
        return new File(project.getProjectHome()).getAbsolutePath() + File.separator +
                project.getSettings().getJavaSrcDirectory();
    }
}
