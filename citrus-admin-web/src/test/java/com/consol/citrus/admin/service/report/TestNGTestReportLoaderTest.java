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

package com.consol.citrus.admin.service.report;

import com.consol.citrus.admin.model.*;
import com.consol.citrus.admin.service.TestCaseService;
import com.consol.citrus.admin.service.report.testng.TestNGTestReportLoader;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassPathResource;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.mockito.Mockito.when;

/**
 * @author Christoph Deppisch
 */
public class TestNGTestReportLoaderTest {

    private TestNGTestReportLoader service = new TestNGTestReportLoader();

    @Mock
    private TestCaseService testCaseService;

    private Project project;

    private com.consol.citrus.admin.model.Test test1 = new com.consol.citrus.admin.model.Test("com.consol.citrus.samples", "Test_1_IT", "test_1", "Test_1_IT.test_1", TestType.JAVA);
    private com.consol.citrus.admin.model.Test test2 = new com.consol.citrus.admin.model.Test("com.consol.citrus.samples", "Test_2_IT", "test_2", "Test_2_IT.test_2", TestType.JAVA);
    private com.consol.citrus.admin.model.Test test3 = new com.consol.citrus.admin.model.Test("com.consol.citrus.samples", "Test_3_IT", "test_3", "Test_3_IT.test_3", TestType.JAVA);

    @BeforeClass
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        service.setTestCaseService(testCaseService);

        project = new Project(new ClassPathResource("projects/maven").getFile().getCanonicalPath());

        when(testCaseService.findTest(project, "com.consol.citrus.samples", "Test_1_IT", "test_1")).thenReturn(test1);
        when(testCaseService.findTest(project, "com.consol.citrus.samples", "Test_2_IT", "test_2")).thenReturn(test2);
        when(testCaseService.findTest(project, "com.consol.citrus.samples", "Test_3_IT", "test_3")).thenReturn(test3);
    }

    @Test
    public void testReport() throws Exception {
        Assert.assertTrue(service.hasTestResults(project));

        TestReport report = service.getLatest(project);
        Assert.assertEquals(report.getProjectName(), project.getName());
        Assert.assertEquals(report.getSuiteName(), "Sample test suite");
        Assert.assertEquals(report.getDuration(), 9000L);
        Assert.assertEquals(report.getExecutionDate().getTime(), 1451602800000L);
        Assert.assertEquals(report.getTotal(), 16L);
        Assert.assertEquals(report.getPassed(), 10L);
        Assert.assertEquals(report.getFailed(), 5L);
        Assert.assertEquals(report.getSkipped(), 1L);

        Assert.assertEquals(report.getResults().size(), 3L);

        TestResult testResult = report.getResults().get(0);
        Assert.assertEquals(testResult.getTest().getClassName(), "Test_1_IT");
        Assert.assertEquals(testResult.getTest().getName(), "Test_1_IT.test_1");
        Assert.assertEquals(testResult.getTest().getMethodName(), "test_1");
        Assert.assertEquals(testResult.getTest().getPackageName(), "com.consol.citrus.samples");
        Assert.assertTrue(testResult.getStatus().equals(TestStatus.PASS));
        Assert.assertNull(testResult.getErrorCause());

        testResult = report.getResults().get(1);
        Assert.assertEquals(testResult.getTest().getClassName(), "Test_2_IT");
        Assert.assertEquals(testResult.getTest().getName(), "Test_2_IT.test_2");
        Assert.assertEquals(testResult.getTest().getMethodName(), "test_2");
        Assert.assertEquals(testResult.getTest().getPackageName(), "com.consol.citrus.samples");
        Assert.assertTrue(testResult.getStatus().equals(TestStatus.PASS));
        Assert.assertNull(testResult.getErrorCause());

        testResult = report.getResults().get(2);
        Assert.assertEquals(testResult.getTest().getClassName(), "Test_3_IT");
        Assert.assertEquals(testResult.getTest().getName(), "Test_3_IT.test_3");
        Assert.assertEquals(testResult.getTest().getMethodName(), "test_3");
        Assert.assertEquals(testResult.getTest().getPackageName(), "com.consol.citrus.samples");
        Assert.assertTrue(testResult.getStatus().equals(TestStatus.FAIL));
        Assert.assertEquals(testResult.getErrorCause(), "com.consol.citrus.exceptions.TestCaseFailedException");
        Assert.assertEquals(testResult.getErrorMessage(), "Test case failed");
        Assert.assertNotNull(testResult.getStackTrace());
    }

    @Test
    public void testResult() throws Exception {
        Assert.assertTrue(service.hasTestResults(project));

        TestReport report = service.getLatest(project, test1);
        Assert.assertEquals(report.getResults().size(), 1L);
        Assert.assertEquals(report.getTotal(), 1L);
        Assert.assertEquals(report.getPassed(), 1L);
        Assert.assertEquals(report.getFailed(), 0L);
        Assert.assertEquals(report.getResults().get(0).getTest().getClassName(), "Test_1_IT");
        Assert.assertEquals(report.getResults().get(0).getTest().getName(), "Test_1_IT.test_1");
        Assert.assertEquals(report.getResults().get(0).getTest().getMethodName(), "test_1");
        Assert.assertEquals(report.getResults().get(0).getTest().getPackageName(), "com.consol.citrus.samples");
        Assert.assertTrue(report.getResults().get(0).getStatus().equals(TestStatus.PASS));
        Assert.assertNull(report.getResults().get(0).getErrorCause());

        report = service.getLatest(project, test3);
        Assert.assertEquals(report.getResults().size(), 1L);
        Assert.assertEquals(report.getTotal(), 1L);
        Assert.assertEquals(report.getPassed(), 0L);
        Assert.assertEquals(report.getFailed(), 1L);
        Assert.assertEquals(report.getResults().get(0).getTest().getClassName(), "Test_3_IT");
        Assert.assertEquals(report.getResults().get(0).getTest().getName(), "Test_3_IT.test_3");
        Assert.assertEquals(report.getResults().get(0).getTest().getMethodName(), "test_3");
        Assert.assertEquals(report.getResults().get(0).getTest().getPackageName(), "com.consol.citrus.samples");
        Assert.assertTrue(report.getResults().get(0).getStatus().equals(TestStatus.FAIL));
        Assert.assertEquals(report.getResults().get(0).getErrorCause(), "com.consol.citrus.exceptions.TestCaseFailedException");
        Assert.assertEquals(report.getResults().get(0).getErrorMessage(), "Test case failed");
        Assert.assertNotNull(report.getResults().get(0).getStackTrace());
    }
}
