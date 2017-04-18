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

package com.consol.citrus.admin.service.spring;

import com.consol.citrus.admin.model.Project;
import com.consol.citrus.functions.core.RandomNumberFunction;
import com.consol.citrus.jms.endpoint.JmsEndpoint;
import com.consol.citrus.model.config.core.*;
import com.consol.citrus.model.config.http.HttpClientModel;
import com.consol.citrus.model.config.jms.JmsEndpointModel;
import com.consol.citrus.model.config.jms.JmsSyncEndpointModel;
import com.consol.citrus.model.config.ws.WebServiceClientModel;
import com.consol.citrus.util.FileUtils;
import com.consol.citrus.validation.matcher.ValidationMatcherConfig;
import com.consol.citrus.validation.matcher.core.StartsWithValidationMatcher;
import com.consol.citrus.variable.dictionary.DataDictionary;
import com.consol.citrus.ws.client.WebServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.*;
import java.util.List;

/**
 * @author Christoph Deppisch
 */
@ContextConfiguration(locations = { "classpath:citrus-admin-unit-context.xml" })
public class SpringJavaConfigServiceTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private SpringJavaConfigService springJavaConfigService;

    private Project project = new Project();

    @Test
    public void testImports() throws Exception {
        List<Class<?>> imports = springJavaConfigService.getConfigImports(ImportConfig.class, project);
        Assert.assertEquals(imports.size(), 1L);
        Assert.assertEquals(imports.get(0), ChildConfig.class);
    }

    @Test
    public void testSchemaRepositoryConfig() throws Exception {
        SchemaRepositoryModel model = springJavaConfigService.getBeanDefinition(SchemaRepositoryConfig.class, project, "mySchemaRepository", SchemaRepositoryModel.class);
        Assert.assertNotNull(model);
        Assert.assertEquals(model.getId(), "mySchemaRepository");
        Assert.assertEquals(model.getLocations().getLocations().size(), 1L);
        Assert.assertEquals(model.getLocations().getLocations().get(0).getPath(), "classpath*:com/consol/citrus/schemas/*.xsd");
        Assert.assertEquals(model.getSchemas().getSchemas().size(), 1L);
        Assert.assertTrue(model.getSchemas().getSchemas().get(0).getId().startsWith("schema:"));

        List<SchemaRepositoryModel> list = springJavaConfigService.getBeanDefinitions(SchemaRepositoryConfig.class, project, SchemaRepositoryModel.class);
        Assert.assertEquals(list.size(), 1L);
    }

    @Test
    public void testSchemaConfig() throws Exception {
        SchemaModel model = springJavaConfigService.getBeanDefinition(SchemaRepositoryConfig.class, project, "mySchema", SchemaModel.class);
        Assert.assertNotNull(model);
        Assert.assertEquals(model.getId(), "mySchema");

        List<SchemaModel> list = springJavaConfigService.getBeanDefinitions(SchemaRepositoryConfig.class, project, SchemaModel.class);
        Assert.assertEquals(list.size(), 1L);
    }

    @Test
    public void testGlobalVariablesConfig() throws Exception {
        GlobalVariablesModel model = springJavaConfigService.getBeanDefinition(GlobalVariablesConfig.class, project, "globalVariables", GlobalVariablesModel.class);
        Assert.assertNotNull(model);
        Assert.assertEquals(model.getVariables().size(), 2L);
        Assert.assertEquals(model.getVariables().get(0).getName(), "foo");
        Assert.assertEquals(model.getVariables().get(0).getValue(), "globalFoo");
        Assert.assertEquals(model.getVariables().get(1).getName(), "bar");
        Assert.assertEquals(model.getVariables().get(1).getValue(), "globalBar");

        List<GlobalVariablesModel> list = springJavaConfigService.getBeanDefinitions(GlobalVariablesConfig.class, project, GlobalVariablesModel.class);
        Assert.assertEquals(list.size(), 1L);
    }

    @Test
    public void testNamespaceContextConfig() throws Exception {
        NamespaceContextModel model = springJavaConfigService.getBeanDefinition(NamespaceContextConfig.class, project, "namespaceContext", NamespaceContextModel.class);
        Assert.assertNotNull(model);
        Assert.assertEquals(model.getNamespaces().size(), 2L);
        Assert.assertEquals(model.getNamespaces().get(0).getPrefix(), "bar");
        Assert.assertEquals(model.getNamespaces().get(0).getUri(), "http://sample.namespaces.com/bar");
        Assert.assertEquals(model.getNamespaces().get(1).getPrefix(), "foo");
        Assert.assertEquals(model.getNamespaces().get(1).getUri(), "http://sample.namespaces.com/foo");

        List<NamespaceContextModel> list = springJavaConfigService.getBeanDefinitions(NamespaceContextConfig.class, project, NamespaceContextModel.class);
        Assert.assertEquals(list.size(), 1L);
    }

    @Test
    public void testFunctionLibraryConfig() throws Exception {
        FunctionLibraryModel model = springJavaConfigService.getBeanDefinition(FunctionLibraryConfig.class, project, "myFunctionLibrary", FunctionLibraryModel.class);
        Assert.assertNotNull(model);
        Assert.assertEquals(model.getId(), "myFunctionLibrary");
        Assert.assertEquals(model.getPrefix(), "my:");
        Assert.assertEquals(model.getFunctions().size(), 1L);
        Assert.assertEquals(model.getFunctions().get(0).getName(), "foo");
        Assert.assertEquals(model.getFunctions().get(0).getClazz(), RandomNumberFunction.class.getName());

        List<FunctionLibraryModel> list = springJavaConfigService.getBeanDefinitions(FunctionLibraryConfig.class, project, FunctionLibraryModel.class);
        Assert.assertEquals(list.size(), 1L);
    }

    @Test
    public void testValidationMatcherLibraryConfig() throws Exception {
        ValidationMatcherLibraryModel model = springJavaConfigService.getBeanDefinition(ValidationMatcherLibraryConfig.class, project, "myMatcherLibrary", ValidationMatcherLibraryModel.class);
        Assert.assertNotNull(model);
        Assert.assertEquals(model.getId(), "myMatcherLibrary");
        Assert.assertEquals(model.getPrefix(), "my:");
        Assert.assertEquals(model.getMatchers().size(), 1L);
        Assert.assertEquals(model.getMatchers().get(0).getName(), "foo");
        Assert.assertEquals(model.getMatchers().get(0).getClazz(), StartsWithValidationMatcher.class.getName());

        List<ValidationMatcherLibraryModel> list = springJavaConfigService.getBeanDefinitions(ValidationMatcherConfig.class, project, ValidationMatcherLibraryModel.class);
        Assert.assertEquals(list.size(), 1L);
    }

    @Test
    public void testDataDictionaryConfig() throws Exception {
        XmlDataDictionaryModel xmlDataDictionary = springJavaConfigService.getBeanDefinition(DataDictionaryConfig.class, project, "xmlDataDictionary", XmlDataDictionaryModel.class);
        Assert.assertNotNull(xmlDataDictionary);
        Assert.assertEquals(xmlDataDictionary.getId(), "xmlDataDictionary");
        Assert.assertEquals(xmlDataDictionary.getMappings().getMappings().size(), 2L);
        Assert.assertEquals(xmlDataDictionary.getMappings().getMappings().get(0).getPath(), "foo.text");
        Assert.assertEquals(xmlDataDictionary.getMappings().getMappings().get(0).getValue(), "newFoo");
        Assert.assertEquals(xmlDataDictionary.getMappings().getMappings().get(1).getPath(), "bar.text");
        Assert.assertEquals(xmlDataDictionary.getMappings().getMappings().get(1).getValue(), "newBar");
        Assert.assertEquals(xmlDataDictionary.getMappingFile().getPath(), "path/to/some/mapping/file.map");
        Assert.assertEquals(xmlDataDictionary.getMappingStrategy(), DataDictionary.PathMappingStrategy.EXACT.name());
        Assert.assertTrue(xmlDataDictionary.isGlobalScope());

        XpathDataDictionaryModel xpathDataDictionary = springJavaConfigService.getBeanDefinition(DataDictionaryConfig.class, project, "xpathDataDictionary", XpathDataDictionaryModel.class);
        Assert.assertNotNull(xpathDataDictionary);
        Assert.assertEquals(xpathDataDictionary.getId(), "xpathDataDictionary");
        Assert.assertEquals(xpathDataDictionary.getMappings().getMappings().size(), 2L);
        Assert.assertEquals(xpathDataDictionary.getMappings().getMappings().get(0).getPath(), "//foo/text");
        Assert.assertEquals(xpathDataDictionary.getMappings().getMappings().get(0).getValue(), "newFoo");
        Assert.assertEquals(xpathDataDictionary.getMappings().getMappings().get(1).getPath(), "//bar/text");
        Assert.assertEquals(xpathDataDictionary.getMappings().getMappings().get(1).getValue(), "newBar");
        Assert.assertEquals(xpathDataDictionary.getMappingFile().getPath(), "path/to/some/mapping/file.map");
        Assert.assertEquals(xpathDataDictionary.getMappingStrategy(), DataDictionary.PathMappingStrategy.EXACT.name());
        Assert.assertFalse(xpathDataDictionary.isGlobalScope());

        JsonDataDictionaryModel jsonDataDictionary = springJavaConfigService.getBeanDefinition(DataDictionaryConfig.class, project, "jsonDataDictionary", JsonDataDictionaryModel.class);
        Assert.assertNotNull(jsonDataDictionary);
        Assert.assertEquals(jsonDataDictionary.getId(), "jsonDataDictionary");
        Assert.assertEquals(jsonDataDictionary.getMappings().getMappings().size(), 2L);
        Assert.assertEquals(jsonDataDictionary.getMappings().getMappings().get(0).getPath(), "foo.text");
        Assert.assertEquals(jsonDataDictionary.getMappings().getMappings().get(0).getValue(), "newFoo");
        Assert.assertEquals(jsonDataDictionary.getMappings().getMappings().get(1).getPath(), "bar.text");
        Assert.assertEquals(jsonDataDictionary.getMappings().getMappings().get(1).getValue(), "newBar");
        Assert.assertEquals(jsonDataDictionary.getMappingFile().getPath(), "path/to/some/mapping/file.map");
        Assert.assertEquals(jsonDataDictionary.getMappingStrategy(), DataDictionary.PathMappingStrategy.ENDS_WITH.name());
        Assert.assertTrue(jsonDataDictionary.isGlobalScope());

        List<XmlDataDictionaryModel> list = springJavaConfigService.getBeanDefinitions(DataDictionaryConfig.class, project, XmlDataDictionaryModel.class);
        Assert.assertEquals(list.size(), 1L);
    }

    @Test
    public void testGetBeanDefinition() throws Exception {
        JmsEndpointModel endpoint = springJavaConfigService.getBeanDefinition(GetBeanDefinitionConfig.class, project, "jmsInboundEndpoint", JmsEndpointModel.class);
        Assert.assertEquals(endpoint.getId(), "jmsInboundEndpoint");
        Assert.assertEquals(endpoint.getDestinationName(), "jms.inbound.queue");

        endpoint = springJavaConfigService.getBeanDefinition(GetBeanDefinitionConfig.class, project, "jmsOutboundEndpoint", JmsEndpointModel.class);
        Assert.assertEquals(endpoint.getId(), "jmsOutboundEndpoint");
        Assert.assertEquals(endpoint.getDestinationName(), "jms.outbound.queue");

        HttpClientModel client = springJavaConfigService.getBeanDefinition(GetBeanDefinitionConfig.class, project, "httpClient", HttpClientModel.class);
        Assert.assertEquals(client.getId(), "httpClient");
        Assert.assertEquals(client.getRequestUrl(), "http://localhost:8080/foo");

        String sampleBean = springJavaConfigService.getBeanDefinition(ImportConfig.class, project, "sampleBean", String.class);
        Assert.assertEquals(sampleBean, "bean");
    }

    @Test
    public void testGetBeanDefinitions() throws Exception {
        List<JmsEndpointModel> endpoints = springJavaConfigService.getBeanDefinitions(GetBeanDefinitionConfig.class, project, JmsEndpointModel.class);
        Assert.assertEquals(endpoints.size(), 2L);

        Assert.assertTrue(endpoints.stream().anyMatch(endpoint -> endpoint.getDestinationName().equals("jms.inbound.queue")));
        Assert.assertTrue(endpoints.stream().anyMatch(endpoint -> endpoint.getDestinationName().equals("jms.outbound.queue")));

        List<JmsSyncEndpointModel> syncEndpoints = springJavaConfigService.getBeanDefinitions(GetBeanDefinitionConfig.class, project, JmsSyncEndpointModel.class);
        Assert.assertEquals(syncEndpoints.size(), 1L);

        Assert.assertEquals(syncEndpoints.get(0).getId(), "jmsSyncEndpoint");
        Assert.assertTrue(syncEndpoints.stream().anyMatch(endpoint -> endpoint.getDestinationName().equals("jms.inbound.sync.queue")));

        List<WebServiceClientModel> clients = springJavaConfigService.getBeanDefinitions(GetBeanDefinitionConfig.class, project, WebServiceClientModel.class);
        Assert.assertEquals(clients.size(), 0L);
    }

    @Test
    public void testGetBeanNames() throws Exception {
        List<String> names = springJavaConfigService.getBeanNames(GetBeanDefinitionConfig.class, project, JmsEndpoint.class);
        Assert.assertEquals(names.size(), 2L);

        Assert.assertTrue(names.stream().anyMatch(name -> name.equals("jmsInboundEndpoint")));
        Assert.assertTrue(names.stream().anyMatch(name -> name.equals("jmsOutboundEndpoint")));

        names = springJavaConfigService.getBeanNames(GetBeanDefinitionConfig.class, project, WebServiceClient.class);
        Assert.assertEquals(names.size(), 0L);
    }

    @Test
    public void testAddJavaConfig() throws Exception {
        SchemaModel xsdSchema1 = new SchemaModelBuilder().withId("xsdSchema1").withLocation("path/to/schema1.xsd").build();
        SchemaModel xsdSchema2 = new SchemaModelBuilder().withId("xsdSchema2").withLocation("path/to/schema2.xsd").build();

        SchemaRepositoryModel schemaRepository = new SchemaRepositoryModelBuilder()
                .withId("schemaRepository")
                .addSchema(xsdSchema1)
                .addSchema(xsdSchema2).build();

        JmsEndpointModel endpoint = new JmsEndpointModel();
        endpoint.setId("jmsEndpoint");
        endpoint.setDestinationName("jms.inbound.queue");

        File configFile = new ClassPathResource("config/AddBeanJavaConfig.java").getFile();

        springJavaConfigService.addBeanDefinition(configFile, project, xsdSchema1);
        springJavaConfigService.addBeanDefinition(configFile, project, xsdSchema2);
        springJavaConfigService.addBeanDefinition(configFile, project, schemaRepository);
        springJavaConfigService.addBeanDefinition(configFile, project, endpoint);

        String result = FileUtils.readToString(new FileInputStream(configFile));

        System.out.println(result);

        Assert.assertTrue(result.contains("import com.consol.citrus.dsl.endpoint.CitrusEndpoints;"));
        Assert.assertTrue(result.contains("import com.consol.citrus.xml.XsdSchemaRepository;"));
        Assert.assertTrue(result.contains("import org.springframework.core.io.ClassPathResource;"));
        Assert.assertTrue(result.contains("import org.springframework.xml.xsd.SimpleXsdSchema;"));
        Assert.assertTrue(result.contains("import com.consol.citrus.jms.endpoint.JmsEndpoint;"));
        Assert.assertTrue(result.contains("import com.consol.citrus.http.client.HttpClient;"));

        Assert.assertTrue(result.contains("public HttpClient httpClient() {"));
        Assert.assertTrue(result.contains("public JmsEndpoint jmsEndpoint() {"));
        Assert.assertTrue(result.contains("return CitrusEndpoints.jms().asynchronous()"));
        Assert.assertTrue(result.contains(".destination(\"jms.inbound.queue\")"));
        Assert.assertTrue(result.contains("public XsdSchemaRepository schemaRepository() {"));
        Assert.assertTrue(result.contains("public SimpleXsdSchema xsdSchema1() {"));
        Assert.assertTrue(result.contains("xsdSchema1.setXsd(new ClassPathResource(\"path/to/schema1.xsd\"));"));
        Assert.assertTrue(result.contains("public SimpleXsdSchema xsdSchema2() {"));
        Assert.assertTrue(result.contains("xsdSchema2.setXsd(new ClassPathResource(\"path/to/schema2.xsd\"));"));
    }

    /**
     * Creates a temporary file in operating system and writes template content to file.
     * @param templateName
     * @param content
     * @return
     */
    private File createTempContextFile(String templateName, String content) throws IOException {
        FileWriter writer = null;
        File tempFile;

        try {
            tempFile = File.createTempFile(templateName, ".java");

            writer = new FileWriter(tempFile);
            writer.write(content);
        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }

        return tempFile;
    }
}