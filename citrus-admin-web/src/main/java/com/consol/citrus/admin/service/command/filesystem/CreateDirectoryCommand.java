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

package com.consol.citrus.admin.service.command.filesystem;

import com.consol.citrus.admin.process.listener.ProcessListener;
import com.consol.citrus.admin.service.command.AbstractTerminalCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.File;

/**
 * @author Christoph Deppisch
 */
public class CreateDirectoryCommand extends AbstractTerminalCommand {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(CreateDirectoryCommand.class);

    private static final String MKDIR = "mkdir %s";

    private String directory;

    /**
     * Constructor initializes with working directory.
     *
     * @param workingDirectory the working directory where the command is to be executed from
     * @param shellListeners
     */
    public CreateDirectoryCommand(File workingDirectory, ProcessListener... shellListeners) {
        super(workingDirectory, shellListeners);
    }

    @Override
    public String buildCommand() {
        StringBuilder builder = new StringBuilder();

        if (StringUtils.hasText(directory)) {
            builder.append(String.format(MKDIR, directory));
        } else {
            directory = getWorkingDirectory().getName();
            setWorkingDirectory(getWorkingDirectory().getParentFile());
        }

        log.debug("Using create directory command: " + builder.toString());

        return builder.toString();
    }

    @Override
    protected void validateWorkingDirectory(File workingDirectory) {
        if (!workingDirectory.getParentFile().exists()) {
            throw new IllegalStateException(String.format("Invalid directory to create '%s'", workingDirectory.getAbsolutePath()));
        }
    }

    /**
     * Set directory to create;
     * @param directory
     * @return
     */
    public CreateDirectoryCommand directory(String directory) {
        this.directory = directory;
        return this;
    }
}
