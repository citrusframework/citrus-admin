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

package com.consol.citrus.admin.mock;

import com.consol.citrus.context.TestContext;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.oxm.Marshaller;
import org.springframework.util.ReflectionUtils;
import org.springframework.xml.transform.StringResult;

import java.util.Collections;

/**
 * @author Christoph Deppisch
 * @since 2.6
 */
public class Mocks {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(Mocks.class);

    /**
     * Constructs Spring application context mock.
     * @return
     */
    public static ApplicationContext getApplicationContextMock() {
        ApplicationContext mock = Mockito.mock(ApplicationContext.class);
        Marshaller marshaller = Mockito.mock(Marshaller.class);
        Mockito.when(mock.getBeansOfType(Marshaller.class)).thenReturn(Collections.singletonMap("mockMarshaller", marshaller));
        Mockito.when(mock.getBean(Marshaller.class)).thenReturn(marshaller);
        try {
            Mockito.doAnswer(invocation -> {
                StringResult result = invocation.getArgument(1);
                result.getWriter().write(invocation.getArgument(0).toString());
                return null;
            }).when(marshaller).marshal(Mockito.any(), Mockito.any(StringResult.class));
        } catch (java.io.IOException e) {
            log.warn("Failed to initialize object marshaller", e);
        }

        ObjectMapper mapper = Mockito.mock(ObjectMapper.class);
        Mockito.when(mock.getBeansOfType(ObjectMapper.class)).thenReturn(Collections.singletonMap("mockMapper", mapper));
        Mockito.when(mock.getBean(ObjectMapper.class)).thenReturn(mapper);

        ObjectWriter writer = Mockito.mock(ObjectWriter.class);
        Mockito.when(mapper.writer()).thenReturn(writer);
        try {
            Mockito.when(writer.writeValueAsString(Mockito.any())).thenAnswer(invocation -> invocation.getArgument(0).toString());
        } catch (JsonProcessingException e) {
            log.warn("Failed to initialize object mapper", e);
        }

        return mock;
    }

    /**
     * Constructs test context mock.
     * @return
     */
    public static TestContext getTestContextMock() {
        return Mockito.mock(TestContext.class);
    }

    /**
     * Inject Spring autowired fields in target instance with mocks.
     * @param target
     */
    public static void injectMocks(Object target) {
        ReflectionUtils.doWithFields(target.getClass(),
                field -> ReflectionUtils.setField(field, target, Mockito.mock(field.getType())),
                field -> {
                    if (field.isAnnotationPresent(Autowired.class)) {
                        if (!field.isAccessible()) {
                            ReflectionUtils.makeAccessible(field);
                        }

                        return true;
                    }

                    return false;
                });
    }
}
