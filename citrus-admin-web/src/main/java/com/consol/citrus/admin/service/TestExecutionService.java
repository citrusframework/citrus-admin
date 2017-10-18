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

package com.consol.citrus.admin.service;

import com.consol.citrus.admin.exception.ApplicationRuntimeException;
import com.consol.citrus.admin.model.*;
import com.consol.citrus.admin.model.build.BuildContext;
import com.consol.citrus.admin.service.command.maven.MavenBuildContext;
import com.consol.citrus.admin.process.ProcessMonitor;
import com.consol.citrus.admin.process.listener.ProcessListener;
import com.consol.citrus.admin.service.command.maven.MavenCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.util.List;

/**
 * @author Christoph Deppisch
 */
@Service
public class TestExecutionService {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(TestExecutionService.class);

    @Autowired
    private ProcessMonitor processMonitor;

    @Autowired
    private TerminalService terminalService;

    @Autowired
    private List<ProcessListener> processListeners;

    /**
     * Runs all test cases and returns result outcome (success or failure).
     * @param project
     * @return
     */
    public String execute(Project project) {
        File projectHome = new File(project.getProjectHome());
        MavenBuildContext buildContext = getBuildContext(project);
        MavenCommand command = new MavenCommand(projectHome, buildContext, processListeners.toArray(new ProcessListener[processListeners.size()]));

        if (buildContext.isClean()) {
            command.clean();
        }

        if (buildContext.isCompile()) {
            command.compile();
        }

        if (StringUtils.hasText(buildContext.getCommand())) {
            command.custom(buildContext.getCommand());
        } else if (buildContext.getTestPlugin().equals("maven-failsafe")) {
            command.integrationTest();
        } else {
            command.test();
        }

        try {
            return terminalService.execute(command);
        } catch (Exception e) {
            throw new ApplicationRuntimeException("Failed to execute all test cases", e);
        }

    }

    /**
     * Runs a test case and returns result outcome (success or failure).
     * @param project
     * @param test
     * @return
     */
    public String execute(Project project, Test test) {
        File projectHome = new File(project.getProjectHome());
        MavenBuildContext buildContext = getBuildContext(project);
        MavenCommand command = new MavenCommand(projectHome, buildContext, processListeners.toArray(new ProcessListener[processListeners.size()]));

        if (buildContext.isClean()) {
            command.clean();
        }

        if (buildContext.isCompile()) {
            command.compile();
        }

        if (StringUtils.hasText(buildContext.getCommand())) {
            command.custom(buildContext.getCommand());
        } else if (buildContext.getTestPlugin().equals("maven-failsafe")) {
            command.integrationTest(test);
        } else {
            command.test(test);
        }

        try {
            return terminalService.execute(command);
        } catch (Exception e) {
            throw new ApplicationRuntimeException("Failed to execute Citrus test case '" + test.getName() + "'", e);
        }
    }

    /**
     * Runs a test group and returns result outcome (success or failure).
     * @param project
     * @param group
     * @return
     */
    public String execute(Project project, TestGroup group) {
        File projectHome = new File(project.getProjectHome());
        MavenBuildContext buildContext = getBuildContext(project);
        MavenCommand command = new MavenCommand(projectHome, buildContext, processListeners.toArray(new ProcessListener[processListeners.size()]));

        if (buildContext.isClean()) {
            command.clean();
        }

        if (buildContext.isCompile()) {
            command.compile();
        }

        if (StringUtils.hasText(buildContext.getCommand())) {
            command.custom(buildContext.getCommand());
        } else if (buildContext.getTestPlugin().equals("maven-failsafe")) {
            command.integrationTest(group);
        } else {
            command.test(group);
        }

        try {
            return terminalService.execute(command);
        } catch (Exception e) {
            throw new ApplicationRuntimeException("Failed to execute test group '" + group.getName() + "'", e);
        }
    }

    /**
     * Stops process with given id.
     * @param processId
     */
    public void stop(String processId) {
        processMonitor.stop(processId);
    }

    /**
     * Check that maven build configuration is set and get it from project settings.
     * @param project
     * @return
     */
    private MavenBuildContext getBuildContext(Project project) {
        BuildContext buildContext = project.getSettings().getBuild();
        if (!MavenBuildContext.class.isInstance(buildContext)) {
            throw new ApplicationRuntimeException("Unable to execute Maven command with non-maven build configuration: " + buildContext.getClass());
        }

        return (MavenBuildContext) buildContext;
    }
}
