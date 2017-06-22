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

import java.io.File;

/**
 * @author Christoph Deppisch
 */
public class MoveCommand extends AbstractTerminalCommand {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(MoveCommand.class);

    private static final String MV = "mv -f %s %s";

    /**
     * Constructor initializes with working directory.
     *
     * @param workingDirectory the working directory where the command is to be executed from
     * @param shellListeners
     */
    public MoveCommand(File workingDirectory, ProcessListener... shellListeners) {
        super(workingDirectory, shellListeners);
    }

    @Override
    public String buildCommand() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format(MV, getArguments().toArray()));

        log.debug("Using move command: " + builder.toString());

        return builder.toString();
    }

    /**
     * Add source file argument.
     * @param source
     * @return
     */
    public MoveCommand source(String source) {
        getArguments().add(source);
        return this;
    }

    /**
     * Add target file argument.
     * @param target
     * @return
     */
    public MoveCommand target(String target) {
        getArguments().add(target);
        return this;
    }
}
