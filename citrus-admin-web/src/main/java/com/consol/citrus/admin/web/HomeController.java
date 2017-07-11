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

import com.consol.citrus.admin.Application;
import com.consol.citrus.admin.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author Christoph Deppisch
 */
@Controller
@RequestMapping("/")
public class HomeController {

    @Autowired
    private ProjectService projectService;

    @RequestMapping(method = RequestMethod.GET)
    public String index() {
        if (projectService.getActiveProject() == null) {
            return "redirect:/setup";
        }

        return "forward:/index.html";
    }

    @RequestMapping(value = "api/version", method = RequestMethod.GET)
    @ResponseBody
    public String version() {
        return Application.getVersion();
    }

    @RequestMapping(value = "dashboard", method = RequestMethod.GET)
    public String dashboard() {
        return index();
    }

    @RequestMapping(value = "report", method = RequestMethod.GET)
    public String report() {
        return index();
    }

    @RequestMapping(value = "open", method = RequestMethod.GET)
    public String open() {
        return index();
    }

    @RequestMapping(value = "new", method = RequestMethod.GET)
    public String newRoute() {
        return index();
    }

    @RequestMapping(value = "about", method = RequestMethod.GET)
    public String about() {
        return index();
    }

    @RequestMapping(value = { "configuration", "configuration/*" }, method = RequestMethod.GET)
    public String configuration() {
        return index();
    }

    @RequestMapping(value = { "tests", "tests/*" }, method = RequestMethod.GET)
    public String tests() {
        return index();
    }

    @RequestMapping(value = { "settings", "settings/*" }, method = RequestMethod.GET)
    public String settings() {
        return index();
    }

    @RequestMapping(value = "log", method = RequestMethod.GET)
    public String log() {
        return index();
    }
}
