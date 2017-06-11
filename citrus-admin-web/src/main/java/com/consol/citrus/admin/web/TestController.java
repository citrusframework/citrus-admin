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
import com.consol.citrus.admin.service.*;
import com.consol.citrus.admin.service.report.JUnitTestReportService;
import com.consol.citrus.admin.service.report.TestNGTestReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Christoph Deppisch
 */
@Controller
@RequestMapping("api/tests")
public class TestController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private TestExecutionService testExecutionService;

    @Autowired
    private TestNGTestReportService testNGTestReportService;

    @Autowired
    private JUnitTestReportService junitTestReportService;


    @RequestMapping(method = { RequestMethod.GET })
    @ResponseBody
    public List<TestGroup> list() {
        return testCaseService.getTestPackages(projectService.getActiveProject());
    }

    @RequestMapping(value = "/latest", method = { RequestMethod.GET })
    @ResponseBody
    public List<TestGroup> getLatest() {
        return testCaseService.getLatest(projectService.getActiveProject(), 8);
    }

    @RequestMapping(value = "/count", method = { RequestMethod.GET })
    @ResponseBody
    public long getTestCount() {
        return testCaseService.getTestCount(projectService.getActiveProject());
    }

    @RequestMapping(value="/detail", method = { RequestMethod.POST })
    @ResponseBody
    public TestDetail getTestDetail(@RequestBody Test test) {
        Project project = projectService.getActiveProject();
        TestDetail detail = testCaseService.getTestDetail(project, test);
        TestResult result = null;
        if (testNGTestReportService.hasTestResults(project)) {
            result = testNGTestReportService.getLatest(project, test);
        } else if (junitTestReportService.hasTestResults(project)) {
            result = junitTestReportService.getLatest(project, test);
        }

        if (result != null) {
            detail.setResult(result);
        }
        
        return detail;
    }

    @RequestMapping(value="/source", method = { RequestMethod.GET })
    @ResponseBody
    public String getSourceCode(@RequestParam("relativePath") String relativePath) {
        return testCaseService.getSourceCode(projectService.getActiveProject(), relativePath);
    }

    @RequestMapping(value="/source", method = { RequestMethod.PUT })
    public ResponseEntity updateSourceCode(@RequestParam("relativePath") String relativePath, @RequestBody String newSourceCode) {
        testCaseService.updateSourceCode(projectService.getActiveProject(), relativePath, newSourceCode);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value="/execute", method = { RequestMethod.GET })
    @ResponseBody
    public TestResult executeGroup() {
        return testExecutionService.execute(projectService.getActiveProject());
    }

    @RequestMapping(value="/execute", method = { RequestMethod.POST })
    @ResponseBody
    public TestResult execute(@RequestBody Test test) {
        return testExecutionService.execute(projectService.getActiveProject(), test);
    }

    @RequestMapping(value="/execute/group", method = { RequestMethod.POST })
    @ResponseBody
    public TestResult executeGroup(@RequestBody TestGroup group) {
        return testExecutionService.execute(projectService.getActiveProject(), group);
    }

    @RequestMapping(value="/stop/{processId}", method = { RequestMethod.GET })
    @ResponseBody
    public ResponseEntity stop(@PathVariable("processId") String processId) {
        testExecutionService.stop(processId);
        return ResponseEntity.ok().build();
    }
}
