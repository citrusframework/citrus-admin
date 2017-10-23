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

package com.consol.citrus.admin.service.report.testng;

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
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.*;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author Christoph Deppisch
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TestNGTestReportLoader implements TestReportLoader {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(TestNGTestReportLoader.class);

    /** Date format */
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

    @Autowired
    private TestCaseService testCaseService;

    @Override
    public TestReport getLatest(Project activeProject, Test test) {
        TestReport report = new TestReport();

        if (hasTestResults(activeProject)) {
            try {
                Document testResults = XMLUtils.parseMessagePayload(getTestResultsAsString(activeProject));
                Node testClass = XPathUtils.evaluateAsNode(testResults, "/testng-results/suite[1]/test/class[@name = '" + test.getPackageName() + "." + test.getClassName() + "']", null);
                Element testMethod = (Element) XPathUtils.evaluateAsNode(testClass, "test-method[@name='" + test.getMethodName() + "']", null);

                TestResult result = getResult(test, testMethod);
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
                report.setSuiteName(XPathUtils.evaluateAsString(testResults, "/testng-results/suite[1]/@name", null));
                report.setDuration(Long.valueOf(XPathUtils.evaluateAsString(testResults, "/testng-results/suite[1]/@duration-ms", null)));

                try {
                    report.setExecutionDate(dateFormat.parse(XPathUtils.evaluateAsString(testResults, "/testng-results/suite[1]/@started-at", null)));
                } catch (ParseException e) {
                    log.warn("Unable to read test execution time", e);
                }

                report.setPassed(Long.valueOf(XPathUtils.evaluateAsString(testResults, "/testng-results/@passed", null)));
                report.setFailed(Long.valueOf(XPathUtils.evaluateAsString(testResults, "/testng-results/@failed", null)));
                report.setSkipped(Long.valueOf(XPathUtils.evaluateAsString(testResults, "/testng-results/@skipped", null)));
                report.setTotal(report.getPassed() + report.getFailed() + report.getSkipped());

                NodeList testClasses = XPathUtils.evaluateAsNodeList(testResults, "testng-results/suite[1]/test/class", null);
                for (int i = 0; i < testClasses.getLength(); i++) {
                    Element testClass = (Element) testClasses.item(i);

                    List<Element> testMethods = DomUtils.getChildElementsByTagName(testClass, "test-method");
                    for (Element testMethod : testMethods) {
                        if (!testMethod.hasAttribute("is-config") || testMethod.getAttribute("is-config").equals("false")) {
                            String packageName = testClass.getAttribute("name").substring(0, testClass.getAttribute("name").lastIndexOf('.'));
                            String className = testClass.getAttribute("name").substring(packageName.length() + 1);
                            String methodName = testMethod.getAttribute("name");

                            Test test = testCaseService.findTest(activeProject, packageName, className, methodName);
                            TestResult result = getResult(test, testMethod);
                            report.getResults().add(result);
                        }
                    }
                }
            } catch (IOException e) {
                log.error("Failed to read test results file", e);
            }
        }

        return report;
    }

    /**
     * Fills result object with test method information.
     * @param test
     * @param testMethod
     * @return
     */
    private TestResult getResult(Test test, Element testMethod) {
        TestResult result = new TestResult();
        result.setTest(test);
        result.setSuccess(testMethod.getAttribute("status").equals("PASS"));

        Element exceptionElement = DomUtils.getChildElementByTagName(testMethod, "exception");
        if (exceptionElement != null) {
            Element messageElement = DomUtils.getChildElementByTagName(exceptionElement, "message");
            if (messageElement != null) {
                result.setErrorMessage(DomUtils.getTextValue(messageElement).trim());
            }

            result.setErrorCause(exceptionElement.getAttribute("class"));

            Element stackTraceElement = DomUtils.getChildElementByTagName(exceptionElement, "full-stacktrace");
            if (stackTraceElement != null) {
                result.setStackTrace(DomUtils.getTextValue(stackTraceElement).trim());
            }
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
     * @throws IOException
     */
    private String getTestResultsAsString(Project activeProject) throws IOException {
        Resource fileResource = getTestResultsFile(activeProject);
        try (InputStream fileIn = fileResource.getInputStream()) {
            return FileUtils.readToString(fileIn);
        }
    }

    /**
     * Access file resource representing the TestNG results file.
     * @param activeProject
     * @return
     */
    private Resource getTestResultsFile(Project activeProject) {
        return new FileSystemResource(activeProject.getProjectHome() + "/target/failsafe-reports/testng-results.xml");
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
