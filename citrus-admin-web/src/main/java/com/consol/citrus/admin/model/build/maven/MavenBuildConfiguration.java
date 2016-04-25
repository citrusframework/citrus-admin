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

package com.consol.citrus.admin.model.build.maven;

import com.consol.citrus.admin.model.build.AbstractBuildConfiguration;

import java.util.List;

/**
 * @author Christoph Deppisch
 */
public class MavenBuildConfiguration extends AbstractBuildConfiguration {

    private String testPlugin = "maven-failsafe-plugin";
    private List<String> profiles;

    public MavenBuildConfiguration() {
        super("maven");
    }

    /**
     * Sets the active profiles.
     * @param profiles
     */
    public void setProfiles(List<String> profiles) {
        this.profiles = profiles;
    }

    /**
     * Gets the active profiles.
     * @return
     */
    public List<String> getProfiles() {
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
     * Should we use failsafe Maven plugin for test execution.
     * @return
     */
    public boolean useFailsafe() {
        return testPlugin.equalsIgnoreCase("maven-failsafe-plugin");
    }
}
