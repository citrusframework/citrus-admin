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

import com.consol.citrus.admin.model.Project;
import com.consol.citrus.admin.model.TestReport;
import com.consol.citrus.admin.service.ProjectService;
import com.consol.citrus.admin.service.report.TestReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Christoph Deppisch
 */
@Controller
@RequestMapping("/report")
public class ReportController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private List<TestReportService> testReportService;

    @RequestMapping(value = "/latest", method = RequestMethod.GET)
    @ResponseBody
    public TestReport getLatest() {
        Project project = projectService.getActiveProject();
        for (TestReportService reportService : testReportService) {
            if (reportService.hasTestResults(project)) {
                return reportService.getLatest(project);
            }
        }

        return new TestReport();
    }
}
