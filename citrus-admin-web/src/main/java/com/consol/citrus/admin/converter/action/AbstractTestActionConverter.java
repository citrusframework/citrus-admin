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

package com.consol.citrus.admin.converter.action;

import com.consol.citrus.TestAction;
import com.consol.citrus.admin.converter.AbstractObjectConverter;
import com.consol.citrus.admin.exception.ApplicationRuntimeException;
import com.consol.citrus.admin.model.Property;
import com.consol.citrus.admin.model.TestActionModel;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Christoph Deppisch
 */
public abstract class AbstractTestActionConverter<S, R extends TestAction> extends AbstractObjectConverter<TestActionModel, S> implements TestActionConverter<S, R> {

    private final String actionType;

    /**
     * Default constructor using action type reference.
     * @param actionType
     */
    protected AbstractTestActionConverter(String actionType) {
        this.actionType = actionType;
    }

    @Override
    public TestActionModel convert(S model) {
        TestActionModel actionModel = new TestActionModel(getActionType(), getSourceModelClass());

        ReflectionUtils.doWithFields(getSourceModelClass(), field -> {
            Property property = property(field.getName(), getDisplayName(getFieldName(field.getName())), model, getDefaultValue(field), isRequiredField(field))
                    .options(getFieldOptions(field))
                    .optionType(getOptionType(field));

            actionModel.add(property);
        }, field -> include(model, field));

        return actionModel;
    }

    @Override
    public S convertBack(TestActionModel definition) {
        try {
            S instance = getSourceModelClass().newInstance();

            for (Property property : definition.getProperties()) {
                if (property.getValue() != null) {
                    Method setter = findSetter(getSourceModelClass(), property.getFieldName());
                    ReflectionUtils.invokeMethod(setter, instance, getMethodArgument(setter.getParameterTypes()[0], property.getValue()));
                }
            }

            return instance;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ApplicationRuntimeException("Failed to instantiate model class", e);
        }
    }

    /**
     * Decides if field should be included in model conversion.
     * @param model
     * @param field
     * @return
     */
    protected boolean include(S model, Field field) {
        return true;
    }

    @Override
    protected Map<String, String> getDisplayNameMappings() {
        Map<String, String> mappings = new HashMap<>();
        mappings.put("actor", "TestActor");
        return mappings;
    }

    /**
     * Finds setter method on class for given field name.
     * @param modelClass
     * @param fieldName
     * @return
     */
    private Method findSetter(Class<S> modelClass, String fieldName) {
        final Method[] setter = {null};

        ReflectionUtils.doWithMethods(modelClass, method -> {
            if (method.getName().equals("set" + StringUtils.capitalize(fieldName))) {
                setter[0] = method;
            }
        }, method -> method.getName().startsWith("set"));

        if (setter[0] == null) {
            throw new ApplicationRuntimeException(String.format("Unable to find proper setter for field '%s' on model class '%s'", fieldName, modelClass));
        }

        return setter[0];
    }

    /**
     * Gets properly typed method argument.
     * @param parameterType
     * @param value
     * @return
     */
    private <T> T getMethodArgument(Class<T> parameterType, Object value) {
        if (parameterType.isInstance(value)) {
            return parameterType.cast(value);
        }

        try {
            return new SimpleTypeConverter().convertIfNecessary(value, parameterType);
        } catch (ConversionNotSupportedException e) {
            if (String.class.equals(parameterType)) {
                return (T) String.valueOf(value);
            }

            throw new ApplicationRuntimeException("Unable to convert method argument type", e);
        }
    }

    @Override
    public String getActionType() {
        return actionType;
    }

    @Override
    public Class<TestActionModel> getTargetModelClass() {
        return TestActionModel.class;
    }
}
