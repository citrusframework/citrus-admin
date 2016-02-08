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

import com.consol.citrus.admin.model.Project;
import com.consol.citrus.admin.model.TestReport;
import com.consol.citrus.admin.service.report.JUnitTestReportService;
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
        Project project = new Project(new ClassPathResource("maven").getFile().getCanonicalPath());

        Assert.assertTrue(service.hasTestResults(project));

        TestReport report = service.getLatest(project);
        Assert.assertEquals(report.getSuiteName(), "Sample test suite");
        Assert.assertEquals(report.getDuration(), 9000L);
        Assert.assertEquals(report.getTotal(), 16L);
        Assert.assertEquals(report.getPassed(), 10L);
        Assert.assertEquals(report.getFailed(), 5L);
        Assert.assertEquals(report.getSkipped(), 1L);
    }
}
