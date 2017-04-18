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

package com.consol.citrus.admin.converter.model.spring;

import com.consol.citrus.TestActor;
import com.consol.citrus.admin.model.spring.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Christoph Deppisch
 */
public class SpringBeanModelConverterTest {

    @Test
    public void testConvert() throws Exception {
        SpringBeanModelConverter<TestActor> converter = new SpringBeanModelConverter<>(TestActor.class);
        TestActor actor = new TestActor();
        actor.setName("testActor");
        SpringBean springBean = converter.convert("actor", actor);

        Assert.assertEquals(springBean.getId(), "actor");
        Assert.assertEquals(springBean.getClazz(), TestActor.class.getName());
        Assert.assertEquals(springBean.getProperties().size(), 2L);
        Assert.assertTrue(springBean.getProperties().stream().anyMatch(property -> property.getName().equals("name") && property.getValue().equals("testActor")));
        Assert.assertTrue(springBean.getProperties().stream().anyMatch(property -> property.getName().equals("disabled") && property.getValue().equals("false")));
    }

    @Test
    public void testGetJavaConfig() throws Exception {
        SpringBeanModelConverter<TestActor> converter = new SpringBeanModelConverter<>(TestActor.class);

        SpringBean springBean = new SpringBean();
        springBean.setId("");
        springBean.setClazz(TestActor.class.getName());
        Property name = new Property();
        name.setName("name");
        name.setValue("testActor");

        springBean.getProperties().add(name);

        String snippet = converter.getJavaConfig(springBean);

        Assert.assertEquals(snippet, "\n" +
                "\t@Bean\n" +
                "\tpublic TestActor springBean() {\n" +
                "\t\tTestActor springBean = new TestActor();\n" +
                "\t\tspringBean.setName(\"testActor\");\n" +
                "\t\treturn springBean;\n" +
                "\t}\n");
    }

}