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
public class Property {

    private String id;
    private String fieldName;
    private String displayName;
    private String value;

    private String optionKey;
    private List<String> options;

    private boolean required = false;

    /**
     * Default constructor basically used when constructing from json object.
     */
    public Property() {
        super();
    }

    /**
     * Constructor using form field id, displayName and value.
     * @param id
     * @param fieldName
     * @param displayName
     * @param value
     * @param required
     */
    public Property(String id, String fieldName, String displayName, String value, boolean required) {
        this.id = id;
        this.fieldName = fieldName;
        this.displayName = displayName;
        this.value = value;
        this.required = required;
    }

    /**
     * Adds option key to search for when collecting options at runtime.
     * @param key
     * @return
     */
    public Property optionKey(String key) {
        this.optionKey = key;
        return this;
    }

    /**
     * Adds options as variable arguments.
     * @param options
     * @return
     */
    public Property options(String ... options) {
        this.options = Arrays.asList(options);
        return this;
    }

    /**
     * Adds options.
     * @param options
     * @return
     */
    public Property options(List<String> options) {
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
     * Gets the parameter id.
     * @return
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the id property.
     *
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the parameter value.
     * @return
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value property.
     *
     * @param value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the parameter options when using drop down list.
     * @return
     */
    public List<String> getOptions() {
        return options;
    }

    /**
     * Sets the options property.
     *
     * @param options
     */
    public void setOptions(List<String> options) {
        this.options = options;
    }

    /**
     * Returns the option key used when searching for options.
     * @return
     */
    public String getOptionKey() {
        return optionKey;
    }

    /**
     * Sets the optionKey property.
     *
     * @param optionKey
     */
    public void setOptionKey(String optionKey) {
        this.optionKey = optionKey;
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
