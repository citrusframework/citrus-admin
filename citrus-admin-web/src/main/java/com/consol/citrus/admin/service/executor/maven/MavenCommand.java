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

import com.consol.citrus.admin.service.executor.ExecuteCommand;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Christoph Deppisch
 */
public class MavenCommand extends ExecuteCommand {

    protected static final String MVN = "mvn ";
    protected static final String COMPILE = "compile ";
    protected static final String INTEGRATION_TEST = "integration-test ";
    protected static final String CLEAN = "clean ";
    protected static final String INSTALL = "install ";

    /**
     * Constructor for executing a command.
     *
     * @param command the command to be executed
     */
    public MavenCommand(String command, File workingDirectory) {
        super(command, workingDirectory);
    }

    protected String buildCommand(String command) {
        StringBuilder builder = new StringBuilder();

        builder.append(MVN);
        builder.append(command);

        for (Map.Entry<Object, Object> propertyEntry: getSystemProperties().entrySet()) {
            builder.append(String.format("-D%s=%s ", propertyEntry.getKey(), propertyEntry.getValue()));
        }

        for (String profile: getActiveProfiles()) {
            builder.append(String.format("-P%s ", profile));
        }

        return builder.toString();
    }

    protected String[] getActiveProfiles() {
        return new String[0];
    }

    protected Map<Object, Object> getSystemProperties() {
        return new HashMap<>();
    }
}
