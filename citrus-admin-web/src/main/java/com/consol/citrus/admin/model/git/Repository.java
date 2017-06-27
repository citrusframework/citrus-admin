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

package com.consol.citrus.admin.model.git;

/**
 * @author Christoph Deppisch
 */
public class Repository {

    private String url;
    private String branch = "master";
    private String module = "/";

    /**
     * Gets the url.
     *
     * @return
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the url.
     *
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Gets the branch.
     *
     * @return
     */
    public String getBranch() {
        return branch;
    }

    /**
     * Sets the branch.
     *
     * @param branch
     */
    public void setBranch(String branch) {
        this.branch = branch;
    }

    /**
     * Gets the module.
     *
     * @return
     */
    public String getModule() {
        return module;
    }

    /**
     * Sets the module.
     *
     * @param module
     */
    public void setModule(String module) {
        this.module = module;
    }
}
