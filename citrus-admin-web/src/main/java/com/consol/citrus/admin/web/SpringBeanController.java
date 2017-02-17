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

import com.consol.citrus.admin.exception.ApplicationRuntimeException;
import com.consol.citrus.admin.model.spring.SpringBean;
import com.consol.citrus.admin.model.spring.SpringContext;
import com.consol.citrus.admin.service.ProjectService;
import com.consol.citrus.admin.service.spring.SpringBeanService;
import com.consol.citrus.util.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Christoph Deppisch
 */
@Controller
@RequestMapping("api/beans")
public class SpringBeanController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private SpringBeanService springBeanService;

    @RequestMapping(method = {RequestMethod.POST})
    @ResponseBody
    public void createBean(@RequestBody SpringBean bean) {
        springBeanService.addBeanDefinition(projectService.getProjectContextConfigFile(), projectService.getActiveProject(), bean);
    }

    @RequestMapping(value = "/{type}", method = {RequestMethod.PUT})
    @ResponseBody
    public void updateBeans(@PathVariable("type") String type, @RequestBody SpringBean bean) {
        springBeanService.updateBeanDefinitions(projectService.getProjectContextConfigFile(), projectService.getActiveProject(), SpringBean.class, bean, "class", type);
    }

    @RequestMapping(value = "/{type}/{id}", method = {RequestMethod.PUT})
    @ResponseBody
    public void updateBean(@PathVariable("id") String id, @RequestBody SpringBean bean) {
        springBeanService.updateBeanDefinition(projectService.getProjectContextConfigFile(), projectService.getActiveProject(), id, bean);
    }

    @RequestMapping(value = "/{type}", method = {RequestMethod.DELETE})
    @ResponseBody
    public void deleteBeans(@PathVariable("type") String type) {
        springBeanService.removeBeanDefinitions(projectService.getProjectContextConfigFile(), projectService.getActiveProject(), SpringBean.class, "class", type);
    }

    @RequestMapping(value = "/{type}/{id}", method = {RequestMethod.DELETE})
    @ResponseBody
    public void deleteBean(@PathVariable("id") String id) {
        springBeanService.removeBeanDefinition(projectService.getProjectContextConfigFile(), projectService.getActiveProject(), id);
    }

    @RequestMapping(method = {RequestMethod.GET})
    @ResponseBody
    public List<SpringBean> listBeans() {
        return springBeanService.getBeanDefinitions(projectService.getProjectContextConfigFile(), projectService.getActiveProject(), SpringBean.class);
    }

    @RequestMapping(value = "/{type}", method = {RequestMethod.GET})
    @ResponseBody
    public List<SpringBean> listBeans(@PathVariable("type") String type) {
        return springBeanService.getBeanDefinitions(projectService.getProjectContextConfigFile(), projectService.getActiveProject(), SpringBean.class, Collections.singletonMap("class", type));
    }

    @RequestMapping(value = "/{type}/{id}", method = {RequestMethod.GET})
    @ResponseBody
    public SpringBean getBean(@PathVariable("id") String id) {
        return springBeanService.getBeanDefinition(projectService.getProjectContextConfigFile(), projectService.getActiveProject(), id, SpringBean.class);
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public List<String> search(@RequestBody String type) {
        return springBeanService.getBeanNames(projectService.getProjectContextConfigFile(), projectService.getActiveProject(), type);
    }

    @RequestMapping(value = "/context", method = {RequestMethod.GET})
    @ResponseBody
    public List<SpringContext> getSpringContextFiles() {
        List<File> configFiles = new ArrayList<>();
        configFiles.add(projectService.getProjectContextConfigFile());
        configFiles.addAll(springBeanService.getConfigImports(projectService.getProjectContextConfigFile(), projectService.getActiveProject()));
        return configFiles
                .stream()
                .map(file -> {
                    SpringContext context = new SpringContext();
                    context.setName(file.getName().substring(0, file.getName().lastIndexOf('.')));
                    context.setFileName(file.getName());
                    return context;
                })
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/context/{file}", method = {RequestMethod.PUT})
    public ResponseEntity updateSpringContextSource(@PathVariable("file") String fileName, @RequestBody String source) {
        File projectContextFile = projectService.getProjectContextConfigFile();
        if (projectContextFile.getName().equals(fileName)) {
            FileUtils.writeToFile(source, projectContextFile);
        } else {
            List<File> importFiles = springBeanService.getConfigImports(projectContextFile, projectService.getActiveProject());
            FileUtils.writeToFile(source, importFiles
                    .stream()
                    .filter(file -> file.getName().equals(fileName))
                    .findFirst()
                    .orElseThrow(() -> new ApplicationRuntimeException("Failed to find Spring context file with name: " + fileName)));
        }

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/context/{file}", method = {RequestMethod.GET})
    @ResponseBody
    public String getSpringContextSource(@PathVariable("file") String fileName) {
        try {
            File projectContextFile = projectService.getProjectContextConfigFile();
            if (projectContextFile.getName().equals(fileName)) {
                return FileUtils.readToString(new FileSystemResource(projectContextFile));
            } else {
                List<File> importFiles = springBeanService.getConfigImports(projectContextFile, projectService.getActiveProject());
                return FileUtils.readToString(new FileSystemResource(importFiles
                        .stream()
                        .filter(file -> file.getName().equals(fileName))
                        .findFirst()
                        .orElseThrow(() -> new ApplicationRuntimeException("Failed to find Spring context file with name: " + fileName))));
            }
        } catch (IOException e) {
            throw new ApplicationRuntimeException("Failed to open Spring context file", e);
        }
    }
}
