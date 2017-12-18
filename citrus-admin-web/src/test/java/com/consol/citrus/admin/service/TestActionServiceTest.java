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

package com.consol.citrus.admin.service;

import com.consol.citrus.admin.marshal.TestActionMarshaller;
import com.consol.citrus.admin.model.Project;
import com.consol.citrus.model.testcase.core.EchoModel;
import com.consol.citrus.model.testcase.core.SendModel;
import com.consol.citrus.model.testcase.http.ClientRequestType;
import com.consol.citrus.model.testcase.http.SendRequestModel;
import com.consol.citrus.util.FileUtils;
import org.springframework.core.io.ClassPathResource;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.*;

/**
 * @author Christoph Deppisch
 */
public class TestActionServiceTest {

    private TestActionService testActionService = new TestActionService();
    private Project project;

    @BeforeMethod
    public void beforeMethod() {
        testActionService.setTestActionMarshaller(new TestActionMarshaller());
        testActionService.init();

        project = new Project();
    }

    @Test
    public void testAddActionDefinition() throws Exception {
        EchoModel echo1 = new EchoModel();
        echo1.setMessage("echo1");
        EchoModel echo2 = new EchoModel();
        echo2.setMessage("echo2");

        SendModel sendModel = new SendModel();
        sendModel.setEndpoint("myEndpoint");

        File tempFile = createTempContextFile("test-add");

        testActionService.addTestAction(tempFile, project, 1L, echo1);
        testActionService.addTestAction(tempFile, project, 1L, echo2);
        testActionService.addTestAction(tempFile, project, 3L, sendModel);

        String result = FileUtils.readToString(new FileInputStream(tempFile));

        Assert.assertTrue(result.contains("<sleep milliseconds=\"5000\">"), String.format("Failed to validate '%s'", result));
        Assert.assertTrue(result.contains("<message>echo1</message>"), String.format("Failed to validate '%s'", result));
        Assert.assertTrue(result.contains("<message>echo2</message>"), String.format("Failed to validate '%s'", result));
        Assert.assertTrue(result.contains("<send endpoint=\"myEndpoint\"/>"), String.format("Failed to validate '%s'", result));
    }

    @Test
    public void testAddActionDefinitionNamespace() throws Exception {
        SendRequestModel sendRequestModel = new SendRequestModel();
        sendRequestModel.setClient("myClient");

        ClientRequestType getRequest = new ClientRequestType();
        getRequest.setBody(new ClientRequestType.Body());
        getRequest.getBody().setData("Hello");
        sendRequestModel.setGET(getRequest);

        File tempFile = createTempContextFile("test-add");

        testActionService.addTestAction(tempFile, project, 1L, sendRequestModel);

        String result = FileUtils.readToString(new FileInputStream(tempFile));

        Assert.assertTrue(result.contains("<http:send-request client=\"myClient\">"), String.format("Failed to validate '%s'", result));
        Assert.assertTrue(result.contains("xmlns:http=\"http://www.citrusframework.org/schema/http/testcase\""), String.format("Failed to validate '%s'", result));
    }

    @Test
    public void testRemoveActionDefinition() throws Exception {
        File tempFile = createTempContextFile("test-remove");

        testActionService.removeTestAction(tempFile, project, 2L);
        testActionService.removeTestAction(tempFile, project, 3L);

        String result = FileUtils.readToString(new FileInputStream(tempFile));

        Assert.assertTrue(result.contains("<sleep milliseconds=\"5000\">"), String.format("Failed to validate '%s'", result));
        Assert.assertTrue(result.contains("<message>Citrus rocks!</message>"), String.format("Failed to validate '%s'", result));
        Assert.assertFalse(result.contains("<message>Hello Citrus!</message>"), String.format("Failed to validate '%s'", result));
        Assert.assertFalse(result.contains("<message>GoodBye Citrus!</message>"), String.format("Failed to validate '%s'", result));
    }
    
    @Test
    public void testUpdateActionDefinition() throws Exception {
        File tempFile = createTempContextFile("test-update");

        EchoModel echo = new EchoModel();
        echo.setMessage("Citrus rocks!");

        testActionService.updateTestAction(tempFile, project, 2L, echo);

        String result = FileUtils.readToString(new FileInputStream(tempFile));

        Assert.assertTrue(result.contains("<message>Citrus rocks!</message>"), String.format("Failed to validate '%s'", result));
        Assert.assertFalse(result.contains("<message>Hello Citrus!</message>"), String.format("Failed to validate '%s'", result));
        Assert.assertTrue(result.contains("<message>GoodBye Citrus!</message>"), String.format("Failed to validate '%s'", result));
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
            writer.write(FileUtils.readToString(new ClassPathResource(templateName + ".xml", TestActionService.class)));
        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }

        return tempFile;
    }

}