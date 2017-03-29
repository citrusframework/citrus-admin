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

package com.consol.citrus.admin.service;

import com.consol.citrus.admin.connector.WebSocketPushEventsListener;
import com.consol.citrus.admin.exception.ApplicationRuntimeException;
import com.consol.citrus.admin.model.Project;
import com.consol.citrus.admin.model.spring.SpringBean;
import com.consol.citrus.admin.service.spring.SpringBeanService;
import com.consol.citrus.util.FileUtils;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.util.*;

import static org.mockito.Mockito.*;

/**
 * @author Christoph Deppisch
 */
public class ProjectServiceTest {

    private ProjectService projectService;
    private FileBrowserService fileBrowserService = new FileBrowserService();
    private SpringBeanService springBeanService = Mockito.mock(SpringBeanService.class);
    private Environment environment = Mockito.mock(Environment.class);

    @BeforeMethod
    public void setup() {
        projectService = new ProjectService();
        projectService.setFileBrowserService(fileBrowserService);
        projectService.setSpringBeanService(springBeanService);
        projectService.setEnvironment(environment);
    }

    @Test(dataProvider = "projectProvider")
    public void testLoadAndSaveProject(String projectHome, String description, Map<String, String> properties, boolean connectorActive) throws Exception {
        String home = new ClassPathResource(projectHome).getFile().getCanonicalPath();
        Project existing = new Project(home);
        if (existing.getProjectInfoFile().exists()) {
            if (!existing.getProjectInfoFile().delete()) {
                Assert.fail();
            }
        }

        projectService.load(home);

        Project project = projectService.getActiveProject();
        Assert.assertNotNull(project);
        Assert.assertEquals(project.getProjectHome(), home);
        Assert.assertEquals(project.getName(), "citrus-integration-tests");
        Assert.assertEquals(project.getVersion(), "1.0.0");
        Assert.assertEquals(project.getSettings().getCitrusVersion(), "2.7.2-SNAPSHOT");
        Assert.assertEquals(project.getSettings().getBasePackage(), "com.consol.citrus");
        Assert.assertEquals(project.getSettings().isUseConnector(), true);
        Assert.assertEquals(project.getSettings().isConnectorActive(), connectorActive);
        Assert.assertEquals(project.getDescription(), description);

        projectService.getActiveProject().getSettings().setUseConnector(false);
        projectService.saveProject(projectService.getActiveProject());

        projectService.load(home);
        project = projectService.getActiveProject();
        Assert.assertEquals(project.getName(), "citrus-integration-tests");
        Assert.assertEquals(project.getVersion(), "1.0.0");
        Assert.assertEquals(project.getSettings().getCitrusVersion(), "2.7.2-SNAPSHOT");
        Assert.assertEquals(project.getSettings().getBasePackage(), "com.consol.citrus");
        Assert.assertEquals(project.getSettings().isUseConnector(), false);
        Assert.assertEquals(project.getSettings().isConnectorActive(), connectorActive);
        Assert.assertEquals(project.getDescription(), description);
    }

    @Test(dataProvider = "projectProvider")
    public void testGetProjectProperties(String projectHome, String description, Map<String, String> properties, boolean connectorActive) throws Exception {
        Project testProject = new Project(new ClassPathResource(projectHome).getFile().getCanonicalPath());

        projectService.setActiveProject(testProject);
        Properties projectProperties = projectService.getProjectProperties();

        Assert.assertEquals(projectProperties.size(), properties.size());
        for (Map.Entry<String, String> propEntry : properties.entrySet()) {
            Assert.assertEquals(projectProperties.get(propEntry.getKey()), propEntry.getValue());
        }
    }

    @Test
    public void testGetProjectContextConfigFile() throws Exception {
        Project testProject = new Project(new ClassPathResource("projects/maven").getFile().getCanonicalPath());
        projectService.setActiveProject(testProject);

        File configFile = projectService.getProjectContextConfigFile();
        Assert.assertTrue(configFile.exists());
        Assert.assertEquals(configFile.getName(), "citrus-context.xml");
    }

    @Test
    public void testManageConnector() throws Exception {
        Project testProject = new Project(new ClassPathResource("projects/maven").getFile().getCanonicalPath());
        projectService.setActiveProject(testProject);

        Assert.assertFalse(FileUtils.readToString(new FileSystemResource(testProject.getMavenPomFile())).contains("citrus-admin-connector"));

        when(springBeanService.getBeanDefinition(any(File.class), eq(testProject), eq(WebSocketPushEventsListener.class.getSimpleName()), eq(SpringBean.class))).thenReturn(null);
        when(environment.getProperty("local.server.port", "8080")).thenReturn("8080");
        projectService.addConnector();

        Assert.assertTrue(FileUtils.readToString(new FileSystemResource(testProject.getMavenPomFile())).contains("citrus-admin-connector"));

        when(springBeanService.getBeanNames(any(File.class), eq(testProject), eq(WebSocketPushEventsListener.class.getName()))).thenReturn(Collections.singletonList(WebSocketPushEventsListener.class.getSimpleName()));
        projectService.removeConnector();

        Assert.assertFalse(FileUtils.readToString(new FileSystemResource(testProject.getMavenPomFile())).contains("citrus-admin-connector"));

        verify(springBeanService).addBeanDefinition(any(File.class), eq(testProject), any(SpringBean.class));
        verify(springBeanService).removeBeanDefinition(any(File.class), eq(testProject), eq(WebSocketPushEventsListener.class.getSimpleName()));
    }

    @Test(expectedExceptions = { ApplicationRuntimeException.class },
            expectedExceptionsMessageRegExp = "Invalid project home - not a proper Citrus project")
    public void testInvalidProjectHome() {
        projectService.load("invalid");
    }

    @DataProvider
    public Object[][] projectProvider() {
        HashMap<String, String> defaultProjectProperties = new HashMap<>();
        defaultProjectProperties.put("project.name", "citrus-integration-tests");
        defaultProjectProperties.put("project.version", "v1.0");

        return new Object[][] {
            new Object[] {"projects/maven", "This is a sample Citrus Maven build", defaultProjectProperties, false},
            new Object[] {"projects/maven_connector", "This is a sample Citrus Maven build", Collections.emptyMap(), true},
            new Object[] {"projects/ant", "This is a sample Citrus ANT build", defaultProjectProperties, false}
        };
    }
}