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

package com.consol.citrus.admin.model.build.maven;

import com.consol.citrus.admin.Application;
import com.consol.citrus.admin.model.build.AbstractBuildConfiguration;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.util.StringUtils;

/**
 * @author Christoph Deppisch
 */
public class MavenBuildConfiguration extends AbstractBuildConfiguration {

    private final String mavenHome;
    private String testPlugin = "maven-failsafe";
    private String command;
    private String profiles = "";
    private boolean useClean = false;

    public MavenBuildConfiguration() {
        super("maven");

        if (System.getProperty(Application.MVN_HOME_DIRECTORY) != null) {
            mavenHome = System.getProperty(Application.MVN_HOME_DIRECTORY);
        } else if (StringUtils.hasText(System.getenv("MAVEN_HOME"))) {
            mavenHome = System.getenv("MAVEN_HOME");
        } else if (StringUtils.hasText(System.getenv("M2_HOME"))) {
            mavenHome = System.getenv("M2_HOME");
        } else {
            mavenHome = "";
        }
    }

    /**
     * Gets the value of the mavenHome property.
     *
     * @return the mavenHome
     */
    @JsonIgnore
    public String getMavenHome() {
        return mavenHome;
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
     * Sets the useClean property.
     *
     * @param useClean
     */
    public void setUseClean(boolean useClean) {
        this.useClean = useClean;
    }

    /**
     * Gets the value of the useClean property.
     *
     * @return the useClean
     */
    public boolean isUseClean() {
        return useClean;
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
