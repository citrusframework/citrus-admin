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

import com.consol.citrus.admin.model.Test;
import com.consol.citrus.admin.model.TestGroup;
import com.consol.citrus.admin.model.build.BuildProperty;
import com.consol.citrus.admin.model.build.maven.MavenBuildConfiguration;
import com.consol.citrus.admin.process.listener.ProcessListener;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.List;

/**
 * ProcessBuilder for launching a single citrus test.
 *
 * @author Martin.Maher@consol.de
 */
public class MavenRunTestsCommand extends MavenCommand {

    private static final String TEST = "test ";
    private static final String INTEGRATION_TEST = "integration-test ";

    /** Test or group to execute */
    private TestGroup group;
    private Test test;

    /**
     * Constructor for executing a command.
     *
     * @param workingDirectory
     * @param buildConfiguration
     * @param shellListeners
     */
    public MavenRunTestsCommand(File workingDirectory, MavenBuildConfiguration buildConfiguration, ProcessListener... shellListeners) {
        super(workingDirectory, buildConfiguration, shellListeners);
    }

    /**
     * Use test command.
     * @return
     */
    public MavenRunTestsCommand test() {
        lifecycleCommand += TEST;
        return this;
    }

    /**
     * Use test command for single test.
     * @param test
     * @return
     */
    public MavenRunTestsCommand test(Test test) {
        this.test = test;
        return this.test();
    }

    /**
     * Use test command for single test group.
     * @param group
     * @return
     */
    public MavenRunTestsCommand test(TestGroup group) {
        this.group = group;
        return this.test();
    }

    /**
     * Use integration test command.
     * @return
     */
    public MavenRunTestsCommand integrationTest() {
        lifecycleCommand += INTEGRATION_TEST;
        return this;
    }

    /**
     * Use integration test command for single test.
     * @param test
     * @return
     */
    public MavenRunTestsCommand integrationTest(Test test) {
        this.test = test;
        return this.integrationTest();
    }

    /**
     * Use integration test command for single test group.
     * @param group
     * @return
     */
    public MavenRunTestsCommand integrationTest(TestGroup group) {
        this.group = group;
        return this.integrationTest();
    }

    @Override
    protected List<BuildProperty> getSystemProperties() {
        BuildProperty utTestNameProperty = null;
        BuildProperty itTestNameProperty = null;

        if (test != null && StringUtils.hasText(test.getName())) {
            utTestNameProperty = new BuildProperty("test", test.getClassName() + "#" + test.getMethodName());
            itTestNameProperty = new BuildProperty("it.test", test.getClassName() + "#" + test.getMethodName());
        } else if (group != null && StringUtils.hasText(group.getName())) {
            utTestNameProperty = new BuildProperty("test", group.getName().replaceAll("\\.", "/") + "/*");
            itTestNameProperty = new BuildProperty("it.test", group.getName().replaceAll("\\.", "/") + "/*");
        }

        List<BuildProperty> properties = super.getSystemProperties();
        if (itTestNameProperty != null && getBuildConfiguration().getTestPlugin().equals("maven-failsafe")) {
            properties.add(itTestNameProperty);
        } else if (utTestNameProperty != null && getBuildConfiguration().getTestPlugin().equals("maven-surefire")) {
            properties.add(utTestNameProperty);
        } else if (utTestNameProperty != null) {
            properties.add(utTestNameProperty);
            properties.add(itTestNameProperty);
        }

        return properties;
    }
}
