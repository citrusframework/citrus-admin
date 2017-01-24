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

import com.consol.citrus.admin.model.Project;
import com.consol.citrus.admin.service.ProjectService;
import com.consol.citrus.admin.service.spring.SpringBeanService;
import com.consol.citrus.model.config.core.*;
import com.consol.citrus.variable.dictionary.DataDictionary;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

/**
 * @author Christoph Deppisch
 */
@ContextConfiguration(locations = { "classpath:citrus-admin-unit-context.xml" })
public class ConfigurationControllerTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private SpringBeanService springBeanService;

    @Autowired
    private ConfigurationController controller;

    @BeforeClass
    public void setup() throws IOException {
        projectService.setActiveProject(new Project(new ClassPathResource("projects/maven").getFile().getAbsolutePath()));
    }

    @Test
    public void testGlobalVariables() throws Exception {
        Assert.assertNotNull(controller.getGlobalVariables());
        Assert.assertEquals(controller.getGlobalVariables().getVariables().size(), 0L);

        GlobalVariablesModel variables = new GlobalVariablesModel();
        GlobalVariablesModel.Variable variable = new GlobalVariablesModel.Variable();
        variable.setName("foo");
        variable.setValue("bar");
        variables.getVariables().add(variable);

        controller.createGlobalVariables(variables);

        GlobalVariablesModel newGlobalVariables = controller.getGlobalVariables();
        Assert.assertNotNull(newGlobalVariables);
        Assert.assertEquals(newGlobalVariables.getVariables().size(), 1L);
        Assert.assertEquals(newGlobalVariables.getVariables().get(0).getName(), "foo");
        Assert.assertEquals(newGlobalVariables.getVariables().get(0).getValue(), "bar");

        newGlobalVariables.getVariables().get(0).setName("bar");
        controller.updateGlobalVariables(newGlobalVariables);

        newGlobalVariables = controller.getGlobalVariables();
        Assert.assertNotNull(newGlobalVariables);
        Assert.assertEquals(newGlobalVariables.getVariables().size(), 1L);
        Assert.assertEquals(newGlobalVariables.getVariables().get(0).getName(), "bar");
        Assert.assertEquals(newGlobalVariables.getVariables().get(0).getValue(), "bar");

        newGlobalVariables.getVariables().clear();
        controller.updateGlobalVariables(newGlobalVariables);

        Assert.assertNotNull(controller.getGlobalVariables());
        Assert.assertEquals(controller.getGlobalVariables().getVariables().size(), 0L);
    }

    @Test
    public void testNamespaceContext() throws Exception {
        Assert.assertNotNull(controller.getNamespaceContext());
        Assert.assertEquals(controller.getNamespaceContext().getNamespaces().size(), 0L);

        NamespaceContextModel namespaceContext = new NamespaceContextModel();
        NamespaceContextModel.Namespace namespace = new NamespaceContextModel.Namespace();
        namespace.setPrefix("foo");
        namespace.setUri("http://citrusframework/sample");
        namespaceContext.getNamespaces().add(namespace);

        controller.createNamespaceContext(namespaceContext);

        NamespaceContextModel newNamespaceContext = controller.getNamespaceContext();
        Assert.assertNotNull(newNamespaceContext);
        Assert.assertEquals(newNamespaceContext.getNamespaces().size(), 1L);
        Assert.assertEquals(newNamespaceContext.getNamespaces().get(0).getPrefix(), "foo");
        Assert.assertEquals(newNamespaceContext.getNamespaces().get(0).getUri(), "http://citrusframework/sample");

        newNamespaceContext.getNamespaces().get(0).setPrefix("bar");
        controller.updateNamespaceContext(newNamespaceContext);

        newNamespaceContext = controller.getNamespaceContext();
        Assert.assertNotNull(newNamespaceContext);
        Assert.assertEquals(newNamespaceContext.getNamespaces().size(), 1L);
        Assert.assertEquals(newNamespaceContext.getNamespaces().get(0).getPrefix(), "bar");
        Assert.assertEquals(newNamespaceContext.getNamespaces().get(0).getUri(), "http://citrusframework/sample");

        newNamespaceContext.getNamespaces().clear();
        controller.updateNamespaceContext(newNamespaceContext);

        Assert.assertNotNull(controller.getNamespaceContext());
        Assert.assertEquals(controller.getNamespaceContext().getNamespaces().size(), 0L);
    }

    @Test
    public void testXmlDataDictionary() throws Exception {
        List<DataDictionaryType> dictionaries = controller.listDataDictionaries();
        Assert.assertEquals(dictionaries.size(), 0L);

        JSONObject dictionary = new JSONObject();
        JSONObject mappings = new JSONObject();
        JSONArray mappingsArray = new JSONArray();

        JSONObject mapping = new JSONObject();
        mapping.put("path", "root");
        mapping.put("value", "foo");
        mappingsArray.add(mapping);
        mappings.put("mappings", mappingsArray);
        dictionary.put("id", "xmlDataDictionary");
        dictionary.put("mappings", mappings);
        dictionary.put("mappingStrategy", DataDictionary.PathMappingStrategy.EXACT.toString());

        controller.createDataDictionary("xml", dictionary);

        dictionaries = controller.listDataDictionaries();
        Assert.assertEquals(dictionaries.size(), 1L);

        DataDictionaryType xmlDataDictionary = controller.getDataDictionary("xmlDataDictionary");
        Assert.assertNotNull(xmlDataDictionary);
        Assert.assertEquals(xmlDataDictionary.getId(), "xmlDataDictionary");
        Assert.assertEquals(xmlDataDictionary.getMappings().getMappings().size(), 1L);
        Assert.assertEquals(xmlDataDictionary.getMappings().getMappings().get(0).getPath(), "root");
        Assert.assertEquals(xmlDataDictionary.getMappings().getMappings().get(0).getValue(), "foo");
        Assert.assertEquals(xmlDataDictionary.getMappingStrategy(), DataDictionary.PathMappingStrategy.EXACT.toString());

        dictionary.put("mappingStrategy", DataDictionary.PathMappingStrategy.STARTS_WITH.toString());
        controller.updateDataDictionary(dictionary);

        xmlDataDictionary = controller.getDataDictionary("xmlDataDictionary");
        Assert.assertNotNull(xmlDataDictionary);
        Assert.assertEquals(xmlDataDictionary.getMappingStrategy(), DataDictionary.PathMappingStrategy.STARTS_WITH.toString());

        controller.deleteComponent("xmlDataDictionary");

        dictionaries = controller.listDataDictionaries();
        Assert.assertEquals(dictionaries.size(), 0L);

        Assert.assertNull(controller.getDataDictionary("xmlDataDictionary"));
    }

    @Test
    public void testJsonDataDictionary() throws Exception {
        List<DataDictionaryType> dictionaries = controller.listDataDictionaries();
        Assert.assertEquals(dictionaries.size(), 0L);

        JSONObject dictionary = new JSONObject();
        JSONObject mappings = new JSONObject();
        JSONArray mappingsArray = new JSONArray();

        JSONObject mapping = new JSONObject();
        mapping.put("path", "$.root");
        mapping.put("value", "foo");
        mappingsArray.add(mapping);
        mappings.put("mappings", mappingsArray);
        dictionary.put("id", "jsonDataDictionary");
        dictionary.put("mappings", mappings);
        dictionary.put("mappingStrategy", DataDictionary.PathMappingStrategy.EXACT.toString());

        controller.createDataDictionary("json", dictionary);

        dictionaries = controller.listDataDictionaries();
        Assert.assertEquals(dictionaries.size(), 1L);

        DataDictionaryType jsonDataDictionary = controller.getDataDictionary("jsonDataDictionary");
        Assert.assertNotNull(jsonDataDictionary);
        Assert.assertEquals(jsonDataDictionary.getId(), "jsonDataDictionary");
        Assert.assertEquals(jsonDataDictionary.getMappings().getMappings().size(), 1L);
        Assert.assertEquals(jsonDataDictionary.getMappings().getMappings().get(0).getPath(), "$.root");
        Assert.assertEquals(jsonDataDictionary.getMappings().getMappings().get(0).getValue(), "foo");
        Assert.assertEquals(jsonDataDictionary.getMappingStrategy(), DataDictionary.PathMappingStrategy.EXACT.toString());

        dictionary.put("mappingStrategy", DataDictionary.PathMappingStrategy.STARTS_WITH.toString());
        controller.updateDataDictionary(dictionary);

        jsonDataDictionary = controller.getDataDictionary("jsonDataDictionary");
        Assert.assertNotNull(jsonDataDictionary);
        Assert.assertEquals(jsonDataDictionary.getMappingStrategy(), DataDictionary.PathMappingStrategy.STARTS_WITH.toString());

        controller.deleteComponent("jsonDataDictionary");

        dictionaries = controller.listDataDictionaries();
        Assert.assertEquals(dictionaries.size(), 0L);

        Assert.assertNull(controller.getDataDictionary("jsonDataDictionary"));
    }

    @Test
    public void testXpathDataDictionary() throws Exception {
        List<DataDictionaryType> dictionaries = controller.listDataDictionaries();
        Assert.assertEquals(dictionaries.size(), 0L);

        JSONObject dictionary = new JSONObject();
        JSONObject mappings = new JSONObject();
        JSONArray mappingsArray = new JSONArray();

        JSONObject mapping = new JSONObject();
        mapping.put("path", "/root");
        mapping.put("value", "foo");
        mappingsArray.add(mapping);
        mappings.put("mappings", mappingsArray);
        dictionary.put("id", "xpathDataDictionary");
        dictionary.put("mappings", mappings);
        dictionary.put("mappingStrategy", DataDictionary.PathMappingStrategy.EXACT.toString());

        controller.createDataDictionary("xpath", dictionary);

        dictionaries = controller.listDataDictionaries();
        Assert.assertEquals(dictionaries.size(), 1L);

        DataDictionaryType xpathDataDictionary = controller.getDataDictionary("xpathDataDictionary");
        Assert.assertNotNull(xpathDataDictionary);
        Assert.assertEquals(xpathDataDictionary.getId(), "xpathDataDictionary");
        Assert.assertEquals(xpathDataDictionary.getMappings().getMappings().size(), 1L);
        Assert.assertEquals(xpathDataDictionary.getMappings().getMappings().get(0).getPath(), "/root");
        Assert.assertEquals(xpathDataDictionary.getMappings().getMappings().get(0).getValue(), "foo");
        Assert.assertEquals(xpathDataDictionary.getMappingStrategy(), DataDictionary.PathMappingStrategy.EXACT.toString());

        dictionary.put("mappingStrategy", DataDictionary.PathMappingStrategy.STARTS_WITH.toString());
        controller.updateDataDictionary(dictionary);

        xpathDataDictionary = controller.getDataDictionary("xpathDataDictionary");
        Assert.assertNotNull(xpathDataDictionary);
        Assert.assertEquals(xpathDataDictionary.getMappingStrategy(), DataDictionary.PathMappingStrategy.STARTS_WITH.toString());

        controller.deleteComponent("xpathDataDictionary");

        dictionaries = controller.listDataDictionaries();
        Assert.assertEquals(dictionaries.size(), 0L);

        Assert.assertNull(controller.getDataDictionary("xpathDataDictionary"));
    }

    @Test
    public void testValidationMatcherLibrary() throws Exception {
        List<ValidationMatcherLibraryModel> libraries = controller.listValidationMatcherLibraries();
        Assert.assertEquals(libraries.size(), 0L);

        ValidationMatcherLibraryModel library = new ValidationMatcherLibraryModel();
        ValidationMatcherLibraryModel.Matcher matcher = new ValidationMatcherLibraryModel.Matcher();
        matcher.setName("fooValidationMatcher");
        matcher.setClazz("com.citrus.foo.FooValidationMatcher");
        library.getMatchers().add(matcher);

        library.setId("newValidationMatcherLibrary");
        library.setPrefix("foo");

        controller.createValidationMatcherLibrary(library);

        libraries = controller.listValidationMatcherLibraries();
        Assert.assertEquals(libraries.size(), 1L);

        ValidationMatcherLibraryModel newValidationMatcherLibrary = controller.getValidationMatcherLibrary("newValidationMatcherLibrary");
        Assert.assertNotNull(newValidationMatcherLibrary);
        Assert.assertEquals(newValidationMatcherLibrary.getPrefix(), "foo");

        newValidationMatcherLibrary.setPrefix("bar");
        controller.updateValidationMatcherLibrary(newValidationMatcherLibrary);

        newValidationMatcherLibrary = controller.getValidationMatcherLibrary("newValidationMatcherLibrary");
        Assert.assertNotNull(newValidationMatcherLibrary);
        Assert.assertEquals(newValidationMatcherLibrary.getPrefix(), "bar");

        controller.deleteComponent("newValidationMatcherLibrary");

        libraries = controller.listValidationMatcherLibraries();
        Assert.assertEquals(libraries.size(), 0L);

        Assert.assertNull(controller.getValidationMatcherLibrary("newValidationMatcherLibrary"));
    }

    @Test
    public void testFunctionLibrary() throws Exception {
        List<FunctionLibraryModel> libraries = controller.listFunctionLibraries();
        Assert.assertEquals(libraries.size(), 0L);

        FunctionLibraryModel library = new FunctionLibraryModel();
        FunctionLibraryModel.Function function = new FunctionLibraryModel.Function();
        function.setName("fooFunction");
        function.setClazz("com.citrus.foo.FooFunction");
        library.getFunctions().add(function);

        library.setId("newFunctionLibrary");
        library.setPrefix("foo");

        controller.createFunctionLibrary(library);

        libraries = controller.listFunctionLibraries();
        Assert.assertEquals(libraries.size(), 1L);

        FunctionLibraryModel newFunctionLibrary = controller.getFunctionLibrary("newFunctionLibrary");
        Assert.assertNotNull(newFunctionLibrary);
        Assert.assertEquals(newFunctionLibrary.getPrefix(), "foo");

        newFunctionLibrary.setPrefix("bar");
        controller.updateFunctionLibrary(newFunctionLibrary);

        newFunctionLibrary = controller.getFunctionLibrary("newFunctionLibrary");
        Assert.assertNotNull(newFunctionLibrary);
        Assert.assertEquals(newFunctionLibrary.getPrefix(), "bar");

        controller.deleteComponent("newFunctionLibrary");

        libraries = controller.listFunctionLibraries();
        Assert.assertEquals(libraries.size(), 0L);

        Assert.assertNull(controller.getFunctionLibrary("newFunctionLibrary"));
    }

    @Test
    public void testSchemaRepository() throws Exception {
        List<SchemaRepositoryModel> schemaRepositories = controller.listSchemaRepositories();
        Assert.assertEquals(schemaRepositories.size(), 0L);

        SchemaRepositoryModel schemaRepository = new SchemaRepositoryModel();
        schemaRepository.setId("newSchemaRepository");
        schemaRepository.setSchemaMappingStrategy("simpleMappingStrategy");
        SchemaRepositoryModel.Schemas schemas = new SchemaRepositoryModel.Schemas();

        SchemaModel schema = new SchemaModel();
        schema.setId("newSchema");
        schema.setLocation("sample/location.xsd");
        schemas.getSchemas().add(schema);
        schemaRepository.setSchemas(schemas);

        controller.createSchemaRepository(schemaRepository);

        schemaRepositories = controller.listSchemaRepositories();
        Assert.assertEquals(schemaRepositories.size(), 1L);

        SchemaRepositoryModel newSchemaRepository = controller.getSchemaRepository("newSchemaRepository");
        Assert.assertNotNull(newSchemaRepository);
        Assert.assertEquals(newSchemaRepository.getSchemaMappingStrategy(), "simpleMappingStrategy");

        newSchemaRepository.setSchemaMappingStrategy("newMappingStrategy");
        controller.updateSchemaRepository(newSchemaRepository);

        newSchemaRepository = controller.getSchemaRepository("newSchemaRepository");
        Assert.assertNotNull(newSchemaRepository);
        Assert.assertEquals(newSchemaRepository.getSchemaMappingStrategy(), "newMappingStrategy");

        controller.deleteComponent("newSchemaRepository");

        schemaRepositories = controller.listSchemaRepositories();
        Assert.assertEquals(schemaRepositories.size(), 0L);

        Assert.assertNull(controller.getSchemaRepository("newSchemaRepository"));
    }

    @Test
    public void testSchema() throws Exception {
        List<SchemaModel> schemas = controller.listSchemas();
        Assert.assertEquals(schemas.size(), 0L);

        SchemaModel schema = new SchemaModel();
        schema.setId("newSchema");
        schema.setLocation("sample/location.xsd");

        controller.createSchema(schema);

        schemas = controller.listSchemas();
        Assert.assertEquals(schemas.size(), 1L);

        SchemaModel newSchema = controller.getSchema("newSchema");
        Assert.assertNotNull(newSchema);
        Assert.assertEquals(newSchema.getLocation(), "sample/location.xsd");

        newSchema.setLocation("sample/new.xsd");
        controller.updateSchema(newSchema);

        newSchema = controller.getSchema("newSchema");
        Assert.assertNotNull(newSchema);
        Assert.assertEquals(newSchema.getLocation(), "sample/new.xsd");

        controller.deleteComponent("newSchema");

        schemas = controller.listSchemas();
        Assert.assertEquals(schemas.size(), 0L);

        Assert.assertNull(controller.getSchema("newSchema"));
    }
}