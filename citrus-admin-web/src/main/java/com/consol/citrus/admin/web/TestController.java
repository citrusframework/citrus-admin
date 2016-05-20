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
import com.consol.citrus.admin.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Christoph Deppisch
 */
@Controller
@RequestMapping("/tests")
public class TestController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TestCaseService testCaseService;

    @Autowired
    private TestExecutionService testExecutionService;

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
        return testCaseService.getTestDetail(projectService.getActiveProject(), test);
    }

    @RequestMapping(value="/source/{type}", method = { RequestMethod.POST })
    @ResponseBody
    public String getSourceCode(@RequestBody Test test, @PathVariable("type") String type) {
        return testCaseService.getSourceCode(projectService.getActiveProject(), new TestDetail(test), TestType.valueOf(type.toUpperCase()));
    }

    @RequestMapping(value="/execute", method = { RequestMethod.POST })
    @ResponseBody
    public TestResult execute(@RequestBody Test test) {
        return testExecutionService.execute(projectService.getActiveProject(), test);
    }

    @RequestMapping(value="/stop/{processId}", method = { RequestMethod.GET })
    @ResponseBody
    public ResponseEntity<String> stop(@PathVariable("processId") String processId) {
        testExecutionService.stop(processId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
