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

import com.consol.citrus.admin.configuration.ConfigurationProvider;
import com.consol.citrus.admin.model.*;
import com.consol.citrus.admin.process.listener.ProcessListener;
import com.consol.citrus.admin.service.command.AbstractTerminalCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.*;

/**
 * @author Christoph Deppisch
 */
public class MavenCommand extends AbstractTerminalCommand {

    private static final String MVN = "mvn ";
    protected static final String CLEAN = "clean ";
    protected static final String COMPILE = "compile ";
    protected static final String TEST = "test ";
    protected static final String PACKAGE = "package ";
    protected static final String INTEGRATION_TEST = "integration-test ";
    protected static final String INSTALL = "install ";
    protected static final String VERIFY = "verify ";

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(MavenCommand.class);

    /** Maven build configuration */
    private MavenConfiguration configuration = ConfigurationProvider.load(MavenConfiguration.class);
    private final MavenBuildContext buildContext;

    protected String lifecycleCommand = "";

    /** Test or group to execute */
    private TestGroup group;
    private Test test;

    /**
     * Constructor for executing a command.
     * @param workingDirectory
     * @param buildContext
     * @param shellListeners
     */
    public MavenCommand(File workingDirectory, MavenBuildContext buildContext, ProcessListener... shellListeners) {
        super(workingDirectory, shellListeners);
        this.buildContext = buildContext;
    }

    /**
     * Build the execute command.
     * @return
     */
    public String buildCommand() {
        StringBuilder builder = new StringBuilder();
        if (StringUtils.hasText(configuration.getMavenHome())) {
            builder.append(configuration.getMavenHome() + System.getProperty("file.separator") + "bin" + System.getProperty("file.separator") + MVN);
        } else {
            builder.append(MVN);
        }

        builder.append(lifecycleCommand);

        for (Property propertyEntry: getSystemProperties()) {
            builder.append(String.format("-D%s=%s ", propertyEntry.getId(), propertyEntry.getValue()));
        }

        List<String> activeProfiles = Arrays.asList(StringUtils.commaDelimitedListToStringArray(buildContext.getProfiles()));
        if (!CollectionUtils.isEmpty(activeProfiles)) {
            builder.append(String.format("-P%s ", StringUtils.collectionToCommaDelimitedString(activeProfiles)));
        }

        log.debug("Using Maven command: " + builder.toString());

        return builder.toString();
    }

    /**
     * Use custom configuration.
     * @return
     */
    public MavenCommand configuration(MavenConfiguration configuration) {
        this.configuration = configuration;
        return this;
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
     * Use test command.
     * @return
     */
    public MavenCommand test() {
        lifecycleCommand += TEST;
        return this;
    }

    /**
     * Use test command for single test.
     * @param test
     * @return
     */
    public MavenCommand test(Test test) {
        this.test = test;
        return this.test();
    }

    /**
     * Use test command for single test group.
     * @param group
     * @return
     */
    public MavenCommand test(TestGroup group) {
        this.group = group;
        return this.test();
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
     * Use integration test command.
     * @return
     */
    public MavenCommand integrationTest() {
        lifecycleCommand += INTEGRATION_TEST;
        return this;
    }

    /**
     * Use integration test command for single test.
     * @param test
     * @return
     */
    public MavenCommand integrationTest(Test test) {
        this.test = test;
        return this.integrationTest();
    }

    /**
     * Use integration test command for single test group.
     * @param group
     * @return
     */
    public MavenCommand integrationTest(TestGroup group) {
        this.group = group;
        return this.integrationTest();
    }

    /**
     * Gets the build system properties.
     * @return
     */
    protected List<Property> getSystemProperties() {
        List<Property> systemProperties = new ArrayList<>();

        Property utTestNameProperty = null;
        Property itTestNameProperty = null;

        if (test != null && StringUtils.hasText(test.getName())) {
            utTestNameProperty = new Property<>("test", test.getClassName() + "#" + test.getMethodName());
            itTestNameProperty = new Property<>("it.test", test.getClassName() + "#" + test.getMethodName());
        } else if (group != null && StringUtils.hasText(group.getName())) {
            utTestNameProperty = new Property<>("test", group.getName().replaceAll("\\.", "/") + "/*");
            itTestNameProperty = new Property<>("it.test", group.getName().replaceAll("\\.", "/") + "/*");
        }

        systemProperties.addAll(buildContext.getProperties());

        if (itTestNameProperty != null && buildContext.getTestPlugin().equals("maven-failsafe")) {
            systemProperties.add(itTestNameProperty);
        } else if (utTestNameProperty != null && buildContext.getTestPlugin().equals("maven-surefire")) {
            systemProperties.add(utTestNameProperty);
        } else if (utTestNameProperty != null) {
            systemProperties.add(utTestNameProperty);
            systemProperties.add(itTestNameProperty);
        }

        return systemProperties;
    }

    /**
     * Gets the value of the buildConfiguration property.
     *
     * @return the buildConfiguration
     */
    public MavenConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * Gets the buildContext.
     *
     * @return
     */
    public MavenBuildContext getBuildContext() {
        return buildContext;
    }
}
