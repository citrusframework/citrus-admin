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

import com.consol.citrus.Citrus;
import com.consol.citrus.admin.Application;
import com.consol.citrus.admin.exception.ApplicationRuntimeException;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.File;
import java.io.IOException;

/**
 * @author Christoph Deppisch
 */
public class Project {

    private final String projectHome;
    private String name;
    private String description;
    private String version;
    private String srcDirectory = System.getProperty(Application.TEST_SRC_DIRECTORY, Citrus.DEFAULT_TEST_SRC_DIRECTORY);
    private String basePackage = System.getProperty(Application.BASE_PACKAGE, "com.consol.citrus");
    private Long testCount = 0L;

    /** Citrus project information as Json file */
    private static final String PROJECT_INFO_FILENAME = "citrus-project.info";

    /**
     * Default constructor using project home directory.
     * @param projectHome
     */
    public Project(String projectHome) {
        try {
            this.projectHome = new File(projectHome).getCanonicalPath();
        } catch (IOException e) {
            throw new ApplicationRuntimeException("Unable to access project home directory", e);
        }
    }

    /**
     * Gets the ANT build file.
     * @return
     */
    @JsonIgnore
    public File getAntBuildFile() {
        if (isAntProject()) {
            return new File(getAntBuildFilePath());
        } else {
            throw new ApplicationRuntimeException("Failed to get ANT build file - project is not a ANT project");
        }
    }

    /**
     * Checks ANT project nature by finding the basic build.xml ANT file in project home directory.
     * @return
     */
    @JsonIgnore
    public boolean isAntProject() {
        return new File(getAntBuildFilePath()).exists();
    }

    /**
     * Gets the ANT build xml file path.
     * @return
     */
    @JsonIgnore
    private String getAntBuildFilePath() {
        return projectHome + System.getProperty("file.separator") + "build.xml";
    }

    /**
     * Gets the Maven POM file.
     * @return
     */
    @JsonIgnore
    public File getMavenPomFile() {
        if (isMavenProject()) {
            return new File(getMavenPomFilePath());
        } else {
            throw new ApplicationRuntimeException("Failed to get Maven POM file - project is not a Maven project");
        }
    }

    /**
     * Checks Maven project nature by finding the basic pom.xml Maven file in project home directory.
     * @return
     */
    @JsonIgnore
    public boolean isMavenProject() {
        return new File(getMavenPomFilePath()).exists();
    }

    /**
     * Gets the Maven POM xml file path.
     * @return
     */
    @JsonIgnore
    private String getMavenPomFilePath() {
        return projectHome + System.getProperty("file.separator") + "pom.xml";
    }

    /**
     * Gets file pointer to project info file in project home directory.
     * @return
     */
    @JsonIgnore
    public File getProjectInfoFile() {
        return new File(projectHome + System.getProperty("file.separator") + PROJECT_INFO_FILENAME);
    }

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
     * Gets the value of the version property.
     *
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the version property.
     *
     * @param version
     */
    public void setVersion(String version) {
        this.version = version;
    }

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
     * Gets the value of the projectHome property.
     *
     * @return the projectHome
     */
    public String getProjectHome() {
        return projectHome;
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
     * Gets the value of the testCount property.
     *
     * @return the testCount
     */
    public Long getTestCount() {
        return testCount;
    }

    /**
     * Sets the testCount property.
     *
     * @param testCount
     */
    public void setTestCount(Long testCount) {
        this.testCount = testCount;
    }
}
