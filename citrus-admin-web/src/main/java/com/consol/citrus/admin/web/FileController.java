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

import com.consol.citrus.admin.Application;
import com.consol.citrus.admin.model.Project;
import com.consol.citrus.admin.service.FileBrowserService;
import com.consol.citrus.admin.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;

/**
 * @author Christoph Deppisch
 */
@Controller
@RequestMapping("api/file")
public class FileController {

    @Autowired
    private FileBrowserService fileBrowserService;

    @Autowired
    private ProjectService projectService;

    @RequestMapping(value = "browse", method = RequestMethod.POST)
    @ResponseBody
    public ModelAndView browse(@RequestParam("dir") String dir) {
        String directory = fileBrowserService.decodeDirectoryUrl(dir, Application.getRootDirectory());
        String[] folders = fileBrowserService.getFolders(new File(directory));

        ModelAndView view = new ModelAndView("filetree");
        Project project = new Project(fileBrowserService.separatorsToUnix(directory));
        if (project.getProjectInfoFile().exists()) {
            project.loadSettings();
        }

        view.addObject("valid", projectService.validateProject(project));
        view.addObject("folders", folders);
        view.addObject("baseDir", fileBrowserService.separatorsToUnix(directory));

        return view;
    }
}
