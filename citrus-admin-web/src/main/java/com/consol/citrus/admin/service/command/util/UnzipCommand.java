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

package com.consol.citrus.admin.service.command.util;

import com.consol.citrus.admin.process.listener.ProcessListener;
import com.consol.citrus.admin.service.command.AbstractTerminalCommand;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.File;

/**
 * @author Christoph Deppisch
 */
public class UnzipCommand extends AbstractTerminalCommand {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(UnzipCommand.class);

    private static final String UNZIP = "unzip -qo "; //quiet and overwrite

    private String archive = "archive.zip";
    private String toDir = "archive";

    /**
     * Constructor initializes with working directory.
     *
     * @param workingDirectory the working directory where the command is to be executed from
     * @param shellListeners
     */
    public UnzipCommand(File workingDirectory, ProcessListener... shellListeners) {
        super(workingDirectory, shellListeners);
    }

    /**
     * Build the execute command.
     * @return
     */
    public String buildCommand() {
        StringBuilder builder = new StringBuilder();
        builder.append(UNZIP);

        builder.append(archive);

        if (StringUtils.hasText(toDir)) {
            builder.append(String.format(" -d %s", toDir));
        }

        log.debug("Using unzip command: " + builder.toString());

        return builder.toString();
    }

    /**
     * Unzip archive.
     * @param archive
     * @return
     */
    public UnzipCommand archive(String archive) {
        this.archive = archive;
        this.toDir = FilenameUtils.getBaseName(archive);
        return this;
    }

    /**
     * Sets target dir to extract files to.
     * @param toDir
     * @return
     */
    public UnzipCommand toDir(String toDir) {
        this.toDir = toDir;
        return this;
    }
}
