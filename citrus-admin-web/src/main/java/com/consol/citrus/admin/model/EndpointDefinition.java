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

package com.consol.citrus.admin.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Christoph Deppisch
 */
public class EndpointDefinition {

    private final String id;
    private final String type;
    private final Class<?> modelType;
    private List<Property> properties = new ArrayList<Property>();

    /**
     * Constructor using endpoint type field and identifier.
     * @param type
     * @param id
     */
    public EndpointDefinition(String type, String id, Class<?> modelType) {
        this.id = id;
        this.modelType = modelType;
        this.type = type;
    }

    /**
     * Adds a new configuration property as key value pair.
     * @param property
     * @return
     */
    public EndpointDefinition add(Property property) {
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
     * Gets the endpoint type such as http, jms, camel, etc.
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the model type that is logically linked to this endpoint data.
     * @return
     */
    public Class<?> getModelType() {
        return modelType;
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
