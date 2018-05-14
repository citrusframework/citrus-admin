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
import com.consol.citrus.admin.service.ProjectService;
import com.consol.citrus.admin.service.spring.SpringBeanService;
import com.consol.citrus.admin.service.spring.SpringJavaConfigService;
import com.consol.citrus.model.config.core.*;
import com.consol.citrus.xml.schema.XsdSchemaMappingStrategy;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Christoph Deppisch
 */
@Controller
@RequestMapping("api/config")
public class ConfigurationController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private SpringBeanService springBeanService;

    @Autowired
    private SpringJavaConfigService springJavaConfigService;

    @RequestMapping(value = "/global-variables", method = {RequestMethod.POST})
    @ResponseBody
    public void createGlobalVariables(@RequestBody GlobalVariablesModel component) {
        if (projectService.hasSpringXmlApplicationContext()) {
            springBeanService.addBeanDefinition(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), component);
        } else if (projectService.hasSpringJavaConfig()) {
            springJavaConfigService.addBeanDefinition(projectService.getSpringJavaConfigFile(), projectService.getActiveProject(), component);
        }
    }

    @RequestMapping(value = "/global-variables", method = {RequestMethod.PUT})
    @ResponseBody
    public void updateGlobalVariables(@RequestBody GlobalVariablesModel component) {
        if (component.getVariables().isEmpty()) {
            if (projectService.hasSpringXmlApplicationContext()) {
                springBeanService.removeBeanDefinitions(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), GlobalVariablesModel.class);
            } else if (projectService.hasSpringJavaConfig()) {
                springJavaConfigService.removeBeanDefinitions(projectService.getSpringJavaConfigFile(), projectService.getActiveProject(), GlobalVariablesModel.class);
            }
        } else if (getGlobalVariables().getVariables().isEmpty()) {
            createGlobalVariables(component);
        } else {
            if (projectService.hasSpringXmlApplicationContext()) {
                springBeanService.updateBeanDefinitions(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), GlobalVariablesModel.class, component);
            } else if (projectService.hasSpringJavaConfig()) {
                springJavaConfigService.updateBeanDefinitions(projectService.getSpringJavaConfigFile(), projectService.getActiveProject(), GlobalVariablesModel.class, component);
            }
        }
    }

    @RequestMapping(value = "/global-variables", method = {RequestMethod.GET})
    @ResponseBody
    public GlobalVariablesModel getGlobalVariables() {
        List<GlobalVariablesModel> components = null;
        if (projectService.hasSpringXmlApplicationContext()) {
            components = springBeanService.getBeanDefinitions(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), GlobalVariablesModel.class);
        } else if (projectService.hasSpringJavaConfig()) {
            components = springJavaConfigService.getBeanDefinitions(projectService.getActiveProject().getSpringJavaConfig(), projectService.getActiveProject(), GlobalVariablesModel.class);
        }

        if (CollectionUtils.isEmpty(components)) {
            return new GlobalVariablesModel();
        } else {
            return components.get(0);
        }
    }

    @RequestMapping(value = "/namespace-context", method = {RequestMethod.POST})
    @ResponseBody
    public void createNamespaceContext(@RequestBody NamespaceContextModel component) {
        if (projectService.hasSpringXmlApplicationContext()) {
            springBeanService.addBeanDefinition(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), component);
        } else if (projectService.hasSpringJavaConfig()) {
            springJavaConfigService.addBeanDefinition(projectService.getSpringJavaConfigFile(), projectService.getActiveProject(), component);
        }
    }

    @RequestMapping(value = "/namespace-context", method = {RequestMethod.PUT})
    @ResponseBody
    public void updateNamespaceContext(@RequestBody NamespaceContextModel component) {
        if (component.getNamespaces().isEmpty()) {
            if (projectService.hasSpringXmlApplicationContext()) {
                springBeanService.removeBeanDefinitions(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), NamespaceContextModel.class);
            } else if (projectService.hasSpringJavaConfig()) {
                springJavaConfigService.removeBeanDefinitions(projectService.getSpringJavaConfigFile(), projectService.getActiveProject(), NamespaceContextModel.class);
            }
        } else if (getNamespaceContext().getNamespaces().isEmpty()) {
            createNamespaceContext(component);
        } else {
            if (projectService.hasSpringXmlApplicationContext()) {
                springBeanService.updateBeanDefinitions(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), NamespaceContextModel.class, component);
            } else if (projectService.hasSpringJavaConfig()) {
                springJavaConfigService.updateBeanDefinitions(projectService.getSpringJavaConfigFile(), projectService.getActiveProject(), NamespaceContextModel.class, component);
            }
        }
    }

    @RequestMapping(value = "/namespace-context", method = {RequestMethod.GET})
    @ResponseBody
    public NamespaceContextModel getNamespaceContext() {
        List<NamespaceContextModel> components = null;
        if (projectService.hasSpringXmlApplicationContext()) {
            components = springBeanService.getBeanDefinitions(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), NamespaceContextModel.class);
        } else if (projectService.hasSpringJavaConfig()) {
            components = springJavaConfigService.getBeanDefinitions(projectService.getActiveProject().getSpringJavaConfig(), projectService.getActiveProject(), NamespaceContextModel.class);
        }

        if (CollectionUtils.isEmpty(components)) {
            return new NamespaceContextModel();
        } else {
            return components.get(0);
        }
    }

    @RequestMapping(value = "/schema-repository", method = {RequestMethod.GET})
    @ResponseBody
    public List<SchemaRepositoryModel> listSchemaRepositories() {
        if (projectService.hasSpringXmlApplicationContext()) {
            return springBeanService.getBeanDefinitions(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), SchemaRepositoryModel.class);
        } else if (projectService.hasSpringJavaConfig()) {
            return springJavaConfigService.getBeanDefinitions(projectService.getActiveProject().getSpringJavaConfig(), projectService.getActiveProject(), SchemaRepositoryModel.class);
        }

        return new ArrayList<>();
    }

    @RequestMapping(value = "/schema-repository/{id}", method = {RequestMethod.GET})
    @ResponseBody
    public SchemaRepositoryModel getSchemaRepository(@PathVariable("id") String id) {
        if (projectService.hasSpringXmlApplicationContext()) {
            return springBeanService.getBeanDefinition(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), id, SchemaRepositoryModel.class);
        } else if (projectService.hasSpringJavaConfig()) {
            return springJavaConfigService.getBeanDefinition(projectService.getActiveProject().getSpringJavaConfig(), projectService.getActiveProject(), id, SchemaRepositoryModel.class);
        }

        throw new ApplicationRuntimeException("No proper Spring application context defined in project");
    }

    @RequestMapping(value = "/schema-repository", method = {RequestMethod.POST})
    @ResponseBody
    public void createSchemaRepository(@RequestBody SchemaRepositoryModel component) {
        if (projectService.hasSpringXmlApplicationContext()) {
            springBeanService.addBeanDefinition(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), component);
        } else if (projectService.hasSpringJavaConfig()) {
            springJavaConfigService.addBeanDefinition(projectService.getSpringJavaConfigFile(), projectService.getActiveProject(), component);
        }
    }

    @RequestMapping(value = "/schema-repository", method = {RequestMethod.PUT})
    @ResponseBody
    public void updateSchemaRepository(@RequestBody SchemaRepositoryModel component) {
        if (projectService.hasSpringXmlApplicationContext()) {
            springBeanService.updateBeanDefinition(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), component.getId(), component);
        } else if (projectService.hasSpringJavaConfig()) {
            springJavaConfigService.updateBeanDefinition(projectService.getSpringJavaConfigFile(), projectService.getActiveProject(), component.getId(), component);
        }
    }

    @RequestMapping(value = "/schema", method = {RequestMethod.GET})
    @ResponseBody
    public List<SchemaModel> listSchemas() {
        List<SchemaModel> schemas = new ArrayList<>();
        if (projectService.hasSpringXmlApplicationContext()) {
            schemas = springBeanService.getBeanDefinitions(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), SchemaModel.class);
        } else if (projectService.hasSpringJavaConfig()) {
            schemas = springJavaConfigService.getBeanDefinitions(projectService.getActiveProject().getSpringJavaConfig(), projectService.getActiveProject(), SchemaModel.class);
        }

        for (int i = 0; i < schemas.size(); i++) {
            if (!StringUtils.hasText(schemas.get(i).getId())) {
                schemas.get(i).setId("schema" + (i + 1));
            }
        }

        return schemas;
    }

    @RequestMapping(value = "/schema/{id}", method = {RequestMethod.GET})
    @ResponseBody
    public SchemaModel getSchema(@PathVariable("id") String id) {
        if (projectService.hasSpringXmlApplicationContext()) {
            return springBeanService.getBeanDefinition(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), id, SchemaModel.class);
        } else if (projectService.hasSpringJavaConfig()) {
            return springJavaConfigService.getBeanDefinition(projectService.getActiveProject().getSpringJavaConfig(), projectService.getActiveProject(), id, SchemaModel.class);
        }

        throw new ApplicationRuntimeException("No proper Spring application context defined in project");
    }

    @RequestMapping(value = "/schema", method = {RequestMethod.POST})
    @ResponseBody
    public void createSchema(@RequestBody SchemaModel component) {
        if (projectService.hasSpringXmlApplicationContext()) {
            springBeanService.addBeanDefinition(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), component);
        } else if (projectService.hasSpringJavaConfig()) {
            springJavaConfigService.addBeanDefinition(projectService.getSpringJavaConfigFile(), projectService.getActiveProject(), component);
        }
    }

    @RequestMapping(value = "/schema", method = {RequestMethod.PUT})
    @ResponseBody
    public void updateSchema(@RequestBody SchemaModel component) {
        if (projectService.hasSpringXmlApplicationContext()) {
            springBeanService.updateBeanDefinition(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), component.getId(), component);
        } else if (projectService.hasSpringJavaConfig()) {
            springJavaConfigService.updateBeanDefinition(projectService.getSpringJavaConfigFile(), projectService.getActiveProject(), component.getId(), component);
        }
    }

    @RequestMapping(value = "/function-library", method = {RequestMethod.GET})
    @ResponseBody
    public List<FunctionLibraryModel> listFunctionLibraries() {
        if (projectService.hasSpringXmlApplicationContext()) {
            return springBeanService.getBeanDefinitions(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), FunctionLibraryModel.class);
        } else if (projectService.hasSpringJavaConfig()) {
            return springJavaConfigService.getBeanDefinitions(projectService.getActiveProject().getSpringJavaConfig(), projectService.getActiveProject(), FunctionLibraryModel.class);
        }

        return new ArrayList<>();
    }

    @RequestMapping(value = "/function-library/{id}", method = {RequestMethod.GET})
    @ResponseBody
    public FunctionLibraryModel getFunctionLibrary(@PathVariable("id") String id) {
        if (projectService.hasSpringXmlApplicationContext()) {
            return springBeanService.getBeanDefinition(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), id, FunctionLibraryModel.class);
        } else if (projectService.hasSpringJavaConfig()) {
            return springJavaConfigService.getBeanDefinition(projectService.getActiveProject().getSpringJavaConfig(), projectService.getActiveProject(), id, FunctionLibraryModel.class);
        } else {
            throw new ApplicationRuntimeException("No proper Spring application context defined in project");
        }
    }

    @RequestMapping(value = "/function-library", method = {RequestMethod.POST})
    @ResponseBody
    public void createFunctionLibrary(@RequestBody FunctionLibraryModel component) {
        if (projectService.hasSpringXmlApplicationContext()) {
            springBeanService.addBeanDefinition(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), component);
        } else if (projectService.hasSpringJavaConfig()) {
            springJavaConfigService.addBeanDefinition(projectService.getSpringJavaConfigFile(), projectService.getActiveProject(), component);
        }
    }

    @RequestMapping(value = "/function-library", method = {RequestMethod.PUT})
    @ResponseBody
    public void updateFunctionLibrary(@RequestBody FunctionLibraryModel component) {
        if (projectService.hasSpringXmlApplicationContext()) {
            springBeanService.updateBeanDefinition(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), component.getId(), component);
        } else if (projectService.hasSpringJavaConfig()) {
            springJavaConfigService.updateBeanDefinition(projectService.getSpringJavaConfigFile(), projectService.getActiveProject(), component.getId(), component);
        }
    }

    @RequestMapping(value = "/validation-matcher", method = {RequestMethod.GET})
    @ResponseBody
    public List<ValidationMatcherLibraryModel> listValidationMatcherLibraries() {
        if (projectService.hasSpringXmlApplicationContext()) {
            return springBeanService.getBeanDefinitions(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), ValidationMatcherLibraryModel.class);
        } else if (projectService.hasSpringJavaConfig()) {
            return springJavaConfigService.getBeanDefinitions(projectService.getActiveProject().getSpringJavaConfig(), projectService.getActiveProject(), ValidationMatcherLibraryModel.class);
        }

        return new ArrayList<>();
    }

    @RequestMapping(value = "/validation-matcher/{id}", method = {RequestMethod.GET})
    @ResponseBody
    public ValidationMatcherLibraryModel getValidationMatcherLibrary(@PathVariable("id") String id) {
        if (projectService.hasSpringXmlApplicationContext()) {
            return springBeanService.getBeanDefinition(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), id, ValidationMatcherLibraryModel.class);
        } else if (projectService.hasSpringJavaConfig()) {
            return springJavaConfigService.getBeanDefinition(projectService.getActiveProject().getSpringJavaConfig(), projectService.getActiveProject(), id, ValidationMatcherLibraryModel.class);
        }

        throw new ApplicationRuntimeException("No proper Spring application context defined in project");
    }

    @RequestMapping(value = "/validation-matcher", method = {RequestMethod.POST})
    @ResponseBody
    public void createValidationMatcherLibrary(@RequestBody ValidationMatcherLibraryModel component) {
        if (projectService.hasSpringXmlApplicationContext()) {
            springBeanService.addBeanDefinition(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), component);
        } else if (projectService.hasSpringJavaConfig()) {
            springJavaConfigService.addBeanDefinition(projectService.getSpringJavaConfigFile(), projectService.getActiveProject(), component);
        }
    }

    @RequestMapping(value = "/validation-matcher", method = {RequestMethod.PUT})
    @ResponseBody
    public void updateValidationMatcherLibrary(@RequestBody ValidationMatcherLibraryModel component) {
        if (projectService.hasSpringXmlApplicationContext()) {
            springBeanService.updateBeanDefinition(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), component.getId(), component);
        } else if (projectService.hasSpringJavaConfig()) {
            springJavaConfigService.updateBeanDefinition(projectService.getSpringJavaConfigFile(), projectService.getActiveProject(), component.getId(), component);
        }
    }

    @RequestMapping(value = "/data-dictionary", method = {RequestMethod.GET})
    @ResponseBody
    public List<DataDictionaryType> listDataDictionaries() {
        List<DataDictionaryType> libraries = new ArrayList<DataDictionaryType>();
        if (projectService.hasSpringXmlApplicationContext()) {
            libraries.addAll(springBeanService.getBeanDefinitions(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), XpathDataDictionaryModel.class));
            libraries.addAll(springBeanService.getBeanDefinitions(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), XmlDataDictionaryModel.class));
            libraries.addAll(springBeanService.getBeanDefinitions(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), JsonDataDictionaryModel.class));
        } else if (projectService.hasSpringJavaConfig()) {
            Class<?> springJavaConfig = projectService.getActiveProject().getSpringJavaConfig();

            libraries.addAll(springJavaConfigService.getBeanDefinitions(springJavaConfig, projectService.getActiveProject(), XpathDataDictionaryModel.class));
            libraries.addAll(springJavaConfigService.getBeanDefinitions(springJavaConfig, projectService.getActiveProject(), XmlDataDictionaryModel.class));
            libraries.addAll(springJavaConfigService.getBeanDefinitions(springJavaConfig, projectService.getActiveProject(), JsonDataDictionaryModel.class));
        }

        return libraries;
    }

    @RequestMapping(value = "/data-dictionary/{id}", method = {RequestMethod.GET})
    @ResponseBody
    public DataDictionaryType getDataDictionary(@PathVariable("id") String id) {
        DataDictionaryType library = null;

        if (projectService.hasSpringXmlApplicationContext()) {
            library = springBeanService.getBeanDefinition(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), id, XpathDataDictionaryModel.class);

            if (library == null) {
                library = springBeanService.getBeanDefinition(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), id, XmlDataDictionaryModel.class);
            }

            if (library == null) {
                library = springBeanService.getBeanDefinition(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), id, JsonDataDictionaryModel.class);
            }
        } else if (projectService.hasSpringJavaConfig()) {
            Class<?> springJavaConfig = projectService.getActiveProject().getSpringJavaConfig();

            library = springJavaConfigService.getBeanDefinition(springJavaConfig, projectService.getActiveProject(), id, XpathDataDictionaryModel.class);

            if (library == null) {
                library = springJavaConfigService.getBeanDefinition(springJavaConfig, projectService.getActiveProject(), id, XmlDataDictionaryModel.class);
            }

            if (library == null) {
                library = springJavaConfigService.getBeanDefinition(springJavaConfig, projectService.getActiveProject(), id, JsonDataDictionaryModel.class);
            }
        }

        return library;
    }

    @RequestMapping(value = "/data-dictionary/{type}", method = {RequestMethod.POST})
    @ResponseBody
    public void createDataDictionary(@PathVariable("type") String type, @RequestBody JSONObject model) {
        try {
            if (projectService.hasSpringXmlApplicationContext()) {
                springBeanService.addBeanDefinition(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), getDataDictionaryType(type, model));
            } else if (projectService.hasSpringJavaConfig()) {
                springJavaConfigService.addBeanDefinition(projectService.getSpringJavaConfigFile(), projectService.getActiveProject(), getDataDictionaryType(type, model));
            }
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
            if (dictionary instanceof XpathDataDictionaryModel) {
                type = "xpath";
            } else if (dictionary instanceof XmlDataDictionaryModel) {
                type = "xml";
            } else if (dictionary instanceof JsonDataDictionaryModel) {
                type = "json";
            } else {
                throw new ApplicationRuntimeException("Unsupported data-dictionary type: " + dictionary.getClass());
            }

            DataDictionaryType component = getDataDictionaryType(type, model);
            if (projectService.hasSpringXmlApplicationContext()) {
                springBeanService.updateBeanDefinition(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), component.getId(), component);
            } else if (projectService.hasSpringJavaConfig()) {
                springJavaConfigService.updateBeanDefinition(projectService.getSpringJavaConfigFile(), projectService.getActiveProject(), component.getId(), component);
            }
        } catch (IOException e) {
            throw new ApplicationRuntimeException("Failed to read data dictionary model", e);
        }
    }

    /**
     * Get data dictionary implementation for given type.
     *
     * @param type
     * @param model
     * @return
     * @throws IOException
     */
    private DataDictionaryType getDataDictionaryType(String type, JSONObject model) throws IOException {
        ObjectMapper jsonMapper = new ObjectMapper();
        DataDictionaryType component;
        if (type.equals("xpath")) {
            component = jsonMapper.readValue(model.toJSONString(), XpathDataDictionaryModel.class);
        } else if (type.equals("xml")) {
            component = jsonMapper.readValue(model.toJSONString(), XmlDataDictionaryModel.class);
        } else if (type.equals("json")) {
            component = jsonMapper.readValue(model.toJSONString(), JsonDataDictionaryModel.class);
        } else {
            throw new ApplicationRuntimeException("Unsupported data-dictionary type: " + type);
        }
        return component;
    }

    @RequestMapping(value = "/{id}", method = {RequestMethod.DELETE})
    @ResponseBody
    public void deleteComponent(@PathVariable("id") String id) {
        if (projectService.hasSpringXmlApplicationContext()) {
            springBeanService.removeBeanDefinition(projectService.getSpringXmlApplicationContextFile(), projectService.getActiveProject(), id);
        } else if (projectService.hasSpringJavaConfig()) {
            springJavaConfigService.removeBeanDefinition(projectService.getSpringJavaConfigFile(), projectService.getActiveProject(), id);
        }
    }

    @RequestMapping(value = "/mapping-strategy")
    @ResponseBody
    public List<String> getMappingStrategies() {
        if (projectService.hasSpringXmlApplicationContext()) {
            return springBeanService.getBeanNames(
                    projectService.getSpringXmlApplicationContextFile(),
                    projectService.getActiveProject(),
                    XsdSchemaMappingStrategy.class.getName()
            ).stream().filter(Objects::nonNull).collect(Collectors.toList());
        } else if (projectService.hasSpringJavaConfig()) {
            return springJavaConfigService.getBeanNames(
                    projectService.getActiveProject().getSpringJavaConfig(),
                    projectService.getActiveProject(),
                    XsdSchemaMappingStrategy.class
            ).stream().filter(Objects::nonNull).collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}
