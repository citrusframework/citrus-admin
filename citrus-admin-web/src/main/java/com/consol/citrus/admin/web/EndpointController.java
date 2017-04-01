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

import com.consol.citrus.admin.converter.endpoint.EndpointConverter;
import com.consol.citrus.admin.exception.ApplicationRuntimeException;
import com.consol.citrus.admin.model.EndpointModel;
import com.consol.citrus.admin.service.ProjectService;
import com.consol.citrus.admin.service.spring.SpringBeanService;
import com.consol.citrus.admin.service.spring.SpringJavaConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Christoph Deppisch
 */
@Controller
@RequestMapping("api/endpoints")
public class EndpointController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private SpringBeanService springBeanService;

    @Autowired
    private SpringJavaConfigService springJavaConfigService;

    @Autowired
    private List<EndpointConverter> endpointConverter;

    @RequestMapping(method = {RequestMethod.POST})
    @ResponseBody
    public void createEndpoint(@RequestBody EndpointModel endpointDefinition) {
        if (projectService.hasSpringXmlApplicationContext()) {
            springBeanService.addBeanDefinition(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), convertToModel(endpointDefinition));
        } else if (projectService.hasSpringJavaConfig()) {
            springJavaConfigService.addBeanDefinition(projectService.getSpringJavaConfigFile(), projectService.getActiveProject(), convertToModel(endpointDefinition));
        }
    }

    @RequestMapping(value = "/{id}", method = {RequestMethod.PUT})
    @ResponseBody
    public void updateEndpoint(@PathVariable("id") String id, @RequestBody EndpointModel endpointDefinition) {
        if (projectService.hasSpringXmlApplicationContext()) {
            springBeanService.updateBeanDefinition(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), id, convertToModel(endpointDefinition));
        } else if (projectService.hasSpringJavaConfig()) {
            springJavaConfigService.updateBeanDefinition(projectService.getSpringJavaConfigFile(), projectService.getActiveProject(), id, convertToModel(endpointDefinition));
        }
    }

    @RequestMapping(value = "/{id}", method = {RequestMethod.DELETE})
    @ResponseBody
    public void deleteEndpoint(@PathVariable("id") String id) {
        if (projectService.hasSpringXmlApplicationContext()) {
            springBeanService.removeBeanDefinition(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), id);
        } else if (projectService.hasSpringJavaConfig()) {
            springJavaConfigService.removeBeanDefinition(projectService.getSpringJavaConfigFile(), projectService.getActiveProject(), id);
        }
    }

    @RequestMapping(method = {RequestMethod.GET})
    @ResponseBody
    public List<?> listEndpoints() {
        List<EndpointModel> endpoints = new ArrayList<>();
        for (EndpointConverter converter : endpointConverter) {
            List<?> models = new ArrayList<>();
            if (projectService.hasSpringXmlApplicationContext()) {
                models = springBeanService.getBeanDefinitions(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), converter.getSourceModelClass());
            } else if (projectService.hasSpringJavaConfig()) {
                models = springJavaConfigService.getBeanDefinitions(projectService.getSpringJavaConfig(), projectService.getActiveProject(), converter.getSourceModelClass());
            }

            for (Object endpoint : models) {
                endpoints.add(converter.convert(endpoint));
            }
        }

        return endpoints;
    }

    @RequestMapping(value = "/{id}", method = {RequestMethod.GET})
    @ResponseBody
    public Object getEndpoint(@PathVariable("id") String id) {
        for (EndpointConverter converter : endpointConverter) {
            Object model = null;
            if (projectService.hasSpringXmlApplicationContext()) {
                model = springBeanService.getBeanDefinition(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), id, converter.getSourceModelClass());
            } else if (projectService.hasSpringJavaConfig()) {
                model = springJavaConfigService.getBeanDefinition(projectService.getSpringJavaConfig(), projectService.getActiveProject(), id, converter.getSourceModelClass());
            }

            if (model != null) {
                return converter.convert(model);
            }
        }

        throw new ApplicationRuntimeException(String.format("Unable to find endpoint for id '%s'", id));
    }

    @RequestMapping(value = "/types", method = {RequestMethod.GET})
    @ResponseBody
    public List<String> getEndpointTypes() {
        List<String> endpointTypes = new ArrayList<>();
        for (EndpointConverter converter : endpointConverter) {
            endpointTypes.add(converter.getEndpointType());
        }

        return endpointTypes;
    }

    @RequestMapping(value = "/type/{type}", method = {RequestMethod.GET})
    @ResponseBody
    public EndpointModel getEndpointType(@PathVariable("type") String type) {
        for (EndpointConverter converter : endpointConverter) {
            if (converter.getEndpointType().equals(type)) {
                try {
                    return converter.convert(converter.getSourceModelClass().newInstance());
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new ApplicationRuntimeException("Failed to create new endpoint model instance", e);
                }
            }
        }

        throw new ApplicationRuntimeException("Unable to find endpoint definition for type '" + type + "'");
    }

    /**
     * Find endpoint converter for given model type and convert generic endpoint definition back to model class definition.
     * @param endpointDefinition
     * @return
     */
    private Object convertToModel(EndpointModel endpointDefinition) {
        if (!StringUtils.hasText(endpointDefinition.getModelType())) {
            throw new ApplicationRuntimeException("Missing model type in endpoint definition");
        }

        for (EndpointConverter converter : endpointConverter) {
            if (converter.getSourceModelClass().getName().equals(endpointDefinition.getModelType())) {
                return converter.convertBack(endpointDefinition);
            }
        }

        throw new ApplicationRuntimeException("Unable to convert endpoint definition to proper model type");
    }
}
