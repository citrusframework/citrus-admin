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

import com.consol.citrus.model.testcase.core.MetaInfoType;

/**
 * @author Christoph Deppisch
 */
public class Test {

    private String name;

    private TestType type;

    private String packageName;
    private String groups;
    private String file;
    private Long lastModified;

    private MetaInfoType metaInfo;

    private String description;

    /**
     * Gets the value of the name property.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name property.
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the value of the type property.
     *
     * @return the type
     */
    public TestType getType() {
        return type;
    }

    /**
     * Sets the type property.
     *
     * @param type
     */
    public void setType(TestType type) {
        this.type = type;
    }

    /**
     * Gets the value of the packageName property.
     *
     * @return the packageName
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * Sets the packageName property.
     *
     * @param packageName
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /**
     * Gets the value of the groups property.
     *
     * @return the groups
     */
    public String getGroups() {
        return groups;
    }

    /**
     * Sets the groups property.
     *
     * @param groups
     */
    public void setGroups(String groups) {
        this.groups = groups;
    }

    /**
     * Gets the value of the file property.
     *
     * @return the file
     */
    public String getFile() {
        return file;
    }

    /**
     * Sets the file property.
     *
     * @param file
     */
    public void setFile(String file) {
        this.file = file;
    }

    /**
     * Gets the value of the lastModified property.
     *
     * @return the lastModified
     */
    public Long getLastModified() {
        return lastModified;
    }

    /**
     * Sets the lastModified property.
     *
     * @param lastModified
     */
    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * Gets the value of the metaInfo property.
     *
     * @return the metaInfo
     */
    public MetaInfoType getMetaInfo() {
        return metaInfo;
    }

    /**
     * Sets the metaInfo property.
     *
     * @param metaInfo
     */
    public void setMetaInfo(MetaInfoType metaInfo) {
        this.metaInfo = metaInfo;
    }

    /**
     * Gets the value of the description property.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description property.
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
