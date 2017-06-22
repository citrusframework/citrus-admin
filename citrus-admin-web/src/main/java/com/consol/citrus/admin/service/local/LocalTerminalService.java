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

package com.consol.citrus.admin.service.local;

import com.consol.citrus.admin.process.ProcessLauncher;
import com.consol.citrus.admin.process.listener.AbstractProcessListener;
import com.consol.citrus.admin.process.listener.ProcessListener;
import com.consol.citrus.admin.process.local.LocalProcessLauncher;
import com.consol.citrus.admin.service.AbstractTerminalService;
import com.consol.citrus.admin.service.command.TerminalCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Christoph Deppisch
 */
@Service
public class LocalTerminalService extends AbstractTerminalService {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(LocalTerminalService.class);

    @Override
    public String execute(TerminalCommand command, ProcessListener... processListeners) {
        String pid = String.valueOf(nextPid());

        if (getProcessMonitor().getProcessIds().contains(pid)) {
            ProcessLauncher processLauncher = getProcessMonitor().get(pid);
            processLauncher.attach(command, processListeners);
        } else {
            ProcessLauncher processLauncher = new LocalProcessLauncher(getProcessMonitor(), pid);
            processLauncher.launchAndContinue(command, 0, processListeners);
        }

        return pid;
    }

    @Override
    public boolean executeAndWait(TerminalCommand command, ProcessListener... processListeners) {
        CompletableFuture<Boolean> result = new CompletableFuture<>();
        ProcessLauncher processLauncher = new LocalProcessLauncher(getProcessMonitor(), String.valueOf(nextPid()));

        List<ProcessListener> modified = Stream.of(processListeners).collect(Collectors.toList());
        modified.add(new AbstractProcessListener() {
            @Override
            public void onProcessSuccess(String processId) {
                result.complete(true);
            }

            @Override
            public void onProcessFail(String processId, Throwable e) {
                result.complete(false);
            }

            @Override
            public void onProcessFail(String processId, int exitCode) {
                result.complete(false);
            }
        });

        processLauncher.launchAndWait(command, 60000, modified.toArray(new ProcessListener[modified.size()]));

        try {
            return result.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to get process result", e);
            return false;
        }
    }
}
