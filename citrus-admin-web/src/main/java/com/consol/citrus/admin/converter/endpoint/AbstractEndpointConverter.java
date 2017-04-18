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

package com.consol.citrus.admin.converter.endpoint;

import com.consol.citrus.TestActor;
import com.consol.citrus.admin.converter.AbstractObjectConverter;
import com.consol.citrus.admin.exception.ApplicationRuntimeException;
import com.consol.citrus.admin.model.EndpointModel;
import com.consol.citrus.admin.model.Property;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.xml.bind.annotation.XmlSchema;
import java.lang.reflect.Method;

/**
 * Abstract endpoint converter provides basic endpoint property handling by Java reflection on JAXb objects.
 *
 * @author Christoph Deppisch
 */
public abstract class AbstractEndpointConverter<S> extends AbstractObjectConverter<EndpointModel, S> implements EndpointConverter<S> {

    /**
     * Adds basic endpoint properties using reflection on sourceModel objects.
     * @param endpointModel
     * @param sourceModel
     */
    protected void addEndpointProperties(EndpointModel endpointModel, S sourceModel) {
        endpointModel.add(property("timeout", sourceModel, "5000"));
        endpointModel.add(property("actor", "TestActor", sourceModel).optionKey(TestActor.class.getName()));
    }

    @Override
    public String getEndpointType() {
        String endpointNamespace = getSourceModelClass().getPackage().getAnnotation(XmlSchema.class).namespace();
        return endpointNamespace.substring("http://www.citrusframework.org/schema/".length(), endpointNamespace.indexOf("/config"));
    }

    @Override
    public S convertBack(EndpointModel definition) {
        try {
            S instance = getSourceModelClass().newInstance();

            ReflectionUtils.invokeMethod(findSetter(getSourceModelClass(), "id"), instance, definition.getId());

            for (Property property : definition.getProperties()) {
                if (StringUtils.hasText(property.getValue())) {
                    Method setter = findSetter(getSourceModelClass(), property.getFieldName());
                    ReflectionUtils.invokeMethod(setter, instance, getMethodArgument(setter, property.getValue()));
                }
            }

            return instance;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ApplicationRuntimeException("Failed to instantiate model class", e);
        }
    }

    @Override
    public Class<EndpointModel> getTargetModelClass() {
        return EndpointModel.class;
    }

    private Method findSetter(Class<S> modelClass, String fieldName) {
        final Method[] setter = {null};

        ReflectionUtils.doWithMethods(modelClass, new ReflectionUtils.MethodCallback() {
            @Override
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                if (method.getName().equals("set" + StringUtils.capitalize(fieldName))) {
                    setter[0] = method;
                }
            }
        }, new ReflectionUtils.MethodFilter() {
            @Override
            public boolean matches(Method method) {
                return method.getName().startsWith("set");
            }
        });

        if (setter[0] == null) {
            throw new ApplicationRuntimeException(String.format("Unable to find proper setter for field '%s' on model class '%s'", fieldName, modelClass));
        }

        return setter[0];
    }

    /**
     *
     * @param setter
     * @param value
     * @return
     */
    private Object getMethodArgument(Method setter, String value) {
        Class<?> type = setter.getParameterTypes()[0];
        if (type.isInstance(value)) {
            return type.cast(value);
        }

        try {
            return new SimpleTypeConverter().convertIfNecessary(value, type);
        } catch (ConversionNotSupportedException e) {
            if (String.class.equals(type)) {
                return value.toString();
            }

            throw new ApplicationRuntimeException("Unable to convert method argument type", e);
        }
    }
}
