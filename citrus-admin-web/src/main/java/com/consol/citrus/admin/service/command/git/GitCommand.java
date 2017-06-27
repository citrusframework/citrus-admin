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

package com.consol.citrus.admin.service.command.git;

import com.consol.citrus.admin.process.listener.ProcessListener;
import com.consol.citrus.admin.service.command.AbstractTerminalCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;

/**
 * @author Christoph Deppisch
 */
public class GitCommand extends AbstractTerminalCommand {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(GitCommand.class);

    private static final String GIT = "git ";
    private static final String CLONE = "clone %s";
    private static final String CHECKOUT = "checkout %s";
    private static final String VERSION = "--version";

    private String operation = CLONE;

    /**
     * Constructor initializes with working directory.
     *
     * @param workingDirectory the working directory where the command is to be executed from
     * @param shellListeners
     */
    public GitCommand(File workingDirectory, ProcessListener... shellListeners) {
        super(workingDirectory, shellListeners);
    }

    /**
     * Build the execute command.
     * @return
     */
    public String buildCommand() {
        StringBuilder builder = new StringBuilder();
        builder.append(GIT);
        builder.append(String.format(operation, getArguments().toArray()));

        log.debug("Using Git command: " + builder.toString());

        return builder.toString();
    }

    /**
     * Use git clone command.
     * @param repositoryUrl
     * @return
     */
    public GitCommand clone(URL repositoryUrl) {
        getArguments().add(repositoryUrl.toString());
        operation = CLONE;
        return this;
    }

    /**
     * Use git checkout command.
     * @param branch
     * @return
     */
    public GitCommand checkout(String branch) {
        getArguments().add(branch);
        operation = CHECKOUT;
        return this;
    }

    /**
     * Use git clone command to custom directory.
     * @param repositoryUrl
     * @param directory
     * @return
     */
    public GitCommand clone(URL repositoryUrl, String directory) {
        getArguments().add(repositoryUrl.toString());
        getArguments().add(directory);
        operation = CLONE + " %s";
        return this;
    }

    /**
     * Version command.
     * @return
     */
    public GitCommand version() {
        operation = VERSION;
        return this;
    }
}
