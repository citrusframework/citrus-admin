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

package com.consol.citrus.admin.service.executor.maven;

import com.consol.citrus.admin.model.build.BuildProperty;
import com.consol.citrus.admin.model.build.maven.MavenBuildConfiguration;
import com.consol.citrus.admin.service.executor.AbstractExecuteCommand;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christoph Deppisch
 */
public class MavenCommand extends AbstractExecuteCommand {

    protected static final String MVN = "mvn ";
    protected static final String COMPILE = "compile ";
    protected static final String TEST = "surefire:test ";
    protected static final String INTEGRATION_TEST = "failsafe:integration-test ";
    protected static final String CLEAN = "clean ";
    protected static final String PACKAGE = "package ";
    protected static final String INSTALL = "install ";

    /** Maven build configuration */
    private final MavenBuildConfiguration buildConfiguration;

    /**
     * Constructor for executing a command.
     * @param workingDirectory
     * @param buildConfiguration
     */
    public MavenCommand(File workingDirectory, MavenBuildConfiguration buildConfiguration) {
        super(workingDirectory);
        this.buildConfiguration = buildConfiguration;
    }

    /**
     * Build the execute command.
     * @return
     */
    protected String buildCommand() {
        StringBuilder builder = new StringBuilder();

        builder.append(MVN);
        builder.append(getLifeCycleCommand());

        for (BuildProperty propertyEntry: getSystemProperties()) {
            builder.append(String.format("-D%s=%s ", propertyEntry.getName(), propertyEntry.getValue()));
        }

        for (String profile: getActiveProfiles()) {
            builder.append(String.format("-P%s ", profile));
        }

        return builder.toString();
    }

    /**
     * Gets the build system properties.
     * @return
     */
    protected List<BuildProperty> getSystemProperties() {
        List<BuildProperty> systemProperties = new ArrayList<>();

        systemProperties.addAll(buildConfiguration.getProperties());
        return systemProperties;
    }

    /**
     * Gets the value of the buildConfiguration property.
     *
     * @return the buildConfiguration
     */
    public MavenBuildConfiguration getBuildConfiguration() {
        return buildConfiguration;
    }

    protected String getLifeCycleCommand() {
        return "";
    }

    protected String[] getActiveProfiles() {
        return new String[0];
    }
}
