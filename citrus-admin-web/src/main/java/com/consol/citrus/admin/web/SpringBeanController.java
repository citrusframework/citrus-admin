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

import com.consol.citrus.admin.exception.ApplicationRuntimeException;
import com.consol.citrus.admin.model.spring.SpringBean;
import com.consol.citrus.admin.model.spring.SpringContext;
import com.consol.citrus.admin.service.ProjectService;
import com.consol.citrus.admin.service.spring.SpringBeanService;
import com.consol.citrus.admin.service.spring.SpringJavaConfigService;
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

    @Autowired
    private SpringJavaConfigService springJavaConfigService;

    @RequestMapping(method = {RequestMethod.POST})
    @ResponseBody
    public void createBean(@RequestBody SpringBean bean) {
        if (projectService.hasSpringXmlApplicationContext()) {
            springBeanService.addBeanDefinition(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), bean);
        } else if (projectService.hasSpringJavaConfig()) {
            springJavaConfigService.addBeanDefinition(projectService.getSpringJavaConfigFile(), projectService.getActiveProject(), bean);
        }
    }

    @RequestMapping(value = "/{type}", method = {RequestMethod.PUT})
    @ResponseBody
    public void updateBeans(@PathVariable("type") String type, @RequestBody SpringBean bean) {
        if (projectService.hasSpringXmlApplicationContext()) {
            springBeanService.updateBeanDefinitions(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), SpringBean.class, bean, "class", type);
        } else if (projectService.hasSpringJavaConfig()) {
            try {
                springJavaConfigService.updateBeanDefinitions(projectService.getSpringJavaConfigFile(), projectService.getActiveProject(), projectService.getActiveProject().getClassLoader().loadClass(type), bean);
            } catch (ClassNotFoundException | IOException e) {
                throw new ApplicationRuntimeException("Failed to access bean type", e);
            }
        }
    }

    @RequestMapping(value = "/{type}/{id}", method = {RequestMethod.PUT})
    @ResponseBody
    public void updateBean(@PathVariable("id") String id, @RequestBody SpringBean bean) {
        if (projectService.hasSpringXmlApplicationContext()) {
            springBeanService.updateBeanDefinition(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), id, bean);
        } else if (projectService.hasSpringJavaConfig()) {
            springJavaConfigService.updateBeanDefinition(projectService.getSpringJavaConfigFile(), projectService.getActiveProject(), id, bean);
        }
    }

    @RequestMapping(value = "/{type}", method = {RequestMethod.DELETE})
    @ResponseBody
    public void deleteBeans(@PathVariable("type") String type) {
        if (projectService.hasSpringXmlApplicationContext()) {
            springBeanService.removeBeanDefinitions(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), SpringBean.class, "class", type);
        } else if (projectService.hasSpringJavaConfig()) {
            try {
                springJavaConfigService.removeBeanDefinitions(projectService.getSpringJavaConfigFile(), projectService.getActiveProject(), projectService.getActiveProject().getClassLoader().loadClass(type));
            } catch (IOException | ClassNotFoundException e) {
                throw new ApplicationRuntimeException("Failed to access bean type", e);
            }
        }
    }

    @RequestMapping(value = "/{type}/{id}", method = {RequestMethod.DELETE})
    @ResponseBody
    public void deleteBean(@PathVariable("id") String id) {
        if (projectService.hasSpringXmlApplicationContext()) {
            springBeanService.removeBeanDefinition(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), id);
        } else if (projectService.hasSpringJavaConfig()) {
            springJavaConfigService.removeBeanDefinition(projectService.getSpringJavaConfigFile(), projectService.getActiveProject(), id);
        }
    }

    @RequestMapping(method = {RequestMethod.GET})
    @ResponseBody
    public List<SpringBean> listBeans() {
        if (projectService.hasSpringXmlApplicationContext()) {
            return springBeanService.getBeanDefinitions(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), SpringBean.class);
        } else if (projectService.hasSpringJavaConfig()) {
            return springJavaConfigService.getBeanDefinitions(projectService.getActiveProject().getSpringJavaConfig(), projectService.getActiveProject(), SpringBean.class);
        }

        return new ArrayList<>();
    }

    @RequestMapping(value = "/{type}", method = {RequestMethod.GET})
    @ResponseBody
    public List<SpringBean> listBeans(@PathVariable("type") String type) {
        if (projectService.hasSpringXmlApplicationContext()) {
            return springBeanService.getBeanDefinitions(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), SpringBean.class, Collections.singletonMap("class", type));
        } else if (projectService.hasSpringJavaConfig()) {
            return listBeans().stream().filter(bean -> bean.getClazz().equals(type)).collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    @RequestMapping(value = "/{type}/{id}", method = {RequestMethod.GET})
    @ResponseBody
    public SpringBean getBean(@PathVariable("id") String id) {
        if (projectService.hasSpringXmlApplicationContext()) {
            return springBeanService.getBeanDefinition(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), id, SpringBean.class);
        } else if (projectService.hasSpringJavaConfig()) {
            return springJavaConfigService.getBeanDefinition(projectService.getActiveProject().getSpringJavaConfig(), projectService.getActiveProject(), id, SpringBean.class);
        }

        throw new ApplicationRuntimeException("No proper Spring application context defined in project");
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public List<String> search(@RequestBody String type) {
        if (projectService.hasSpringXmlApplicationContext()) {
            return springBeanService.getBeanNames(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), type);
        } else if (projectService.hasSpringJavaConfig()) {
            try {
                return springJavaConfigService.getBeanNames(projectService.getActiveProject().getSpringJavaConfig(), projectService.getActiveProject(), projectService.getActiveProject().getClassLoader().loadClass(type));
            } catch (ClassNotFoundException | IOException e) {
                throw new ApplicationRuntimeException("Failed to access bean type", e);
            }
        }

        return new ArrayList<>();
    }

    @RequestMapping(value = "/context", method = {RequestMethod.GET})
    @ResponseBody
    public List<SpringContext> getSpringContextFiles() {
        if (projectService.hasSpringXmlApplicationContext()) {
            List<File> configFiles = new ArrayList<>();
            configFiles.add(projectService.getSpringXmlApplicationContextFile());
            configFiles.addAll(springBeanService.getConfigImports(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject()));

            return configFiles
                    .stream()
                    .map(file -> {
                        SpringContext context = new SpringContext();
                        context.setName(file.getName().substring(0, file.getName().lastIndexOf('.')));
                        context.setFileName(file.getName());
                        return context;
                    })
                    .collect(Collectors.toList());
        } else if (projectService.hasSpringJavaConfig()) {
            List<Class<?>> configFiles = new ArrayList<>();
            configFiles.add(projectService.getActiveProject().getSpringJavaConfig());
            configFiles.addAll(springJavaConfigService.getConfigImports(projectService.getActiveProject().getSpringJavaConfig(), projectService.getActiveProject()));

            return configFiles
                    .stream()
                    .map(clazz -> {
                        SpringContext context = new SpringContext();
                        context.setName(clazz.getSimpleName());
                        context.setFileName(clazz.getName());
                        return context;
                    })
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    @RequestMapping(value = "/context/{file}", method = {RequestMethod.PUT})
    public ResponseEntity updateSpringContextSource(@PathVariable("file") String fileName, @RequestBody String source) {
        if (projectService.hasSpringXmlApplicationContext()) {
            File projectContextFile = projectService.getSpringXmlApplicationContextFile();
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
        } else if (projectService.hasSpringJavaConfig()) {
            Class<?> projectConfig = projectService.getActiveProject().getSpringJavaConfig();
            if (projectConfig.getName().equals(fileName)) {
                FileUtils.writeToFile(source, projectService.getSpringJavaConfigFile());
            } else {
                List<Class<?>> importFiles = springJavaConfigService.getConfigImports(projectConfig, projectService.getActiveProject());
                FileUtils.writeToFile(source, importFiles
                        .stream()
                        .filter(clazz -> clazz.getName().equals(fileName))
                        .findFirst()
                        .map(clazz -> new File(projectService.getActiveProject().getJavaDirectory(), clazz.getName().replaceAll("\\.", File.separator) + ".java"))
                        .orElseThrow(() -> new ApplicationRuntimeException("Failed to find Spring context file with name: " + fileName)));
            }
        }

        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/context/{file}", method = {RequestMethod.GET})
    @ResponseBody
    public String getSpringContextSource(@PathVariable("file") String fileName) {
        try {
            if (projectService.hasSpringXmlApplicationContext()) {
                File projectContextFile = projectService.getSpringXmlApplicationContextFile();

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
            } else if (projectService.hasSpringJavaConfig()) {
                Class<?> projectConfig = projectService.getActiveProject().getSpringJavaConfig();

                if (projectConfig.getName().equals(fileName)) {
                    return FileUtils.readToString(new FileSystemResource(projectService.getSpringJavaConfigFile()));
                } else {
                    List<Class<?>> importFiles = springJavaConfigService.getConfigImports(projectConfig, projectService.getActiveProject());
                    return FileUtils.readToString(new FileSystemResource(importFiles
                            .stream()
                            .filter(clazz -> clazz.getName().equals(fileName))
                            .findFirst()
                            .map(clazz -> new File(projectService.getActiveProject().getJavaDirectory(), clazz.getName().replaceAll("\\.", File.separator) + ".java"))
                            .orElseThrow(() -> new ApplicationRuntimeException("Failed to find Spring context file with name: " + fileName))));
                }
            }
        } catch (IOException e) {
            throw new ApplicationRuntimeException("Failed to open Spring context file", e);
        }

        throw new ApplicationRuntimeException("No proper Spring application context defined in project");
    }
}
