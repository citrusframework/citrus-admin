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

import com.consol.citrus.admin.model.build.BuildProperty;
import com.consol.citrus.admin.model.build.maven.MavenBuildConfiguration;
import com.consol.citrus.admin.process.listener.ProcessListener;
import com.consol.citrus.admin.service.command.AbstractTerminalCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christoph Deppisch
 */
public class MavenCommand extends AbstractTerminalCommand {

    private static final String MVN = "mvn ";
    protected static final String CLEAN = "clean ";
    protected static final String COMPILE = "compile ";
    protected static final String PACKAGE = "package ";
    protected static final String INSTALL = "install ";
    protected static final String VERIFY = "verify ";

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(MavenCommand.class);

    /** Maven build configuration */
    private final MavenBuildConfiguration buildConfiguration;

    protected String lifecycleCommand = "";

    /**
     * Constructor for executing a command.
     * @param workingDirectory
     * @param buildConfiguration
     * @param shellListeners
     */
    public MavenCommand(File workingDirectory, MavenBuildConfiguration buildConfiguration, ProcessListener... shellListeners) {
        super(workingDirectory, shellListeners);
        this.buildConfiguration = buildConfiguration;
    }

    /**
     * Build the execute command.
     * @return
     */
    public String buildCommand() {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.hasText(buildConfiguration.getMavenHome())) {
            builder.append(buildConfiguration.getMavenHome() + System.getProperty("file.separator") + "bin" + System.getProperty("file.separator") + MVN);
        } else {
            builder.append(MVN);
        }

        builder.append(lifecycleCommand);

        for (BuildProperty propertyEntry: getSystemProperties()) {
            builder.append(String.format("-D%s=%s ", propertyEntry.getName(), propertyEntry.getValue()));
        }

        if (StringUtils.hasText(getActiveProfiles())) {
            builder.append(String.format("-P%s ", getActiveProfiles()));
        }

        log.debug("Using Maven command: " + builder.toString());

        return builder.toString();
    }

    /**
     * Use clean command.
     * @return
     */
    public MavenCommand clean() {
        lifecycleCommand += CLEAN;
        return this;
    }

    /**
     * Use compile command.
     * @return
     */
    public MavenCommand compile() {
        lifecycleCommand += COMPILE;
        return this;
    }

    /**
     * Use package command.
     * @return
     */
    public MavenCommand packaging() {
        lifecycleCommand += PACKAGE;
        return this;
    }

    /**
     * Use install command.
     * @return
     */
    public MavenCommand install() {
        lifecycleCommand += INSTALL;
        return this;
    }

    /**
     * Use verify command.
     * @return
     */
    public MavenCommand verify() {
        lifecycleCommand += VERIFY;
        return this;
    }

    /**
     * Use custom lifecycle command.
     * @param command
     * @return
     */
    public MavenCommand custom(String command) {
        lifecycleCommand += command + " ";
        return this;
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

    protected String getActiveProfiles() {
        return getBuildConfiguration().getProfiles();
    }
}
