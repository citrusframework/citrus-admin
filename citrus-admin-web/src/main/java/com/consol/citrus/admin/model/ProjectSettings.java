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

package com.consol.citrus.admin.model;

import com.consol.citrus.Citrus;
import com.consol.citrus.admin.Application;
import com.consol.citrus.admin.model.build.BuildConfiguration;
import com.consol.citrus.admin.model.build.maven.MavenBuildConfiguration;

/**
 * @author Christoph Deppisch
 */
public class ProjectSettings {

    private String srcDirectory = System.getProperty(Application.TEST_SRC_DIRECTORY, Citrus.DEFAULT_TEST_SRC_DIRECTORY);
    private String basePackage = System.getProperty(Application.BASE_PACKAGE, "com.consol.citrus");
    private String citrusVersion = Citrus.getVersion();

    private BuildConfiguration build = new MavenBuildConfiguration();

    /**
     * Gets the value of the srcDirectory property.
     *
     * @return the srcDirectory
     */
    public String getSrcDirectory() {
        return srcDirectory;
    }

    /**
     * Sets the srcDirectory property.
     *
     * @param srcDirectory
     */
    public void setSrcDirectory(String srcDirectory) {
        this.srcDirectory = srcDirectory;
    }


    /**
     * Gets the value of the basePackage property.
     *
     * @return the basePackage
     */
    public String getBasePackage() {
        return basePackage;
    }

    /**
     * Sets the basePackage property.
     *
     * @param basePackage
     */
    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    /**
     * Sets the build configuration.
     * @param build
     */
    public void setBuild(BuildConfiguration build) {
        this.build = build;
    }

    /**
     * Gets the build configuration.
     * @return
     */
    public BuildConfiguration getBuild() {
        return build;
    }

    /**
     * Sets the citrus version.
     * @param citrusVersion
     */
    public void setCitrusVersion(String citrusVersion) {
        this.citrusVersion = citrusVersion;
    }

    /**
     * Gets the citrus version.
     * @return
     */
    public String getCitrusVersion() {
        return citrusVersion;
    }
}
