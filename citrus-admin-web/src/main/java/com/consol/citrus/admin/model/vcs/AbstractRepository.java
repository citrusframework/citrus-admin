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

package com.consol.citrus.admin.model.vcs;

/**
 * @author Christoph Deppisch
 */
public abstract class AbstractRepository implements Repository {

    private final String vcs;

    private String url;
    private String branch;
    private String module = "/";

    private String username;
    private String password;

    /**
     * Default constructor using version control type.
     * @param vcs
     */
    public AbstractRepository(String vcs) {
        this.vcs = vcs;
    }

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

    /**
     * Gets the vcs.
     *
     * @return
     */
    public String getVcs() {
        return vcs;
    }

    /**
     * Gets the username.
     *
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     *
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the password.
     *
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password.
     *
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
}
