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

import com.consol.citrus.Citrus;
import com.consol.citrus.admin.model.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

/**
 * @author Christoph Deppisch
 */
@ContextConfiguration(locations = { "classpath:citrus-admin-unit-context.xml" })
public class TestCaseServiceTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private TestCaseService testCaseService;

    private Project project = Mockito.mock(Project.class);

    @Test
    public void testGetTestPackages() throws IOException {
        reset(project);
        when(project.getSettings()).thenReturn(new ProjectSettings());
        String projectHome = new ClassPathResource("projects/sample").getFile().getAbsolutePath();
        when(project.getProjectHome()).thenReturn(projectHome);
        when(project.getJavaDirectory()).thenReturn(projectHome + "/src/test/java/");

        List<TestGroup> testPackages = testCaseService.getTestPackages(project);

        Assert.assertNotNull(testPackages);
        Assert.assertEquals(testPackages.size(), 3L);
        Assert.assertEquals(testPackages.get(0).getName(), "bar");
        Assert.assertEquals(testPackages.get(0).getTests().size(), 1L);
        Assert.assertEquals(testPackages.get(1).getName(), "foo");
        Assert.assertEquals(testPackages.get(1).getTests().size(), 2L);
        Assert.assertEquals(testPackages.get(2).getName(), "javadsl");
        Assert.assertEquals(testPackages.get(2).getTests().size(), 4L);

        assertTestPresent(testPackages.get(0).getTests(), "BarTest", "BarTest", "barTest");
        assertTestPresent(testPackages.get(1).getTests(), "FooTest", "FooTest", "FooTest");
        assertTestPresent(testPackages.get(1).getTests(), "WithoutLastUpdatedOnTest", "WithoutLastUpdatedOnTest", "withoutLastUpdatedOnTest");
        assertTestPresent(testPackages.get(2).getTests(), "CitrusJavaTest.fooTest", "CitrusJavaTest", "fooTest");
        assertTestPresent(testPackages.get(2).getTests(), "BarJavaTest", "CitrusJavaTest", "barTest");
        assertTestPresent(testPackages.get(2).getTests(), "DataProviderJavaTest.fooProviderTest", "DataProviderJavaTest", "fooProviderTest");
        assertTestPresent(testPackages.get(2).getTests(), "BarProviderTest", "DataProviderJavaTest", "barProviderTest");
    }

    private void assertTestPresent(List<com.consol.citrus.admin.model.Test> tests, String name, String className, String methodName) {
        for (com.consol.citrus.admin.model.Test test : tests) {
            if (test.getName().equals(name) && test.getClassName().equals(className) && test.getMethodName().equals(methodName)) {
                return;
            }
        }

        Assert.fail(String.format("Missing test: name='%s', className='%s', methodName='%s'", name, className, methodName));
    }

    @Test
    public void testGetTestDetailXml() throws Exception {
        reset(project);
        when(project.getSettings()).thenReturn(new ProjectSettings());
        when(project.getProjectHome()).thenReturn(new ClassPathResource("projects/sample").getFile().getAbsolutePath());
        when(project.getAbsolutePath("foo/FooTest.xml")).thenReturn(new ClassPathResource("projects/sample/src/test/resources/foo/FooTest.xml").getFile().getAbsolutePath());

        TestDetail testDetail = testCaseService.getTestDetail(project, new com.consol.citrus.admin.model.Test("foo", "FooTest", "fooTest", "FooTest", TestType.XML));

        Assert.assertEquals(testDetail.getName(), "FooTest");
        Assert.assertEquals(testDetail.getPackageName(), "foo");
        Assert.assertEquals(testDetail.getRelativePath(), "foo/FooTest.xml");
        Assert.assertEquals(testDetail.getType(), TestType.XML);
        Assert.assertEquals(testDetail.getAuthor(), "Christoph");
        Assert.assertEquals(testDetail.getLastModified().longValue(), 1315222929000L);
        Assert.assertEquals(testDetail.getDescription(), "This is a sample test");
        Assert.assertTrue(testDetail.getFile().endsWith("foo/FooTest"));
        Assert.assertEquals(testDetail.getActions().size(), 3L);
        Assert.assertEquals(testDetail.getActions().get(0).getType(), "echo");
        Assert.assertEquals(testDetail.getActions().get(1).getType(), "send");
        Assert.assertEquals(testDetail.getActions().get(2).getType(), "send");

        Assert.assertEquals(testDetail.getActions().get(0).getProperties().size(), 2L);
        Assert.assertEquals(testDetail.getActions().get(0).getProperties().get(0).getId(), "description");
        Assert.assertEquals(testDetail.getActions().get(0).getProperties().get(1).getId(), "message");

        Assert.assertEquals(testDetail.getActions().get(1).getProperties().size(), 4L);
        Assert.assertEquals(testDetail.getActions().get(1).getProperties().get(0).getId(), "endpoint");
        Assert.assertEquals(testDetail.getActions().get(1).getProperties().get(0).getValue(), "sampleEndpoint");
        Assert.assertEquals(testDetail.getActions().get(1).getProperties().get(1).getId(), "message.data");
        Assert.assertEquals(testDetail.getActions().get(1).getProperties().get(1).getValue(), "Hello");

        Assert.assertEquals(testDetail.getActions().get(2).getProperties().size(), 4L);
        Assert.assertEquals(testDetail.getActions().get(2).getProperties().get(0).getId(), "endpoint");
        Assert.assertEquals(testDetail.getActions().get(2).getProperties().get(0).getValue(), "samplePayloadEndpoint");
        Assert.assertEquals(testDetail.getActions().get(2).getProperties().get(1).getId(), "message.payload");
        Assert.assertEquals(testDetail.getActions().get(2).getProperties().get(1).getValue(), "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Test xmlns=\"http://www.citrusframework.org\" xmlns:spring=\"http://www.springframework.org/schema/beans\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">Hello</Test>");
    }

    @Test
    public void testGetTestDetailXmlWithoutLastUpdatedOn() throws Exception {
        reset(project);
        when(project.getSettings()).thenReturn(new ProjectSettings());
        when(project.getProjectHome()).thenReturn(new ClassPathResource("projects/sample").getFile().getAbsolutePath());
        when(project.getAbsolutePath("foo/WithoutLastUpdatedOnTest.xml")).thenReturn(new ClassPathResource("projects/sample/src/test/resources/foo/WithoutLastUpdatedOnTest.xml").getFile().getAbsolutePath());

        TestDetail testDetail = testCaseService.getTestDetail(project, new com.consol.citrus.admin.model.Test("foo", "WithoutLastUpdatedOnTest", "withoutLastUpdatedOnTest", "WithoutLastUpdatedOnTest", TestType.XML));

        Assert.assertNull(testDetail.getLastModified());
    }

    @Test
    public void testGetTestDetailJava() throws Exception {
        reset(project);
        when(project.getName()).thenReturn("citrus-core");
        when(project.getVersion()).thenReturn(Citrus.getVersion());
        when(project.isMavenProject()).thenReturn(true);
        when(project.getSettings()).thenReturn(new ProjectSettings());
        String projectHome = new ClassPathResource("projects/maven").getFile().getAbsolutePath();
        when(project.getProjectHome()).thenReturn(projectHome);
        when(project.getJavaDirectory()).thenReturn(projectHome + "projects/maven/src/test/java/");
        when(project.getMavenPomFile()).thenReturn(new ClassPathResource("projects/maven/pom.xml").getFile());

        TestDetail testDetail = testCaseService.getTestDetail(project, new com.consol.citrus.admin.model.Test("com.consol.citrus.admin.javadsl", "CitrusJavaTest", "fooTest", "CitrusJavaTest.fooTest", TestType.JAVA));

        Assert.assertEquals(testDetail.getName(), "CitrusJavaTest.fooTest");
        Assert.assertEquals(testDetail.getPackageName(), "com.consol.citrus.admin.javadsl");
        Assert.assertEquals(testDetail.getRelativePath(), "com/consol/citrus/admin/javadsl/CitrusJavaTest.java");
        Assert.assertEquals(testDetail.getType(), TestType.JAVA);
        Assert.assertTrue(testDetail.getFile().endsWith("javadsl/CitrusJavaTest"));
        Assert.assertEquals(testDetail.getActions().size(), 1L);
        Assert.assertEquals(testDetail.getActions().get(0).getType(), "echo");

        Assert.assertEquals(testDetail.getActions().get(0).getProperties().size(), 2L);
        Assert.assertEquals(testDetail.getActions().get(0).getProperties().get(0).getId(), "description");
        Assert.assertEquals(testDetail.getActions().get(0).getProperties().get(1).getId(), "message");
    }
}