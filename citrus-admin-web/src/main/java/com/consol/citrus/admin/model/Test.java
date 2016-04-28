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

/**
 * @author Christoph Deppisch
 */
public class Test {

    private String name;

    private TestType type;
    private String packageName;

    private String className;
    private String methodName;

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
     * Sets the className property.
     *
     * @param className
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Gets the value of the className property.
     *
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * Sets the methodName property.
     *
     * @param methodName
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * Gets the value of the methodName property.
     *
     * @return the methodName
     */
    public String getMethodName() {
        return methodName;
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
}
