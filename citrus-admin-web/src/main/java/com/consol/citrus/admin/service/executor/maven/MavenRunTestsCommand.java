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

package com.consol.citrus.admin.service.executor.maven;

import com.consol.citrus.admin.model.Test;
import com.consol.citrus.admin.model.TestGroup;
import com.consol.citrus.admin.model.build.BuildProperty;
import com.consol.citrus.admin.model.build.maven.MavenBuildConfiguration;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.List;

/**
 * ProcessBuilder for launching a single citrus test.
 *
 * @author Martin.Maher@consol.de
 */
public class MavenRunTestsCommand extends MavenCommand {

    /** Test or group to execute */
    private TestGroup group;
    private Test test;

    public MavenRunTestsCommand(File projectDirectory, Test test, MavenBuildConfiguration buildConfiguration) {
        this(projectDirectory, buildConfiguration);
        this.test = test;
    }

    public MavenRunTestsCommand(File projectDirectory, TestGroup group, MavenBuildConfiguration buildConfiguration) {
        this(projectDirectory, buildConfiguration);
        this.group = group;
    }

    public MavenRunTestsCommand(File projectDirectory, MavenBuildConfiguration buildConfiguration) {
        super(projectDirectory, buildConfiguration);
    }

    @Override
    protected String getLifeCycleCommand() {
        String commandLine = getBuildConfiguration().isUseClean() ? CLEAN : "";
        if (StringUtils.hasText(getBuildConfiguration().getCommand())) {
            return commandLine + getBuildConfiguration().getCommand() + " ";
        } else if (getBuildConfiguration().getTestPlugin().equals("maven-failsafe")) {
            return commandLine + COMPILE + INTEGRATION_TEST;
        } else if (getBuildConfiguration().getTestPlugin().equals("maven-surefire")) {
            return commandLine + COMPILE + TEST;
        }

        return commandLine;
    }

    @Override
    protected List<BuildProperty> getSystemProperties() {
        BuildProperty utTestNameProperty = null;
        BuildProperty itTestNameProperty = null;

        if (test != null && StringUtils.hasText(test.getName())) {
            utTestNameProperty = new BuildProperty("test", test.getClassName() + "#" + test.getMethodName());
            itTestNameProperty = new BuildProperty("it.test", test.getClassName() + "#" + test.getMethodName());
        } else if (group != null && StringUtils.hasText(group.getName())) {
            utTestNameProperty = new BuildProperty("test", group.getName() + ".*");
            itTestNameProperty = new BuildProperty("it.test", group.getName() + ".*");
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
