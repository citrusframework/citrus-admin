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

package com.consol.citrus.admin.service.test;

import com.consol.citrus.admin.model.Project;
import com.consol.citrus.admin.model.TestType;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Christoph Deppisch
 */
public class AnnotationTestProviderTest {

    private AnnotationTestProvider testProvider = new AnnotationTestProvider();

    @Test
    public void testFindTests() throws IOException {
        Project project = new Project(new ClassPathResource("projects/maven").getFile().getAbsolutePath());
        List<com.consol.citrus.admin.model.Test> tests = testProvider.findTests(project, Arrays.stream(new PathMatchingResourcePatternResolver().getResources("projects/maven/src/test/java/**/*.java"))
                .map(resource -> {
                    try {
                        return resource.getFile();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList()));

        Assert.assertEquals(tests.size(), 1L);
        Assert.assertEquals(tests.get(0).getPackageName(), "foo");
        Assert.assertEquals(tests.get(0).getClassName(), "FooTest");
        Assert.assertEquals(tests.get(0).getMethodName(), "FooTest");
        Assert.assertEquals(tests.get(0).getName(), "FooTest");
        Assert.assertEquals(tests.get(0).getType(), TestType.XML);
    }

}