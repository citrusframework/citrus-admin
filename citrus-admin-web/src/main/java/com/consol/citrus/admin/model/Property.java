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

package com.consol.citrus.admin.model;

import java.util.Arrays;
import java.util.List;

/**
 * @author Christoph Deppisch
 */
public class Property<T> {

    private String name;
    private String fieldName;
    private String displayName;
    private T value;

    private Class optionType;
    private List<T> options;

    private boolean required = false;

    /**
     * Default constructor basically used when constructing from json object.
     */
    public Property() {
        super();
    }

    /**
     * Constructor using is and value fields.
     * @param name
     * @param value
     */
    public Property(String name, T value) {
        this(name, name, name, value, false);
    }

    /**
     * Constructor using fields id, field name, displayName, value and required.
     * @param name
     * @param fieldName
     * @param displayName
     * @param value
     * @param required
     */
    public Property(String name, String fieldName, String displayName, T value, boolean required) {
        this.name = name;
        this.fieldName = fieldName;
        this.displayName = displayName;
        this.value = value;
        this.required = required;
    }

    /**
     * Adds option type to search for when collecting options at runtime.
     * @param type
     * @return
     */
    public Property optionType(Class type) {
        this.optionType = type;
        return this;
    }

    /**
     * Adds options as variable arguments.
     * @param options
     * @return
     */
    public Property options(T ... options) {
        this.options = Arrays.asList(options);
        return this;
    }

    /**
     * Adds options.
     * @param options
     * @return
     */
    public Property options(List<T> options) {
        this.options = options;
        return this;
    }

    /**
     * Gets the field name on endpoint object model.
     * @return
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Sets the fieldName property.
     *
     * @param fieldName
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * Gets the display name.
     * @return
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the displayName property.
     *
     * @param displayName
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the name.
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the parameter value.
     * @return
     */
    public T getValue() {
        return value;
    }

    /**
     * Sets the value property.
     *
     * @param value
     */
    public void setValue(T value) {
        this.value = value;
    }

    /**
     * Gets the parameter options when using drop down list.
     * @return
     */
    public List<T> getOptions() {
        return options;
    }

    /**
     * Sets the options property.
     *
     * @param options
     */
    public void setOptions(List<T> options) {
        this.options = options;
    }

    /**
     * Returns the option type used when searching for options.
     * @return
     */
    public Class getOptionType() {
        return optionType;
    }

    /**
     * Sets the optionType property.
     *
     * @param optionType
     */
    public void setOptionType(Class optionType) {
        this.optionType = optionType;
    }

    /**
     * Gets the required nature.
     *
     * @return
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Sets the required nature.
     *
     * @param required
     */
    public void setRequired(boolean required) {
        this.required = required;
    }
}
