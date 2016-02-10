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
import com.consol.citrus.admin.service.ProjectService;
import com.consol.citrus.admin.service.spring.SpringBeanService;
import com.consol.citrus.model.config.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author Christoph Deppisch
 */
@Controller
@RequestMapping("/beans")
public class SpringBeanController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private SpringBeanService springBeanService;

    private Map<String, Class[]> typeMappings = new HashMap<>();

    /**
     * Default constructor.
     */
    public SpringBeanController() {
        typeMappings.put("schema-repository", new Class[] { SchemaRepositoryDefinition.class });
        typeMappings.put("global-variables", new Class[] { GlobalVariablesDefinition.class });
        typeMappings.put("namespace-context", new Class[] { NamespaceContextDefinition.class });
        typeMappings.put("data-dictionary", new Class[] { XpathDataDictionaryDefinition.class, XmlDataDictionaryDefinition.class, JsonDataDictionaryDefinition.class });
        typeMappings.put("function-library", new Class[] { FunctionLibraryDefinition.class });
        typeMappings.put("validation-matcher", new Class[] { ValidationMatcherLibraryDefinition.class });
    }

    @RequestMapping(method = {RequestMethod.POST})
    @ResponseBody
    public void createBean(@RequestBody Object bean) {
        springBeanService.addBeanDefinition(projectService.getProjectContextConfigFile(), bean);
    }

    @RequestMapping(value = "/{id}", method = {RequestMethod.PUT})
    @ResponseBody
    public void updateBean(@PathVariable("id") String id, @RequestBody Object bean) {
        springBeanService.updateBeanDefinition(projectService.getProjectContextConfigFile(), id, bean);
    }

    @RequestMapping(value = "/{id}", method = {RequestMethod.DELETE})
    @ResponseBody
    public void deleteBean(@PathVariable("id") String id) {
        springBeanService.removeBeanDefinition(projectService.getProjectContextConfigFile(), id);
    }

    @RequestMapping(value = "/{type}", method = {RequestMethod.GET})
    @ResponseBody
    public List<?> listBeans(@PathVariable("type") String type) {
        List<?> beans = new ArrayList<>();
        Class[] types = typeMappings.get(type);

        for (Class definitionType : types) {
            beans.addAll(springBeanService.getBeanDefinitions(projectService.getProjectContextConfigFile(), definitionType));
        }
        return beans;
    }

    @RequestMapping(value = "/{type}/{id}", method = {RequestMethod.GET})
    @ResponseBody
    public Object getBean(@PathVariable("type") String type, @PathVariable("id") String id) {
        Class[] types = typeMappings.get(type);

        for (Class definitionType : types) {
            Object bean = springBeanService.getBeanDefinition(projectService.getProjectContextConfigFile(), id, definitionType);
            if (bean != null) {
                return bean;
            }
        }

        throw new ApplicationRuntimeException(String.format("Unable to find bean of type %s and id %id", type, id));
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public List<String> search(@RequestBody String beanType) {
        return springBeanService.getBeanNames(projectService.getProjectContextConfigFile(), beanType);
    }
}
