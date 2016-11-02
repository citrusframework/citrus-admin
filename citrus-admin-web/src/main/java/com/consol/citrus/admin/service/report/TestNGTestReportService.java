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
import com.consol.citrus.util.FileUtils;
import com.consol.citrus.util.XMLUtils;
import com.consol.citrus.xml.xpath.XPathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.*;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @author Christoph Deppisch
 */
@Service
public class TestNGTestReportService implements TestReportService {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(TestNGTestReportService.class);

    /** Date format */
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

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
                report.setTotal(Long.valueOf(XPathUtils.evaluateAsString(testResults, "/testng-results/@total", null)));

                NodeList testClasses = XPathUtils.evaluateAsNodeList(testResults, "testng-results/suite[1]/test/class", null);
                for (int i = 0; i < testClasses.getLength(); i++) {
                    Element testClass = (Element) testClasses.item(i);

                    List<Element> testMethods = DomUtils.getChildElementsByTagName(testClass, "test-method");
                    for (Element testMethod : testMethods) {
                        if (!testMethod.hasAttribute("is-config") || testMethod.getAttribute("is-config").equals("false")) {
                            TestResult result = new TestResult();
                            Test test = new Test();

                            test.setClassName(testClass.getAttribute("name"));
                            test.setMethodName(testMethod.getAttribute("name"));
                            test.setPackageName(test.getClassName().substring(0, test.getClassName().lastIndexOf('.')));
                            test.setName(test.getClassName().substring(test.getClassName().lastIndexOf('.') + 1) + "." + test.getMethodName());
                            test.setType(TestType.JAVA);

                            result.setTest(test);
                            result.setSuccess(testMethod.getAttribute("status").equals("PASS"));
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
        return FileUtils.readToString(getTestResultsFile(activeProject));
    }

    /**
     * Access file resource representing the TestNG results file.
     * @param activeProject
     * @return
     */
    private Resource getTestResultsFile(Project activeProject) {
        return new FileSystemResource(activeProject.getProjectHome() + "/target/failsafe-reports/testng-results.xml");
    }
}
