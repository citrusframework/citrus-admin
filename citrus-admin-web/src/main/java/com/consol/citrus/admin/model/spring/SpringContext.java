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

package com.consol.citrus.admin.model.spring;

/**
 * @author Christoph Deppisch
 * @since 2.7
 */
public class SpringContext {

    private String name;
    private String fileName;
    private String source;

    /**
     * Gets the name.
     *
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
     * Gets the fileName.
     *
     * @return
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the fileName.
     *
     * @param fileName
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Gets the source.
     *
     * @return
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the source.
     *
     * @param source
     */
    public void setSource(String source) {
        this.source = source;
    }
}
