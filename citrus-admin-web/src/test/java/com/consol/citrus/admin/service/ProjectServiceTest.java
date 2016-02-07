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
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Christoph Deppisch
 */
public class ProjectServiceTest {

    @Test
    public void testLoadMavenProject() throws Exception {
        ProjectService projectService = new ProjectService();
        projectService.load(new ClassPathResource("maven").getFile().getCanonicalPath());

        Project project = projectService.getActiveProject();
        Assert.assertNotNull(project);
        Assert.assertEquals(project.getProjectHome(), new ClassPathResource("maven").getFile().getCanonicalPath());
        Assert.assertEquals(project.getName(), "citrus-integration-tests");
        Assert.assertEquals(project.getVersion(), "2.6-SNAPSHOT");
        Assert.assertEquals(project.getBasePackage(), "com.consol.citrus");
        Assert.assertEquals(project.getDescription(), "");
    }

    @Test
    public void testLoadAntProject() throws Exception {
        ProjectService projectService = new ProjectService();
        projectService.load(new ClassPathResource("ant").getFile().getCanonicalPath());

        Project project = projectService.getActiveProject();
        Assert.assertNotNull(project);
        Assert.assertEquals(project.getProjectHome(), new ClassPathResource("ant").getFile().getCanonicalPath());
        Assert.assertEquals(project.getName(), "citrus-sample");
        Assert.assertEquals(project.getVersion(), "2.6-SNAPSHOT");
        Assert.assertEquals(project.getBasePackage(), "com.consol.citrus");
        Assert.assertEquals(project.getDescription(), "This is a sample Citrus ANT build");
    }
}