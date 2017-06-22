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
import com.consol.citrus.admin.model.build.BuildConfiguration;
import com.consol.citrus.admin.model.build.maven.MavenBuildConfiguration;
import com.consol.citrus.admin.process.ProcessMonitor;
import com.consol.citrus.admin.process.listener.ProcessListener;
import com.consol.citrus.admin.service.command.maven.MavenRunTestsCommand;
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
    public TestResult execute(Project project) {
        TestResult result = new TestResult();

        File projectHome = new File(project.getProjectHome());
        MavenBuildConfiguration buildConfiguration = getBuildConfiguration(project);
        MavenRunTestsCommand command = new MavenRunTestsCommand(projectHome, buildConfiguration, processListeners.toArray(new ProcessListener[processListeners.size()]));

        if (buildConfiguration.isClean()) {
            command.clean();
        }

        if (buildConfiguration.isCompile()) {
            command.compile();
        }

        if (StringUtils.hasText(buildConfiguration.getCommand())) {
            command.custom(buildConfiguration.getCommand());
        } else if (buildConfiguration.getTestPlugin().equals("maven-failsafe")) {
            command.integrationTest();
        } else {
            command.test();
        }

        try {
            String processId = terminalService.execute(command);

            result.setProcessId(processId);
            result.setSuccess(true);
        } catch (Exception e) {
            log.warn("Failed to execute all test cases", e);
            setFailureStack(result, e);
        }

        return result;
    }

    /**
     * Runs a test case and returns result outcome (success or failure).
     * @param project
     * @param test
     * @return
     */
    public TestResult execute(Project project, Test test) {
        TestResult result = new TestResult();
        result.setTest(test);

        File projectHome = new File(project.getProjectHome());
        MavenBuildConfiguration buildConfiguration = getBuildConfiguration(project);
        MavenRunTestsCommand command = new MavenRunTestsCommand(projectHome, buildConfiguration, processListeners.toArray(new ProcessListener[processListeners.size()]));

        if (buildConfiguration.isClean()) {
            command.clean();
        }

        if (buildConfiguration.isCompile()) {
            command.compile();
        }

        if (StringUtils.hasText(buildConfiguration.getCommand())) {
            command.custom(buildConfiguration.getCommand());
        } else if (buildConfiguration.getTestPlugin().equals("maven-failsafe")) {
            command.integrationTest(test);
        } else {
            command.test(test);
        }

        try {
            String processId = terminalService.execute(command);

            result.setProcessId(processId);
            result.setSuccess(true);
        } catch (Exception e) {
            log.warn("Failed to execute Citrus test case '" + test.getName() + "'", e);
            setFailureStack(result, e);
        }

        return result;
    }

    /**
     * Runs a test group and returns result outcome (success or failure).
     * @param project
     * @param group
     * @return
     */
    public TestResult execute(Project project, TestGroup group) {
        TestResult result = new TestResult();

        File projectHome = new File(project.getProjectHome());
        MavenBuildConfiguration buildConfiguration = getBuildConfiguration(project);
        MavenRunTestsCommand command = new MavenRunTestsCommand(projectHome, buildConfiguration, processListeners.toArray(new ProcessListener[processListeners.size()]));

        if (buildConfiguration.isClean()) {
            command.clean();
        }

        if (buildConfiguration.isCompile()) {
            command.compile();
        }

        if (StringUtils.hasText(buildConfiguration.getCommand())) {
            command.custom(buildConfiguration.getCommand());
        } else if (buildConfiguration.getTestPlugin().equals("maven-failsafe")) {
            command.integrationTest(group);
        } else {
            command.test(group);
        }

        try {
            String processId = terminalService.execute(command);

            result.setProcessId(processId);
            result.setSuccess(true);
        } catch (Exception e) {
            log.warn("Failed to execute test group '" + group.getName() + "'", e);
            setFailureStack(result, e);
        }

        return result;
    }

    /**
     * Stops process with given id.
     * @param processId
     */
    public void stop(String processId) {
        processMonitor.stop(processId);
    }

    /**
     * Adds failure stack information to test result.
     * @param result
     * @param e
     */
    private void setFailureStack(TestResult result, Exception e) {
        result.setSuccess(false);

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        e.printStackTrace(new PrintStream(os));
        result.setErrorMessage(e.getMessage());
        result.setErrorCause(e.getClass().getName());
        result.setStackTrace("Caused by: " + os.toString());
    }

    /**
     * Check that maven build configuration is set and get it from project settings.
     * @param project
     * @return
     */
    private MavenBuildConfiguration getBuildConfiguration(Project project) {
        BuildConfiguration buildConfiguration = project.getSettings().getBuild();
        if (!MavenBuildConfiguration.class.isInstance(buildConfiguration)) {
            throw new ApplicationRuntimeException("Unable to execute Maven command with non-maven build configuration: " + buildConfiguration.getClass());
        }

        return (MavenBuildConfiguration) buildConfiguration;
    }
}
