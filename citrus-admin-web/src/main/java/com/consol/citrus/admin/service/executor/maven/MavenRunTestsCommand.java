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

import com.consol.citrus.admin.model.Test;
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

    /** Test to execute */
    private Test test;

    public MavenRunTestsCommand(File projectDirectory, Test test, MavenBuildConfiguration buildConfiguration) {
        this(projectDirectory, buildConfiguration);
        this.test = test;
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
        BuildProperty utTestNameProperty = new BuildProperty("test", test.getClassName() + "#" + test.getMethodName());
        BuildProperty itTestNameProperty = new BuildProperty("it.test", test.getClassName() + "#" + test.getMethodName());

        List<BuildProperty> properties = super.getSystemProperties();
        if (StringUtils.hasText(test.getName()) && getBuildConfiguration().getTestPlugin().equals("maven-failsafe")) {
            properties.add(itTestNameProperty);
        } else if (StringUtils.hasText(test.getName()) && getBuildConfiguration().getTestPlugin().equals("maven-surefire")) {
            properties.add(utTestNameProperty);
        } else if (StringUtils.hasText(test.getName())) {
            properties.add(utTestNameProperty);
            properties.add(itTestNameProperty);
        }

        return properties;
    }

    @Override
    protected String[] getActiveProfiles() {
        return StringUtils.commaDelimitedListToStringArray(getBuildConfiguration().getProfiles());
    }
}
