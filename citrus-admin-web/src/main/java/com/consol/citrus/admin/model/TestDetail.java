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

import java.util.*;

/**
 * @author Christoph Deppisch
 */
public class TestDetail extends Test {

    private String groups;
    private String file;
    private Long lastModified;

    private String author;
    private String description;

    private Map<String, Object> variables = new LinkedHashMap<String, Object>();
    private Map<String, Object> parameters = new LinkedHashMap<String, Object>();

    private List<TestAction> actions = new ArrayList<>();

    /**
     * Default constructor.
     */
    public TestDetail() {
        super();
    }

    /**
     * Creates new detail from test.
     * @param test
     */
    public TestDetail(Test test) {
        setName(test.getName());
        setClassName(test.getClassName());
        setMethodName(test.getMethodName());
        setPackageName(test.getPackageName());

        setType(test.getType());
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
     * Gets the value of the author property.
     *
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the author property.
     *
     * @param author
     */
    public void setAuthor(String author) {
        this.author = author;
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

    /**
     * Gets the value of the variables property.
     *
     * @return the variables
     */
    public Map<String, Object> getVariables() {
        return variables;
    }

    /**
     * Sets the variables property.
     *
     * @param variables
     */
    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    /**
     * Gets the value of the parameters property.
     *
     * @return the parameters
     */
    public Map<String, Object> getParameters() {
        return parameters;
    }

    /**
     * Sets the parameters property.
     *
     * @param parameters
     */
    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    /**
     * Gets the value of the actions property.
     *
     * @return the actions
     */
    public List<TestAction> getActions() {
        return actions;
    }

    /**
     * Sets the actions property.
     *
     * @param actions
     */
    public void setActions(List<TestAction> actions) {
        this.actions = actions;
    }
}
