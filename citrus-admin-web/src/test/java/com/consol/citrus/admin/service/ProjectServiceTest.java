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

import com.consol.citrus.admin.model.Project;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.CollectionUtils;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.File;
import java.util.Properties;

/**
 * @author Christoph Deppisch
 */
public class ProjectServiceTest {

    private ProjectService projectService;
    private FileBrowserService fileBrowserService = new FileBrowserService();

    @BeforeMethod
    public void setup() {
        projectService = new ProjectService();
        projectService.fileBrowserService = fileBrowserService;
    }

    @Test(dataProvider = "projectProvider")
    public void testLoadAndSaveProject(String projectHome, String description) throws Exception {
        if (projectService.getProjectSettingsFile(new ClassPathResource(projectHome).getFile().getCanonicalPath()).exists()) {
            if (!projectService.getProjectSettingsFile(new ClassPathResource(projectHome).getFile().getCanonicalPath()).delete()) {
                Assert.fail();
            }
        }

        projectService.load(new ClassPathResource(projectHome).getFile().getCanonicalPath());

        Project project = projectService.getActiveProject();
        Assert.assertNotNull(project);
        Assert.assertEquals(project.getProjectHome(), new ClassPathResource(projectHome).getFile().getCanonicalPath());
        Assert.assertEquals(project.getName(), "citrus-integration-tests");
        Assert.assertEquals(project.getVersion(), "1.0.0");
        Assert.assertEquals(project.getSettings().getCitrusVersion(), "2.6-SNAPSHOT");
        Assert.assertEquals(project.getSettings().getBasePackage(), "com.consol.citrus");
        Assert.assertEquals(project.getDescription(), description);

        projectService.getActiveProject().setDescription("New description");
        projectService.saveProject();

        projectService.load(new ClassPathResource(projectHome).getFile().getCanonicalPath());
        project = projectService.getActiveProject();
        Assert.assertEquals(project.getName(), "citrus-integration-tests");
        Assert.assertEquals(project.getVersion(), "1.0.0");
        Assert.assertEquals(project.getSettings().getCitrusVersion(), "2.6-SNAPSHOT");
        Assert.assertEquals(project.getSettings().getBasePackage(), "com.consol.citrus");
        Assert.assertEquals(project.getDescription(), "New description");
    }

    @Test(dataProvider = "projectProvider")
    public void testGetProjectProperties(String projectHome, String description) throws Exception {
        Project testProject = new Project(new ClassPathResource(projectHome).getFile().getCanonicalPath());

        projectService.setActiveProject(testProject);
        Properties properties = projectService.getProjectProperties();

        Assert.assertFalse(CollectionUtils.isEmpty(properties));
        Assert.assertEquals(properties.size(), 2L);
        Assert.assertEquals(properties.get("project.name"), "citrus-integration-tests");
        Assert.assertEquals(properties.get("project.description"), description);
    }

    @Test
    public void testGetProjectContextConfigFile() throws Exception {
        Project testProject = new Project(new ClassPathResource("test-project/maven").getFile().getCanonicalPath());
        projectService.setActiveProject(testProject);

        File configFile = projectService.getProjectContextConfigFile();
        Assert.assertTrue(configFile.exists());
        Assert.assertEquals(configFile.getName(), "citrus-context.xml");
    }

    @DataProvider
    public Object[][] projectProvider() {
        return new Object[][] {
            new Object[] {"test-project/maven", "This is a sample Citrus Maven build"},
            new Object[] {"test-project/ant", "This is a sample Citrus ANT build"}
        };
    }
}