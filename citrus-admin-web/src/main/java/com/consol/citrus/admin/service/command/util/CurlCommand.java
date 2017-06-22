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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.net.URL;

/**
 * @author Christoph Deppisch
 */
public class CurlCommand extends AbstractTerminalCommand {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(CurlCommand.class);

    private static final String CURL = "curl ";

    private URL url;
    private String outputFile = "archive.zip";

    /**
     * Constructor initializes with working directory.
     *
     * @param workingDirectory the working directory where the command is to be executed from
     * @param shellListeners
     */
    public CurlCommand(File workingDirectory, ProcessListener... shellListeners) {
        super(workingDirectory, shellListeners);
    }

    /**
     * Build the execute command.
     * @return
     */
    public String buildCommand() {
        StringBuilder builder = new StringBuilder();
        builder.append(CURL);

        if (StringUtils.hasText(outputFile)) {
            builder.append(String.format("--output %s ", outputFile));
        }

        builder.append(url);

        log.debug("Using curl command: " + builder.toString());

        return builder.toString();
    }

    /**
     * Get remote resource from url.
     * @param url
     * @return
     */
    public CurlCommand get(URL url) {
        this.url = url;
        return this;
    }

    /**
     * Get remote resource and save to outputFile.
     * @param url
     * @param outputFile
     * @return
     */
    public CurlCommand get(URL url, String outputFile) {
        get(url);
        this.outputFile = outputFile;
        return this;
    }
}
