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

package com.consol.citrus.admin.service.report;

import com.consol.citrus.admin.model.*;
import com.consol.citrus.admin.service.TestCaseService;
import com.consol.citrus.util.FileUtils;
import com.consol.citrus.util.XMLUtils;
import com.consol.citrus.xml.xpath.XPathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.*;

import java.io.IOException;

/**
 * @author Christoph Deppisch
 */
@Service
public class JUnitTestReportService implements TestReportService {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(JUnitTestReportService.class);

    @Autowired
    private TestCaseService testCaseService;

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
                    TestResult result = new TestResult();
                    String packageName = testCase.getAttribute("classname").substring(0, testCase.getAttribute("classname").lastIndexOf('.'));
                    String className = testCase.getAttribute("classname").substring(packageName.length() + 1);
                    String methodName = testCase.getAttribute("name");

                    Test test = testCaseService.findTest(activeProject, packageName, className, methodName);
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

                    report.getResults().add(result);
                }
            } catch (IOException e) {
                log.error("Failed to read test results file", e);
            }
        }

        return report;
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
        return new FileSystemResource(activeProject.getProjectHome() + "/target/failsafe-reports/TEST-TestSuite.xml");
    }
}
