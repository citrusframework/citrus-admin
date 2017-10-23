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

package com.consol.citrus.admin.web;

import com.consol.citrus.admin.model.*;
import com.consol.citrus.admin.service.ProjectService;
import com.consol.citrus.admin.service.TestReportService;
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
    private TestReportService testReportService;

    @RequestMapping(value = "/latest", method = RequestMethod.GET)
    @ResponseBody
    public TestReport getLatest() {
        return testReportService.getLatest(projectService.getActiveProject());
    }

    @RequestMapping(value="/result", method = { RequestMethod.POST })
    @ResponseBody
    public TestReport getTestResult(@RequestBody Test test) {
        return testReportService.getLatest(projectService.getActiveProject(), test);
    }
}
