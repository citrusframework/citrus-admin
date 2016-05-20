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
        when(project.getProjectHome()).thenReturn(new ClassPathResource("projects/sample").getFile().getAbsolutePath());

        List<TestGroup> testPackages = testCaseService.getTestPackages(project);

        Assert.assertNotNull(testPackages);
        Assert.assertEquals(testPackages.size(), 3L);
        Assert.assertEquals(testPackages.get(0).getName(), "bar");
        Assert.assertEquals(testPackages.get(0).getTests().size(), 1L);
        Assert.assertEquals(testPackages.get(1).getName(), "foo");
        Assert.assertEquals(testPackages.get(1).getTests().size(), 1L);
        Assert.assertEquals(testPackages.get(2).getName(), "javadsl");
        Assert.assertEquals(testPackages.get(2).getTests().size(), 2L);

        Assert.assertEquals(testPackages.get(0).getTests().get(0).getName(), "BarTest");
        Assert.assertEquals(testPackages.get(0).getTests().get(0).getClassName(), "BarTest");
        Assert.assertEquals(testPackages.get(0).getTests().get(0).getMethodName(), "barTest");
        Assert.assertEquals(testPackages.get(1).getTests().get(0).getName(), "FooTest");
        Assert.assertEquals(testPackages.get(1).getTests().get(0).getClassName(), "FooTest");
        Assert.assertEquals(testPackages.get(1).getTests().get(0).getMethodName(), "FooTest");
        Assert.assertEquals(testPackages.get(2).getTests().get(0).getName(), "CitrusJavaTest.fooTest");
        Assert.assertEquals(testPackages.get(2).getTests().get(0).getClassName(), "CitrusJavaTest");
        Assert.assertEquals(testPackages.get(2).getTests().get(0).getMethodName(), "fooTest");
        Assert.assertEquals(testPackages.get(2).getTests().get(1).getName(), "BarJavaTest");
        Assert.assertEquals(testPackages.get(2).getTests().get(1).getClassName(), "CitrusJavaTest");
        Assert.assertEquals(testPackages.get(2).getTests().get(1).getMethodName(), "barTest");
    }

    @Test
    public void testGetTestDetailXml() throws Exception {
        reset(project);
        when(project.getSettings()).thenReturn(new ProjectSettings());
        when(project.getProjectHome()).thenReturn(new ClassPathResource("projects/sample").getFile().getAbsolutePath());

        TestDetail testDetail = testCaseService.getTestDetail(project, new com.consol.citrus.admin.model.Test("foo", "FooTest", "fooTest", "FooTest", TestType.XML));

        Assert.assertEquals(testDetail.getName(), "FooTest");
        Assert.assertEquals(testDetail.getPackageName(), "foo");
        Assert.assertEquals(testDetail.getType(), TestType.XML);
        Assert.assertEquals(testDetail.getAuthor(), "Christoph");
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
        Assert.assertEquals(testDetail.getActions().get(2).getProperties().get(1).getValue(), "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Test xmlns=\"http://www.citrusframework.org\" xmlns:jms=\"http://www.citrusframework.org/schema/jms/testcase\" xmlns:spring=\"http://www.springframework.org/schema/beans\" xmlns:ws=\"http://www.citrusframework.org/schema/ws/testcase\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">Hello</Test>");
    }

    @Test
    public void testGetTestDetailJava() throws Exception {
        reset(project);
        when(project.getName()).thenReturn("citrus-core");
        when(project.getVersion()).thenReturn(Citrus.getVersion());
        when(project.isMavenProject()).thenReturn(true);
        when(project.getSettings()).thenReturn(new ProjectSettings());
        when(project.getProjectHome()).thenReturn(new ClassPathResource("").getFile().getAbsolutePath());

        TestDetail testDetail = testCaseService.getTestDetail(project, new com.consol.citrus.admin.model.Test("com.consol.citrus.admin.javadsl", "CitrusJavaTest", "fooTest", "CitrusJavaTest.fooTest", TestType.JAVA));

        Assert.assertEquals(testDetail.getName(), "CitrusJavaTest.fooTest");
        Assert.assertEquals(testDetail.getPackageName(), "com.consol.citrus.admin.javadsl");
        Assert.assertEquals(testDetail.getType(), TestType.JAVA);
        Assert.assertTrue(testDetail.getFile().endsWith("javadsl/CitrusJavaTest"));
        Assert.assertEquals(testDetail.getActions().size(), 1L);
        Assert.assertEquals(testDetail.getActions().get(0).getType(), "echo");

        Assert.assertEquals(testDetail.getActions().get(0).getProperties().size(), 2L);
        Assert.assertEquals(testDetail.getActions().get(0).getProperties().get(0).getId(), "description");
        Assert.assertEquals(testDetail.getActions().get(0).getProperties().get(1).getId(), "message");
    }
}