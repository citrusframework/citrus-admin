/*
 *  Copyright 2006-2016 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.consol.citrus.admin.model.build;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Christoph Deppisch
 */
public class AbstractBuildConfiguration implements BuildConfiguration {

    protected final String type;
    protected List<BuildProperty> properties = new ArrayList<>();

    /**
     * Constructor
     * @param type
     */
    public AbstractBuildConfiguration(String type) {
        this.type = type;
    }

    @Override
    public List<BuildProperty> getProperties() {
        return properties;
    }

    /**
     * Sets the build properties.
     * @param properties
     */
    public void setProperties(List<BuildProperty> properties) {
        this.properties = properties;
    }

    @Override
    public String getType() {
        return type;
    }
}
