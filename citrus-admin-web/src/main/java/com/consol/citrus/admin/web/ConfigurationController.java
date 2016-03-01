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
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christoph Deppisch
 */
@Controller
@RequestMapping("/config")
public class ConfigurationController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private SpringBeanService springBeanService;

    @RequestMapping(value = "/global-variables", method = {RequestMethod.POST})
    @ResponseBody
    public void createGlobalVariables(@RequestBody GlobalVariablesDefinition component) {
        springBeanService.addBeanDefinition(projectService.getProjectContextConfigFile(), component);
    }

    @RequestMapping(value = "/global-variables", method = {RequestMethod.PUT})
    @ResponseBody
    public void updateGlobalVariables(@RequestBody GlobalVariablesDefinition component) {
        if (component.getVariables().isEmpty()) {
            springBeanService.removeBeanDefinitions(projectService.getProjectContextConfigFile(), GlobalVariablesDefinition.class);
        } else if (getGlobalVariables().getVariables().isEmpty()) {
            createGlobalVariables(component);
        } else {
            springBeanService.updateBeanDefinitions(projectService.getProjectContextConfigFile(), GlobalVariablesDefinition.class, component);
        }
    }

    @RequestMapping(value = "/global-variables", method = {RequestMethod.GET})
    @ResponseBody
    public GlobalVariablesDefinition getGlobalVariables() {
        List<GlobalVariablesDefinition> components = springBeanService.getBeanDefinitions(projectService.getProjectContextConfigFile(), GlobalVariablesDefinition.class);
        if (CollectionUtils.isEmpty(components)) {
            return new GlobalVariablesDefinition();
        } else {
            return components.get(0);
        }
    }

    @RequestMapping(value = "/namespace-context", method = {RequestMethod.POST})
    @ResponseBody
    public void createNamespaceContext(@RequestBody NamespaceContextDefinition component) {
        springBeanService.addBeanDefinition(projectService.getProjectContextConfigFile(), component);
    }

    @RequestMapping(value = "/namespace-context", method = {RequestMethod.PUT})
    @ResponseBody
    public void updateNamespaceContext(@RequestBody NamespaceContextDefinition component) {
        if (component.getNamespaces().isEmpty()) {
            springBeanService.removeBeanDefinitions(projectService.getProjectContextConfigFile(), NamespaceContextDefinition.class);
        } else if (getNamespaceContext().getNamespaces().isEmpty()) {
            createNamespaceContext(component);
        } else {
            springBeanService.updateBeanDefinitions(projectService.getProjectContextConfigFile(), NamespaceContextDefinition.class, component);
        }
    }

    @RequestMapping(value = "/namespace-context", method = {RequestMethod.GET})
    @ResponseBody
    public NamespaceContextDefinition getNamespaceContext() {
        List<NamespaceContextDefinition> components = springBeanService.getBeanDefinitions(projectService.getProjectContextConfigFile(), NamespaceContextDefinition.class);
        if (CollectionUtils.isEmpty(components)) {
            return new NamespaceContextDefinition();
        } else {
            return components.get(0);
        }
    }

    @RequestMapping(value = "/schema-repository", method = {RequestMethod.GET})
    @ResponseBody
    public List<SchemaRepositoryDefinition> listSchemaRepositories() {
        return springBeanService.getBeanDefinitions(projectService.getProjectContextConfigFile(), SchemaRepositoryDefinition.class);
    }

    @RequestMapping(value = "/schema-repository/{id}", method = {RequestMethod.GET})
    @ResponseBody
    public SchemaRepositoryDefinition getSchemaRepository(@PathVariable("id") String id) {
        return springBeanService.getBeanDefinition(projectService.getProjectContextConfigFile(), id, SchemaRepositoryDefinition.class);
    }

    @RequestMapping(value = "/schema-repository", method = {RequestMethod.POST})
    @ResponseBody
    public void createSchemaRepository(@RequestBody SchemaRepositoryDefinition component) {
        springBeanService.addBeanDefinition(projectService.getProjectContextConfigFile(), component);
    }

    @RequestMapping(value = "/schema-repository", method = {RequestMethod.PUT})
    @ResponseBody
    public void updateSchemaRepository(@RequestBody SchemaRepositoryDefinition component) {
        springBeanService.updateBeanDefinition(projectService.getProjectContextConfigFile(), component.getId(), component);
    }

    @RequestMapping(value = "/schema", method = {RequestMethod.GET})
    @ResponseBody
    public List<SchemaDefinition> listSchemas() {
        return springBeanService.getBeanDefinitions(projectService.getProjectContextConfigFile(), SchemaDefinition.class);
    }

    @RequestMapping(value = "/schema/{id}", method = {RequestMethod.GET})
    @ResponseBody
    public SchemaDefinition getSchema(@PathVariable("id") String id) {
        return springBeanService.getBeanDefinition(projectService.getProjectContextConfigFile(), id, SchemaDefinition.class);
    }

    @RequestMapping(value = "/schema", method = {RequestMethod.POST})
    @ResponseBody
    public void createSchema(@RequestBody SchemaDefinition component) {
        springBeanService.addBeanDefinition(projectService.getProjectContextConfigFile(), component);
    }

    @RequestMapping(value = "/schema", method = {RequestMethod.PUT})
    @ResponseBody
    public void updateSchema(@RequestBody SchemaDefinition component) {
        springBeanService.updateBeanDefinition(projectService.getProjectContextConfigFile(), component.getId(), component);
    }

    @RequestMapping(value = "/function-library", method = {RequestMethod.GET})
    @ResponseBody
    public List<FunctionLibraryDefinition> listFunctionLibraries() {
        return springBeanService.getBeanDefinitions(projectService.getProjectContextConfigFile(), FunctionLibraryDefinition.class);
    }

    @RequestMapping(value = "/function-library/{id}", method = {RequestMethod.GET})
    @ResponseBody
    public FunctionLibraryDefinition getFunctionLibrary(@PathVariable("id") String id) {
        return springBeanService.getBeanDefinition(projectService.getProjectContextConfigFile(), id, FunctionLibraryDefinition.class);
    }

    @RequestMapping(value = "/function-library", method = {RequestMethod.POST})
    @ResponseBody
    public void createFunctionLibrary(@RequestBody FunctionLibraryDefinition component) {
        springBeanService.addBeanDefinition(projectService.getProjectContextConfigFile(), component);
    }

    @RequestMapping(value = "/function-library", method = {RequestMethod.PUT})
    @ResponseBody
    public void updateFunctionLibrary(@RequestBody FunctionLibraryDefinition component) {
        springBeanService.updateBeanDefinition(projectService.getProjectContextConfigFile(), component.getId(), component);
    }

    @RequestMapping(value = "/validation-matcher", method = {RequestMethod.GET})
    @ResponseBody
    public List<ValidationMatcherLibraryDefinition> listValidationMatcherLibraries() {
        return springBeanService.getBeanDefinitions(projectService.getProjectContextConfigFile(), ValidationMatcherLibraryDefinition.class);
    }

    @RequestMapping(value = "/validation-matcher/{id}", method = {RequestMethod.GET})
    @ResponseBody
    public ValidationMatcherLibraryDefinition getValidationMatcherLibrary(@PathVariable("id") String id) {
        return springBeanService.getBeanDefinition(projectService.getProjectContextConfigFile(), id, ValidationMatcherLibraryDefinition.class);
    }

    @RequestMapping(value = "/validation-matcher", method = {RequestMethod.POST})
    @ResponseBody
    public void createValidationMatcherLibrary(@RequestBody ValidationMatcherLibraryDefinition component) {
        springBeanService.addBeanDefinition(projectService.getProjectContextConfigFile(), component);
    }

    @RequestMapping(value = "/validation-matcher", method = {RequestMethod.PUT})
    @ResponseBody
    public void updateValidationMatcherLibrary(@RequestBody ValidationMatcherLibraryDefinition component) {
        springBeanService.updateBeanDefinition(projectService.getProjectContextConfigFile(), component.getId(), component);
    }

    @RequestMapping(value = "/data-dictionary", method = {RequestMethod.GET})
    @ResponseBody
    public List<DataDictionaryType> listDataDictionaries() {
        List<DataDictionaryType> libraries = new ArrayList<DataDictionaryType>();

        libraries.addAll(springBeanService.getBeanDefinitions(projectService.getProjectContextConfigFile(), XpathDataDictionaryDefinition.class));
        libraries.addAll(springBeanService.getBeanDefinitions(projectService.getProjectContextConfigFile(), XmlDataDictionaryDefinition.class));
        libraries.addAll(springBeanService.getBeanDefinitions(projectService.getProjectContextConfigFile(), JsonDataDictionaryDefinition.class));

        return libraries;
    }

    @RequestMapping(value = "/data-dictionary/{id}", method = {RequestMethod.GET})
    @ResponseBody
    public DataDictionaryType getDataDictionary(@PathVariable("id") String id) {
        DataDictionaryType library = springBeanService.getBeanDefinition(projectService.getProjectContextConfigFile(), id, XpathDataDictionaryDefinition.class);

        if (library == null) {
            library = springBeanService.getBeanDefinition(projectService.getProjectContextConfigFile(), id, XmlDataDictionaryDefinition.class);
        }

        if (library == null) {
            library = springBeanService.getBeanDefinition(projectService.getProjectContextConfigFile(), id, JsonDataDictionaryDefinition.class);
        }

        return library;
    }

    @RequestMapping(value = "/data-dictionary/{type}", method = {RequestMethod.POST})
    @ResponseBody
    public void createDataDictionary(@PathVariable("type") String type, @RequestBody JSONObject model) {
        try {
            springBeanService.addBeanDefinition(projectService.getProjectContextConfigFile(), getDataDictionaryType(type, model));
        } catch (IOException e) {
            throw new ApplicationRuntimeException("Failed to read data dictionary model", e);
        }
    }

    @RequestMapping(value = "/data-dictionary", method = {RequestMethod.PUT})
    @ResponseBody
    public void updateDataDictionary(@RequestBody JSONObject model) {
        try {
            DataDictionaryType dictionary = getDataDictionary(model.get("id").toString());
            String type;
            if (dictionary instanceof XpathDataDictionaryDefinition) {
                type = "xpath";
            } else if (dictionary instanceof XmlDataDictionaryDefinition) {
                type = "xml";
            } else if (dictionary instanceof JsonDataDictionaryDefinition) {
                type = "json";
            } else {
                throw new ApplicationRuntimeException("Unsupported data-dictionary type: " + dictionary.getClass());
            }

            DataDictionaryType component = getDataDictionaryType(type, model);
            springBeanService.updateBeanDefinition(projectService.getProjectContextConfigFile(), component.getId(), component);
        } catch (IOException e) {
            throw new ApplicationRuntimeException("Failed to read data dictionary model", e);
        }

    }

    /**
     * Get data dictionary implementation for given type.
     * @param type
     * @param model
     * @return
     * @throws IOException
     */
    private DataDictionaryType getDataDictionaryType(String type, JSONObject model) throws IOException {
        ObjectMapper jsonMapper = new ObjectMapper();
        DataDictionaryType component;
        if (type.equals("xpath")) {
            component = jsonMapper.readValue(model.toJSONString(), XpathDataDictionaryDefinition.class);
        } else if (type.equals("xml")) {
            component = jsonMapper.readValue(model.toJSONString(), XmlDataDictionaryDefinition.class);
        } else if (type.equals("json")) {
            component = jsonMapper.readValue(model.toJSONString(), JsonDataDictionaryDefinition.class);
        } else {
            throw new ApplicationRuntimeException("Unsupported data-dictionary type: " + type);
        }
        return component;
    }

    @RequestMapping(value = "/{id}", method = {RequestMethod.DELETE})
    @ResponseBody
    public void deleteComponent(@PathVariable("id") String id) {
        springBeanService.removeBeanDefinition(projectService.getProjectContextConfigFile(), id);
    }
}
