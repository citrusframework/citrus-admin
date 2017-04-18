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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Christoph Deppisch
 */
public class EndpointModel {

    private String id;
    private String type;
    private String modelType;
    private List<Property> properties = new ArrayList<Property>();

    /**
     * Default constructor basically used when constructing from json object.
     */
    public EndpointModel() {
        super();
    }

    /**
     * Constructor using endpoint type field and identifier.
     * @param type
     * @param id
     */
    public EndpointModel(String type, String id, String modelType) {
        this.id = id;
        this.modelType = modelType;
        this.type = type;
    }

    /**
     * Adds a new configuration property as key value pair.
     * @param property
     * @return
     */
    public EndpointModel add(Property property) {
        properties.add(property);
        return this;
    }

    /**
     * Gets the identifier usually the Spring bean name.
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
     * Gets the endpoint type such as http, jms, camel, etc.
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type property.
     *
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets the model type that is logically linked to this endpoint data.
     * @return
     */
    public String getModelType() {
        return modelType;
    }

    /**
     * Sets the modelType property.
     *
     * @param modelType
     */
    public void setModelType(String modelType) {
        this.modelType = modelType;
    }

    /**
     * Gets the key value endpoint properties.
     * @return
     */
    public List<Property> getProperties() {
        return properties;
    }

    /**
     * Sets the endpoint properties as key value properties.
     * @param properties
     */
    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }
}
