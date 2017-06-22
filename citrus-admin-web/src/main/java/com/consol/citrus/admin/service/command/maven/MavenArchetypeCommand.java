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

import com.consol.citrus.admin.model.build.maven.MavenBuildConfiguration;
import com.consol.citrus.admin.model.maven.Archetype;
import com.consol.citrus.admin.process.listener.ProcessListener;
import org.springframework.util.StringUtils;

import java.io.File;

/**
 * @author Christoph Deppisch
 */
public class MavenArchetypeCommand extends MavenCommand {

    private static final String GENERATE = "archetype:generate ";

    /**
     * Constructor for executing a command.
     *
     * @param workingDirectory
     * @param buildConfiguration
     * @param shellListeners
     */
    public MavenArchetypeCommand(File workingDirectory, MavenBuildConfiguration buildConfiguration, ProcessListener... shellListeners) {
        super(workingDirectory, buildConfiguration, shellListeners);
    }

    /**
     * Use test command for single test group.
     * @param archetype
     * @return
     */
    public MavenArchetypeCommand generate(Archetype archetype) {
        StringBuilder commandBuilder = new StringBuilder();

        commandBuilder.append(GENERATE);

        commandBuilder.append("-B ");
        commandBuilder.append(String.format("-DarchetypeGroupId=%s ", archetype.getArchetypeGroupId()));
        commandBuilder.append(String.format("-DarchetypeArtifactId=%s ", archetype.getArchetypeArtifactId()));
        commandBuilder.append(String.format("-DarchetypeVersion=%s ", archetype.getArchetypeVersion()));
        commandBuilder.append(String.format("-DgroupId=%s ", archetype.getGroupId()));
        commandBuilder.append(String.format("-DartifactId=%s ", archetype.getArtifactId()));
        commandBuilder.append(String.format("-Dversion=%s ", archetype.getVersion()));
        commandBuilder.append(String.format("-Dpackage=%s ", StringUtils.hasText(archetype.getPackageName()) ? archetype.getPackageName() : archetype.getGroupId()));

        lifecycleCommand += commandBuilder.toString();
        return this;
    }

}
