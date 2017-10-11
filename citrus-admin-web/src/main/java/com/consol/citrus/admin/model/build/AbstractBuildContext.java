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

package com.consol.citrus.admin.model.build;

import com.consol.citrus.admin.model.Property;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Christoph Deppisch
 */
public class AbstractBuildContext implements BuildContext {

    protected final String type;
    protected List<Property> properties = new ArrayList<>();

    /**
     * Constructor
     * @param type
     */
    public AbstractBuildContext(String type) {
        this.type = type;
    }

    @Override
    public List<Property> getProperties() {
        return properties;
    }

    /**
     * Sets the build properties.
     * @param properties
     */
    public void setProperties(List<Property> properties) {
        this.properties = properties;
    }

    @Override
    public String getType() {
        return type;
    }
}
