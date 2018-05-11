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

package com.consol.citrus.admin.converter;

import com.consol.citrus.admin.model.Property;
import com.consol.citrus.admin.service.ProjectService;
import com.consol.citrus.variable.VariableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.xml.bind.annotation.XmlAttribute;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author Christoph Deppisch
 */
public abstract class AbstractObjectConverter<T, S> implements ObjectConverter<T, S> {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(AbstractObjectConverter.class);

    @Autowired
    private ProjectService projectService;

    protected static final String TRUE = "true";
    protected static final String FALSE = "false";

    /**
     * Adds new endpoint property.
     * @param fieldName
     * @param definition
     */
    protected <V> Property<V> property(String fieldName, S definition) {
        return property(fieldName, definition, false);
    }

    /**
     * Adds new endpoint property.
     * @param fieldName
     * @param definition
     * @param required
     */
    protected <V> Property<V> property(String fieldName, S definition, boolean required) {
        return property(fieldName, definition, null, required);
    }

    /**
     * Adds new endpoint property.
     * @param fieldName
     * @param definition
     * @param defaultValue
     */
    protected <V> Property<V> property(String fieldName, S definition, V defaultValue) {
        return property(fieldName, definition, defaultValue, false);
    }

    /**
     * Adds new endpoint property.
     * @param fieldName
     * @param definition
     * @param defaultValue
     * @param required
     */
    protected <V> Property<V> property(String fieldName, S definition, V defaultValue, boolean required) {
        return property(fieldName, StringUtils.capitalize(fieldName), definition, defaultValue, required);
    }

    /**
     * Adds new endpoint property.
     * @param fieldName
     * @param displayName
     * @param definition
     */
    protected <V> Property<V> property(String fieldName, String displayName, S definition) {
        return property(fieldName, displayName, definition, false);
    }

    /**
     * Adds new endpoint property.
     * @param fieldName
     * @param displayName
     * @param definition
     * @param required
     */
    protected <V> Property<V> property(String fieldName, String displayName, S definition, boolean required) {
        return property(fieldName, displayName, definition, null, required);
    }

    /**
     * Adds new endpoint property.
     * @param fieldName
     * @param displayName
     * @param definition
     * @param defaultValue
     * @param required
     */
    protected <V> Property<V> property(String fieldName, String displayName, S definition, V defaultValue, boolean required) {
        Field field = ReflectionUtils.findField(definition.getClass(), fieldName);

        if (field != null) {
            Method getter = ReflectionUtils.findMethod(definition.getClass(), getterMethodName(field, fieldName));

            V value = defaultValue;
            if (getter != null) {
                Object getterResult = ReflectionUtils.invokeMethod(getter, definition);
                if (getterResult != null) {
                    value = (V) getterResult;
                }
            }

            if (value != null) {
                if (field.isAnnotationPresent(XmlAttribute.class)) {
                    return new Property<>(getFieldName(field.getAnnotation(XmlAttribute.class).name()), fieldName, displayName, resolvePropertyExpression(value), required);
                } else {
                    return new Property<>(getFieldName(fieldName), fieldName, displayName, resolvePropertyExpression(value), required);
                }
            } else {
                return new Property<>(getFieldName(fieldName), fieldName, displayName, null, required);
            }
        } else {
            log.warn(String.format("Unknown field '%s' on source type '%s'", fieldName, definition.getClass()));
            return null;
        }
    }

    /**
     * Resolves property value with project properties in case value is a property expression.
     * @param value
     * @return
     */
    protected <V> V resolvePropertyExpression(V value) {
        if (value instanceof String && VariableUtils.isVariableName(String.valueOf(value))) {
            return (V) projectService.getProjectProperties().getProperty(VariableUtils.cutOffVariablesPrefix(String.valueOf(value)));
        } else {
            return value;
        }
    }

    /**
     * Evaluates if field is required.
     * @param field
     * @return
     */
    protected boolean isRequiredField(Field field) {
        return Stream.of(getRequiredFields()).parallel().anyMatch(name -> field.getName().equals(name));
    }

    /**
     * Optional list of required field names.
     * @return
     */
    protected String[] getRequiredFields() {
        return new String[] {};
    }

    /**
     * Evaluates the target field name.
     * @param field
     * @return
     */
    protected Object getDefaultValue(Field field) {
        return getDefaultValueMappings().entrySet().stream()
                .filter(entry -> entry.getKey().equals(field.getName()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseGet(() -> {
                    if (field.getType().equals(Boolean.class)) {
                        return FALSE;
                    } else {
                        return null;
                    }
                });
    }

    /**
     * Optional mappings for field names to default values.
     * @return
     */
    protected Map<String, Object> getDefaultValueMappings() {
        return new HashMap<>();
    }

    /**
     * Evaluates the target field name.
     * @param fieldName
     * @return
     */
    protected String getFieldName(String fieldName) {
        return getFieldNameMappings().entrySet().stream()
                .filter(entry -> entry.getKey().equals(fieldName))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(fieldName);
    }

    /**
     * Optional mappings for field names.
     * @return
     */
    protected Map<String, String> getFieldNameMappings() {
        return new HashMap<>();
    }

    /**
     * Evaluates the display name.
     * @param fieldName
     * @return
     */
    protected String getDisplayName(String fieldName) {
        return getDisplayNameMappings().entrySet().stream()
                .filter(entry -> entry.getKey().equals(fieldName))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(StringUtils.capitalize(fieldName));
    }

    /**
     * Optional mappings for field names.
     * @return
     */
    protected Map<String, String> getDisplayNameMappings() {
        return new HashMap<>();
    }

    /**
     * Evaluates option type for given field or null if not applicable.
     * @param field
     * @return
     */
    protected Class<?> getOptionType(Field field) {
        return getOptionTypeMappings().entrySet().stream()
                .filter(entry -> entry.getKey().equals(field.getName()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    /**
     * Optional type mappings for fields. Keys are field names.
     * @return
     */
    protected Map<String, Class<?>> getOptionTypeMappings() {
        return new HashMap<>();
    }

    /**
     * Evaluates field options if any.
     * @param field
     * @return
     */
    protected String[] getFieldOptions(Field field) {
        return getFieldOptionMappings().entrySet().stream()
                .filter(entry -> entry.getKey().equals(field.getName()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElseGet(() -> {
                    if (field.getType().equals(Boolean.class)) {
                        return new String[] {TRUE, FALSE};
                    } else {
                        return new String[]{};
                    }
                });
    }

    /**
     * Optional field options. Keys are field names.
     * @return
     */
    protected Map<String, String[]> getFieldOptionMappings() {
        return new HashMap<>();
    }

    /**
     * Construct default Java bean property getter for field name.
     * @param field
     * @param fieldName
     * @return
     */
    protected String getterMethodName(Field field, String fieldName) {
        if (field.getType().equals(Boolean.class)) {
            return "is" + StringUtils.capitalize(fieldName);
        }

        return "get" + StringUtils.capitalize(fieldName);
    }
}
