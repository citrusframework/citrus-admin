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

package com.consol.citrus.admin.service.command.svn;

import com.consol.citrus.admin.process.listener.ProcessListener;
import com.consol.citrus.admin.service.command.AbstractTerminalCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;

/**
 * @author Christoph Deppisch
 */
public class SvnCommand extends AbstractTerminalCommand {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(SvnCommand.class);

    private static final String SVN = "svn ";
    private static final String CHECKOUT = "checkout %s";
    private static final String REVERT = "revert %s";

    private static final String VERSION = "--version";
    private static final String USERNAME = " --username %s";
    private static final String PASSWORD = " --password %s";

    private String operation = CHECKOUT;

    /**
     * Constructor initializes with working directory.
     *
     * @param workingDirectory the working directory where the command is to be executed from
     * @param shellListeners
     */
    public SvnCommand(File workingDirectory, ProcessListener... shellListeners) {
        super(workingDirectory, shellListeners);
    }

    /**
     * Build the execute command.
     * @return
     */
    public String buildCommand() {
        StringBuilder builder = new StringBuilder();
        builder.append(SVN);
        builder.append(String.format(operation, getArguments().toArray()));

        log.debug("Using Subversion command: " + builder.toString());

        return builder.toString();
    }

    /**
     * Use svn checkout command.
     * @param repositoryUrl
     * @return
     */
    public SvnCommand checkout(URL repositoryUrl, String branch) {
        getArguments().add(repositoryUrl.toString() + "/" + branch);
        operation = CHECKOUT;
        return this;
    }

    /**
     * Use svn revert command.
     * @param branch
     * @return
     */
    public SvnCommand revert(String branch) {
        getArguments().add(branch);
        operation = REVERT;
        return this;
    }

    /**
     * Use username arg.
     * @param username
     * @return
     */
    public SvnCommand username(String username) {
        getArguments().add(username);
        operation += USERNAME;
        return this;
    }

    /**
     * Use username arg.
     * @param password
     * @return
     */
    public SvnCommand password(String password) {
        getArguments().add(password);
        operation += PASSWORD;
        return this;
    }

    /**
     * Use svn checkout command to custom directory.
     * @param repositoryUrl
     * @param branch
     * @param directory
     * @return
     */
    public SvnCommand checkout(URL repositoryUrl, String branch, String directory) {
        getArguments().add(repositoryUrl.toString() + "/" + branch);
        getArguments().add(directory);
        operation = CHECKOUT + " %s";
        return this;
    }

    /**
     * Version command.
     * @return
     */
    public SvnCommand version() {
        operation = VERSION;
        return this;
    }
}
