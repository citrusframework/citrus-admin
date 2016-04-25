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

import com.consol.citrus.admin.model.build.maven.MavenBuildConfiguration;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.Map;

/**
 * ProcessBuilder for launching a single citrus test.
 *
 * @author Martin.Maher@consol.de
 */
public class MavenRunTestsCommand extends MavenCommand {

    /** Maven build configuration */
    private final MavenBuildConfiguration buildConfiguration;

    /** Test to execute */
    private String testName;

    public MavenRunTestsCommand(File projectDirectory, String testName, MavenBuildConfiguration buildConfiguration) {
        this(projectDirectory, buildConfiguration);
        this.testName = testName;
    }

    public MavenRunTestsCommand(File projectDirectory, MavenBuildConfiguration buildConfiguration) {
        super(projectDirectory);
        this.buildConfiguration = buildConfiguration;
    }

    @Override
    protected String getLifeCycleCommand() {
        if (buildConfiguration.useFailsafe()) {
            return COMPILE + INTEGRATION_TEST;
        } else {
            return COMPILE + TEST;
        }
    }

    @Override
    protected Map<Object, Object> getSystemProperties() {
        Map<Object, Object> properties = super.getSystemProperties();
        if (StringUtils.hasText(testName) && buildConfiguration.useFailsafe()) {
            properties.put("it.test", testName);
        } else if (StringUtils.hasText(testName)) {
            properties.put("test", testName);
        }

        return properties;
    }
}
