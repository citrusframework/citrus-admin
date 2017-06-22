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

import com.consol.citrus.admin.process.listener.ProcessListener;
import com.consol.citrus.admin.service.command.TerminalCommand;

/**
 * @author Christoph Deppisch
 */
public interface TerminalService {

    /**
     * Executes a terminal command.
     * @param command
     * @param processListeners
     * @return the process id
     */
    String execute(TerminalCommand command, ProcessListener... processListeners);

    /**
     * Executes a terminal command.
     * @param command
     * @param processListeners
     * @return success or failed result
     */
    boolean executeAndWait(TerminalCommand command, ProcessListener... processListeners);

    /**
     * Cancel active process for given process id.
     * @param pid
     * @return success or failed result
     */
    boolean cancelProcess(String pid);
}
