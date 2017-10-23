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

package com.consol.citrus.admin.service.report.junit;

import com.consol.citrus.admin.model.*;
import com.consol.citrus.admin.service.TestCaseService;
import com.consol.citrus.admin.service.report.TestReportLoader;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.util.FileUtils;
import com.consol.citrus.util.XMLUtils;
import com.consol.citrus.xml.xpath.XPathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.*;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * @author Christoph Deppisch
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class JUnit4TestReportLoader implements TestReportLoader {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(JUnit4TestReportLoader.class);

    @Autowired
    private TestCaseService testCaseService;

    @Override
    public TestReport getLatest(Project activeProject, Test test) {
        TestReport report = new TestReport();

        if (hasTestResults(activeProject)) {
            try {
                Document testResults = XMLUtils.parseMessagePayload(getTestResultsAsString(activeProject));
                Element testCase = (Element) XPathUtils.evaluateAsNode(testResults, "/testsuite/testcase[@classname = '" + test.getPackageName() + "." + test.getClassName() + "']", null);

                TestResult result = getResult(test, testCase);
                report.setTotal(1);
                if (result.isSuccess()) {
                    report.setPassed(1L);
                } else {
                    report.setFailed(1L);
                }

                report.getResults().add(result);
            } catch (CitrusRuntimeException e) {
                log.warn("No results found for test: " + test.getPackageName() + "." + test.getClassName() + "#" + test.getMethodName());
            } catch (IOException e) {
                log.error("Failed to read test results file", e);
            }
        }

        return report;
    }

    @Override
    public TestReport getLatest(Project activeProject) {
        TestReport report = new TestReport();

        if (hasTestResults(activeProject)) {
            try {
                Document testResults = XMLUtils.parseMessagePayload(getTestResultsAsString(activeProject));
                report.setProjectName(activeProject.getName());
                report.setSuiteName(XPathUtils.evaluateAsString(testResults, "/testsuite/@name", null));
                report.setDuration(Math.round(Double.valueOf(XPathUtils.evaluateAsString(testResults, "/testsuite/@time", null)) * 1000));

                report.setFailed(Long.valueOf(XPathUtils.evaluateAsString(testResults, "/testsuite/@failures", null)));
                report.setSkipped(Long.valueOf(XPathUtils.evaluateAsString(testResults, "/testsuite/@skipped", null)));
                report.setTotal(Long.valueOf(XPathUtils.evaluateAsString(testResults, "/testsuite/@tests", null)));
                report.setPassed(report.getTotal() - report.getSkipped() - report.getFailed());

                NodeList testCases = XPathUtils.evaluateAsNodeList(testResults, "/testsuite/testcase", null);
                for (int i = 0; i < testCases.getLength(); i++) {
                    Element testCase = (Element) testCases.item(i);

                    String className = testCase.getAttribute("classname");
                    String methodName = testCase.getAttribute("name");
                    String packageName;
                    if (className.indexOf(':') > 0 || className.indexOf(' ') > 0) {
                        // Cucumber BDD test
                        packageName = report.getSuiteName().substring(0, report.getSuiteName().lastIndexOf('.'));

                        String classFileName = report.getSuiteName().substring(packageName.length() + 1);
                        Test test = testCaseService.findTest(activeProject, packageName, classFileName);
                        TestResult result = getResult(test, testCase);
                        
                        result.getTest().setName(className + " - " + methodName);

                        report.getResults().add(result);
                    } else if (className.indexOf('.') > 0) {
                        packageName = className.substring(0, className.lastIndexOf('.'));
                        className = className.substring(packageName.length() + 1);

                        Test test = testCaseService.findTest(activeProject, packageName, className, methodName);
                        TestResult result = getResult(test, testCase);
                        report.getResults().add(result);
                    }
                }
            } catch (IOException e) {
                log.error("Failed to read test results file", e);
            }
        }

        return report;
    }

    /**
     * Fills result object with test case information.
     * @param test
     * @param testCase
     * @return
     */
    private TestResult getResult(Test test, Element testCase) {
        TestResult result = new TestResult();
        result.setTest(test);

        Element failureElement = DomUtils.getChildElementByTagName(testCase, "failure");
        if (failureElement != null) {
            result.setSuccess(false);
            result.setErrorMessage(failureElement.getAttribute("message"));
            result.setErrorCause(failureElement.getAttribute("type"));
            result.setStackTrace(DomUtils.getTextValue(failureElement).trim());
        } else {
            result.setSuccess(true);
        }

        return result;
    }

    @Override
    public boolean hasTestResults(Project activeProject) {
        return getTestResultsFile(activeProject).exists();
    }

    /**
     * Reads test results file content.
     * @return
     * @throws java.io.IOException
     */
    private String getTestResultsAsString(Project activeProject) throws IOException {
        return FileUtils.readToString(getTestResultsFile(activeProject));
    }

    /**
     * Access file resource representing the TestNG results file.
     * @param activeProject
     * @return
     */
    private Resource getTestResultsFile(Project activeProject) {
        FileSystemResource testSuiteFile = new FileSystemResource(activeProject.getProjectHome() + "/target/failsafe-reports/TEST-TestSuite.xml");
        if (testSuiteFile.exists()) {
            return testSuiteFile;
        }

        List<File> testCaseFiles = FileUtils.findFiles(activeProject.getProjectHome() + "/target/failsafe-reports", Collections.singleton("/TEST-*.xml"));
        if (!CollectionUtils.isEmpty(testCaseFiles)) {
            return new FileSystemResource(testCaseFiles.get(0));
        }

        return testSuiteFile;
    }

    /**
     * Sets the testCaseService.
     *
     * @param testCaseService
     */
    public void setTestCaseService(TestCaseService testCaseService) {
        this.testCaseService = testCaseService;
    }
}
