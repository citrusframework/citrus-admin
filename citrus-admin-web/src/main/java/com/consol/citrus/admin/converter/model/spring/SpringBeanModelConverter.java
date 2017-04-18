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

import com.consol.citrus.admin.converter.model.AbstractModelConverter;
import com.consol.citrus.admin.model.spring.Property;
import com.consol.citrus.admin.model.spring.SpringBean;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * @author Christoph Deppisch
 */
public class SpringBeanModelConverter<T> extends AbstractModelConverter<SpringBean, T> {
    /**
     * Default constructor using source model type.
     *
     * @param sourceModelType
     */
    public SpringBeanModelConverter(Class<T> sourceModelType) {
        super(SpringBean.class, sourceModelType);

        addDecorator(new MethodCallDecorator("setProperties") {
            @Override
            public String decorate(String target, String code, Object arg) {
                StringBuilder codeBuilder = new StringBuilder();
                ((List<Property>) arg).forEach(property -> codeBuilder.append(String.format("\t\t%s.set%s(\"%s\");%n", target, StringUtils.capitalize(property.getName()), property.getValue())));

                return codeBuilder.toString();
            }

            @Override
            public boolean allowMethodCall(Object arg) {
                return ((List<Property>) arg).size() > 0;
            }
        });

        addDecorator(new MethodCallDecorator("setClazz") {
            @Override
            public boolean allowMethodCall(Object arg) {
                return false;
            }
        });

        addDecorator(new MethodCallDecorator("setAutowire") {
            @Override
            public boolean allowMethodCall(Object arg) {
                return false;
            }
        });

        addDecorator(new MethodCallDecorator("setOtherAttributes") {
            @Override
            public boolean allowMethodCall(Object arg) {
                return false;
            }
        });
    }

    @Override
    public SpringBean convert(String id, T model) {
        SpringBean converted = super.convert(model);

        ReflectionUtils.doWithMethods(model.getClass(), method -> {
            Object object = ReflectionUtils.invokeMethod(method, model);
            if (object != null) {
                Property property = new Property();
                property.setName(getMethodCall(method.getName()));
                property.setValue(object.toString());

                converted.getProperties().add(property);
            }
        }, method -> (method.getName().startsWith("get") || method.getName().startsWith("is"))
                && !method.getName().equals("getClass")
                && method.getParameterCount() == 0);

        converted.setClazz(model.getClass().getName());
        converted.setId(id);
        return converted;
    }

    @Override
    public String getJavaConfig(SpringBean model) {
        return getJavaConfig(model, model.getId());
    }

    /**
     * Map setter method call based on getter method names.
     * @param methodName
     * @return
     */
    private String getMethodCall(String methodName) {
        if (methodName.startsWith("get")) {
            return StringUtils.uncapitalize(methodName.substring(3));
        } else if (methodName.startsWith("is")) {
            return StringUtils.uncapitalize(methodName.substring(2));
        }

        return methodName;
    }

}
