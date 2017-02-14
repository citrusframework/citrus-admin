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

package com.consol.citrus.admin.mock;

import com.consol.citrus.context.TestContext;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.oxm.Marshaller;
import org.springframework.util.ReflectionUtils;

/**
 * @author Christoph Deppisch
 * @since 2.6
 */
public class Mocks {

    /**
     * Constructs Spring application context mock.
     * @return
     */
    public static ApplicationContext getApplicationContextMock() {
        ApplicationContext mock = Mockito.mock(ApplicationContext.class);
        Mockito.when(mock.getBean(Marshaller.class)).thenReturn(Mockito.mock(Marshaller.class));
        return mock;
    }

    /**
     * Constructs test context mock.
     * @return
     */
    public static TestContext getTestContextMock() {
        TestContext mock = Mockito.mock(TestContext.class);
        return mock;
    }

    /**
     * Inject Spring autowired fields in target instance extendAndGet mocks.
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
