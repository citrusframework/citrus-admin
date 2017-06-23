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

import com.consol.citrus.admin.connector.WebSocketPushEventsListener;
import com.consol.citrus.admin.marshal.SpringBeanMarshaller;
import com.consol.citrus.admin.model.Project;
import com.consol.citrus.admin.model.spring.SpringBean;
import com.consol.citrus.model.config.core.*;
import com.consol.citrus.model.config.jms.JmsEndpointModel;
import com.consol.citrus.util.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.*;
import java.util.List;

/**
 * @author Christoph Deppisch
 */
public class SpringBeanServiceTest {

    private SpringBeanService springBeanConfigService = new SpringBeanService();
    private Project project;

    @BeforeMethod
    public void beforeMethod() {
        springBeanConfigService.setSpringBeanMarshaller(new SpringBeanMarshaller());
        springBeanConfigService.init();

        project = new Project();
    }

    @Test
    public void testAddBeanDefinition() throws Exception {
        SchemaModel xsdSchema1 = new SchemaModelBuilder().withId("1").withLocation("l1").build();
        SchemaModel xsdSchema2 = new SchemaModelBuilder().withId("2").withLocation("l2").build();

        SchemaRepositoryModel schemaRepository = new SchemaRepositoryModelBuilder().withId("x").addSchemaReference("1").addSchemaReference("2").build();

        SpringBean springBean = new SpringBean();
        springBean.setId("listener");
        springBean.setClazz(WebSocketPushEventsListener.class.getName());

        File tempFile = createTempContextFile("citrus-context-add");

        springBeanConfigService.addBeanDefinition(tempFile, project, xsdSchema1);
        springBeanConfigService.addBeanDefinition(tempFile, project, xsdSchema2);
        springBeanConfigService.addBeanDefinition(tempFile, project, schemaRepository);
        springBeanConfigService.addBeanDefinition(tempFile, project, springBean);

        String result = FileUtils.readToString(new FileInputStream(tempFile));

        Assert.assertTrue(result.contains("<citrus:schema id=\"1\" location=\"l1\"/>"), "Failed to validate " + result);
        Assert.assertTrue(result.contains("<citrus:schema id=\"2\" location=\"l2\"/>"), "Failed to validate " + result);
        Assert.assertTrue(result.contains("<citrus:schema-repository id=\"x\">"), "Failed to validate " + result);
        Assert.assertTrue(result.contains("<bean class=\"" + WebSocketPushEventsListener.class.getName() + "\" id=\"listener\"/>"), "Failed to validate " + result);
    }

    @Test
    public void testAddBeanDefinitionNamespace() throws Exception {
        JmsEndpointModel jmsEndpoint = new JmsEndpointModel();
        jmsEndpoint.setId("jmsEndpoint");
        jmsEndpoint.setDestinationName("jms.inbound.queue");

        File tempFile = createTempContextFile("citrus-context-add");

        springBeanConfigService.addBeanDefinition(tempFile, project, jmsEndpoint);

        String result = FileUtils.readToString(new FileInputStream(tempFile));

        Assert.assertTrue(result.contains("<citrus-jms:endpoint id=\"jmsEndpoint\" destination-name=\"jms.inbound.queue\"/>"), "Failed to validate " + result);
        Assert.assertTrue(result.contains("xmlns:citrus-jms=\"http://www.citrusframework.org/schema/jms/config\""), "Failed to validate " + result);
    }

    @Test
    public void testRemoveBeanDefinition() throws Exception {
        File tempFile = createTempContextFile("citrus-context-remove");

        springBeanConfigService.removeBeanDefinition(tempFile, project, "deleteMe");
        springBeanConfigService.removeBeanDefinition(tempFile, project, "deleteMeName");

        springBeanConfigService.removeBeanDefinition(tempFile, project, "helloSchema");

        String result = FileUtils.readToString(new FileInputStream(tempFile));

        Assert.assertTrue(result.contains("id=\"preserveMe\""), "Failed to validate " + result);
        Assert.assertTrue(result.contains("name=\"preserveMeName\""), "Failed to validate " + result);

        Assert.assertFalse(result.contains("<bean id=\"deleteMe\""), "Failed to validate " + result);
        Assert.assertTrue(result.contains("<bean name=\"deleteMeName\""), "Failed to validate " + result);
        Assert.assertTrue(result.contains("<property name=\"deleteMe\" value=\"some\"/>"), "Failed to validate " + result);
    }
    
    @Test
    public void testRemoveSpringBeanDefinitions() throws Exception {
        File tempFile = createTempContextFile("citrus-context-remove-bean");

        springBeanConfigService.removeBeanDefinitions(tempFile, project, SpringBean.class, "class", "com.consol.citrus.DeleteMe");

        String result = FileUtils.readToString(new FileInputStream(tempFile));

        Assert.assertTrue(result.contains("id=\"preserveMe\""), "Failed to validate " + result);
        Assert.assertTrue(result.contains("name=\"preserveMeName\""), "Failed to validate " + result);

        Assert.assertFalse(result.contains("<bean id=\"deleteMe\""), "Failed to validate " + result);
        Assert.assertFalse(result.contains("<bean name=\"deleteMeName\""), "Failed to validate " + result);
        Assert.assertTrue(result.contains("<bean class=\"com.consol.citrus.SampleClass\""), "Failed to validate " + result);
        Assert.assertFalse(result.contains("<bean class=\"com.consol.citrus.DeleteMe\""), "Failed to validate " + result);
        Assert.assertTrue(result.contains("<property name=\"class\" value=\"com.consol.citrus.DeleteMe\"/>"), "Failed to validate " + result);
    }

    @Test
    public void testUpdateBeanDefinition() throws Exception {
        File tempFile = createTempContextFile("citrus-context-update");

        SchemaModel helloSchema = new SchemaModelBuilder().withId("helloSchema").withLocation("newLocation").build();

        springBeanConfigService.updateBeanDefinition(tempFile, project, "helloSchema", helloSchema);

        String result = FileUtils.readToString(new FileInputStream(tempFile));

        Assert.assertTrue(result.contains("<citrus:schema id=\"helloSchema\" location=\"newLocation\"/>"), "Failed to validate " + result);
        Assert.assertTrue(result.contains("<property name=\"helloSchema\" value=\"some\"/>"), "Failed to validate " + result);
        Assert.assertTrue(result.contains("<!-- This is a comment -->"), "Failed to validate " + result);
        Assert.assertTrue(result.contains("<![CDATA[" + System.lineSeparator() + "              some" + System.lineSeparator() + "            ]]>" + System.lineSeparator()), "Failed to validate " + result);
        Assert.assertTrue(result.contains("<![CDATA[" + System.lineSeparator() + "              <some>" + System.lineSeparator() + "                <text>This is a CDATA text</text>" + System.lineSeparator()), "Failed to validate " + result);
    }

    @Test
    public void testGetBeanDefinition() throws Exception {
        File tempFile = createTempContextFile("citrus-context-find");

        SchemaModel schema = springBeanConfigService.getBeanDefinition(tempFile, project, "helloSchema", SchemaModel.class);

        Assert.assertEquals(schema.getId(), "helloSchema");
        Assert.assertEquals(schema.getLocation(), "classpath:com/consol/citrus/demo/sayHello.xsd");

        schema = springBeanConfigService.getBeanDefinition(tempFile, project, "helloSchemaExtended", SchemaModel.class);

        Assert.assertEquals(schema.getId(), "helloSchemaExtended");
        Assert.assertEquals(schema.getLocation(), "classpath:com/consol/citrus/demo/sayHelloExtended.xsd");
    }

    @Test
    public void testGetBeanDefinitions() throws Exception {
        File tempFile = createTempContextFile("citrus-context-find");

        List<SchemaModel> schemas = springBeanConfigService.getBeanDefinitions(tempFile, project, SchemaModel.class);

        Assert.assertEquals(schemas.size(), 2);
        Assert.assertEquals(schemas.get(0).getId(), "helloSchema");
        Assert.assertEquals(schemas.get(0).getLocation(), "classpath:com/consol/citrus/demo/sayHello.xsd");
        Assert.assertEquals(schemas.get(1).getId(), "helloSchemaExtended");
        Assert.assertEquals(schemas.get(1).getLocation(), "classpath:com/consol/citrus/demo/sayHelloExtended.xsd");
    }

    /**
     * Creates a temporary file in operating system and writes template content to file.
     * @param templateName
     * @return
     */
    private File createTempContextFile(String templateName) throws IOException {
        FileWriter writer = null;
        File tempFile;

        try {
            tempFile = File.createTempFile(templateName, ".xml");

            writer = new FileWriter(tempFile);
            writer.write(FileUtils.readToString(new ClassPathResource(templateName + ".xml", SpringBeanService.class)));
        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }

        return tempFile;
    }

}