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
import org.springframework.core.io.ClassPathResource;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Christoph Deppisch
 */
public class JUnitTestReportServiceTest {

    private JUnitTestReportService service = new JUnitTestReportService();

    @Test
    public void testReport() throws Exception {
        Project project = new Project(new ClassPathResource("projects/maven").getFile().getCanonicalPath());

        Assert.assertTrue(service.hasTestResults(project));

        TestReport report = service.getLatest(project);
        Assert.assertEquals(report.getProjectName(), project.getName());
        Assert.assertEquals(report.getSuiteName(), "Sample test suite");
        Assert.assertEquals(report.getDuration(), 9000L);
        Assert.assertEquals(report.getTotal(), 16L);
        Assert.assertEquals(report.getPassed(), 10L);
        Assert.assertEquals(report.getFailed(), 5L);
        Assert.assertEquals(report.getSkipped(), 1L);

        Assert.assertEquals(report.getResults().size(), 3L);

        TestResult testResult = report.getResults().get(0);
        Assert.assertEquals(testResult.getTest().getClassName(), "com.consol.citrus.samples.Test_1_IT");
        Assert.assertEquals(testResult.getTest().getName(), "Test_1_IT.test_1");
        Assert.assertEquals(testResult.getTest().getMethodName(), "test_1");
        Assert.assertEquals(testResult.getTest().getPackageName(), "com.consol.citrus.samples");
        Assert.assertTrue(testResult.isSuccess());
        Assert.assertNull(testResult.getErrorCause());

        testResult = report.getResults().get(1);
        Assert.assertEquals(testResult.getTest().getClassName(), "com.consol.citrus.samples.Test_2_IT");
        Assert.assertEquals(testResult.getTest().getName(), "Test_2_IT.test_2");
        Assert.assertEquals(testResult.getTest().getMethodName(), "test_2");
        Assert.assertEquals(testResult.getTest().getPackageName(), "com.consol.citrus.samples");
        Assert.assertTrue(testResult.isSuccess());
        Assert.assertNull(testResult.getErrorCause());

        testResult = report.getResults().get(2);
        Assert.assertEquals(testResult.getTest().getClassName(), "com.consol.citrus.samples.Test_3_IT");
        Assert.assertEquals(testResult.getTest().getName(), "Test_3_IT.test_3");
        Assert.assertEquals(testResult.getTest().getMethodName(), "test_3");
        Assert.assertEquals(testResult.getTest().getPackageName(), "com.consol.citrus.samples");
        Assert.assertFalse(testResult.isSuccess());
        Assert.assertEquals(testResult.getErrorCause(), "com.consol.citrus.exceptions.TestCaseFailedException");
        Assert.assertEquals(testResult.getErrorMessage(), "Test case failed");
        Assert.assertNotNull(testResult.getStackTrace());
    }
}
