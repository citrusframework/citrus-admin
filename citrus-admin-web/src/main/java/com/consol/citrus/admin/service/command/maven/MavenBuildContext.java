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

package com.consol.citrus.admin.service.command.maven;

import com.consol.citrus.admin.model.build.AbstractBuildContext;

/**
 * @author Christoph Deppisch
 */
public class MavenBuildContext extends AbstractBuildContext {

    private String command;
    private boolean clean = false;
    private boolean compile = true;

    private String testPlugin = "maven-failsafe";

    private String profiles;

    public MavenBuildContext() {
        super("maven");
    }

    /**
     * Sets the active profiles.
     * @param profiles
     */
    public void setProfiles(String profiles) {
        this.profiles = profiles;
    }

    /**
     * Gets the active profiles.
     * @return
     */
    public String getProfiles() {
        return profiles;
    }

    /**
     * Sets the testPlugin property.
     *
     * @param testPlugin
     */
    public void setTestPlugin(String testPlugin) {
        this.testPlugin = testPlugin;
    }

    /**
     * Gets the value of the testPlugin property.
     *
     * @return the testPlugin
     */
    public String getTestPlugin() {
        return testPlugin;
    }

    /**
     * Sets the clean property.
     *
     * @param clean
     */
    public void setClean(boolean clean) {
        this.clean = clean;
    }

    /**
     * Gets the value of the clean property.
     *
     * @return the clean
     */
    public boolean isClean() {
        return clean;
    }

    /**
     * Gets the compile.
     *
     * @return
     */
    public boolean isCompile() {
        return compile;
    }

    /**
     * Sets the compile.
     *
     * @param compile
     */
    public void setCompile(boolean compile) {
        this.compile = compile;
    }

    /**
     * Sets the command property.
     *
     * @param command
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * Gets the value of the command property.
     *
     * @return the command
     */
    public String getCommand() {
        return command;
    }
}
