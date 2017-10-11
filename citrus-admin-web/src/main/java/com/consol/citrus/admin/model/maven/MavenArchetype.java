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

package com.consol.citrus.admin.model.maven;

/**
 * @author Christoph Deppisch
 */
public class MavenArchetype {

    private String archetypeGroupId;
    private String archetypeArtifactId;
    private String archetypeVersion;

    private String artifactId;
    private String groupId;
    private String version;
    private String packageName;

    /**
     * Gets the archetypeGroupId.
     *
     * @return
     */
    public String getArchetypeGroupId() {
        return archetypeGroupId;
    }

    /**
     * Sets the archetypeGroupId.
     *
     * @param archetypeGroupId
     */
    public void setArchetypeGroupId(String archetypeGroupId) {
        this.archetypeGroupId = archetypeGroupId;
    }

    /**
     * Gets the archetypeArtifactId.
     *
     * @return
     */
    public String getArchetypeArtifactId() {
        return archetypeArtifactId;
    }

    /**
     * Sets the archetypeArtifactId.
     *
     * @param archetypeArtifactId
     */
    public void setArchetypeArtifactId(String archetypeArtifactId) {
        this.archetypeArtifactId = archetypeArtifactId;
    }

    /**
     * Gets the archetypeVersion.
     *
     * @return
     */
    public String getArchetypeVersion() {
        return archetypeVersion;
    }

    /**
     * Sets the archetypeVersion.
     *
     * @param archetypeVersion
     */
    public void setArchetypeVersion(String archetypeVersion) {
        this.archetypeVersion = archetypeVersion;
    }

    /**
     * Gets the artifactId.
     *
     * @return
     */
    public String getArtifactId() {
        return artifactId;
    }

    /**
     * Sets the artifactId.
     *
     * @param artifactId
     */
    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    /**
     * Gets the groupId.
     *
     * @return
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * Sets the groupId.
     *
     * @param groupId
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * Gets the version.
     *
     * @return
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the version.
     *
     * @param version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Gets the packageName.
     *
     * @return
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * Sets the packageName.
     *
     * @param packageName
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
