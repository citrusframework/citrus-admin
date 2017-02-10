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

package com.consol.citrus.admin.web;

import com.consol.citrus.admin.model.*;
import com.consol.citrus.admin.service.ProjectService;
import com.consol.citrus.admin.service.report.JUnitTestReportService;
import com.consol.citrus.admin.service.report.TestNGTestReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author Christoph Deppisch
 */
@Controller
@RequestMapping("api/report")
public class ReportController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TestNGTestReportService testNGTestReportService;

    @Autowired
    private JUnitTestReportService junitTestReportService;

    @RequestMapping(value = "/latest", method = RequestMethod.GET)
    @ResponseBody
    public TestReport getLatest() {
        Project project = projectService.getActiveProject();
        if (null != project) {
            if (testNGTestReportService.hasTestResults(project)) {
                return testNGTestReportService.getLatest(project);
            } else if (junitTestReportService.hasTestResults(project)) {
                return junitTestReportService.getLatest(project);
            }
        }

        return new TestReport();
    }

    @RequestMapping(value="/result", method = { RequestMethod.POST })
    @ResponseBody
    public TestResult getTestResult(@RequestBody Test test) {
        Project project = projectService.getActiveProject();
        if (null != project) {
            if (testNGTestReportService.hasTestResults(project)) {
                return testNGTestReportService.getLatest(project, test);
            } else if (junitTestReportService.hasTestResults(project)) {
                return junitTestReportService.getLatest(project, test);
            }
        }

        TestResult result = new TestResult();
        result.setTest(test);
        return result;
    }
}
