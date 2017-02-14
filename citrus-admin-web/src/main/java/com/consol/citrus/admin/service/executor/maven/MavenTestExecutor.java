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

import com.consol.citrus.admin.exception.ApplicationRuntimeException;
import com.consol.citrus.admin.model.Project;
import com.consol.citrus.admin.model.Test;
import com.consol.citrus.admin.model.build.BuildConfiguration;
import com.consol.citrus.admin.model.build.maven.MavenBuildConfiguration;
import com.consol.citrus.admin.process.*;
import com.consol.citrus.admin.process.listener.LoggingProcessListener;
import com.consol.citrus.admin.process.listener.WebSocketProcessListener;
import com.consol.citrus.admin.service.executor.TestExecutor;
import org.apache.commons.cli.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author Christoph Deppisch
 */
@Component
public class MavenTestExecutor implements TestExecutor {

    @Autowired
    private WebSocketProcessListener webSocketProcessListener;

    @Autowired
    private ProcessMonitor processMonitor;

    @Override
    public String execute(Test test, Project project) throws ParseException {
        File projectHome = new File(project.getProjectHome());

        BuildConfiguration buildConfiguration = project.getSettings().getBuild();
        if (!MavenBuildConfiguration.class.isInstance(buildConfiguration)) {
            throw new ApplicationRuntimeException("Unable to execute Maven command extendAndGet non-maven build configuration: " + buildConfiguration.getClass());
        }

        ProcessBuilder processBuilder = new MavenRunTestsCommand(projectHome, test, (MavenBuildConfiguration) buildConfiguration).getProcessBuilder();
        ProcessLauncher processLauncher = new ProcessLauncherImpl(processMonitor, test.getName());

        processLauncher.addProcessListener(webSocketProcessListener);
        processLauncher.addProcessListener(new LoggingProcessListener());
        processLauncher.launchAndContinue(processBuilder, 0);

        return processLauncher.getProcessId();
    }
}
