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

package com.consol.citrus.admin.process.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Christoph Deppisch
 */
public class LoggingProcessListener implements ProcessListener {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(LoggingProcessListener.class);

    @Override
    public void onProcessStart(String processId) {
        log.info(String.format("Starting new process: %s", processId));
    }

    @Override
    public void onProcessSuccess(String processId) {
        log.info(String.format("Success process: %s", processId));
    }

    @Override
    public void onProcessFail(String processId, int exitCode) {
        log.info(String.format("Failed process: %s (%s)", processId, exitCode));
    }

    @Override
    public void onProcessFail(String processId, Throwable e) {
        log.error(String.format("Failed process: %s", processId), e);
    }

    @Override
    public void onProcessOutput(String processId, String output) {
        if (log.isTraceEnabled()) {
            System.out.println(output);
        }
    }

    @Override
    public void onProcessActivity(String processId, String output) {
    }
}
