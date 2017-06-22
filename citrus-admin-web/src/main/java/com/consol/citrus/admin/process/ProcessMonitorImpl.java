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

package com.consol.citrus.admin.process;

import com.consol.citrus.admin.exception.ApplicationRuntimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Martin.Maher@consol.de
 */
@Component
public class ProcessMonitorImpl implements ProcessMonitor {

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(ProcessMonitorImpl.class);

    private Map<String, ProcessLauncher> processMap = new ConcurrentHashMap<>();

    @Override
    public ProcessLauncher get(String processId) {
        if (processMap.containsKey(processId)) {
            return processMap.get(processId);
        } else {
            throw new ApplicationRuntimeException("Failed to find process with id:" + processId);
        }
    }

    @Override
    public void add(ProcessLauncher processLauncher) {
        String id = processLauncher.getProcessId();
        if (processMap.containsKey(id)) {
            String msg = String.format("An active process already exists with the Id '%s'", id);
            LOG.error(msg);
            throw new ProcessLauncherException(msg);
        }
        processMap.put(id, processLauncher);
    }

    @Override
    public void remove(ProcessLauncher processLauncher) {
        processMap.remove(processLauncher.getProcessId());
    }

    @Override
    public Set<String> getProcessIds() {
        return processMap.keySet();
    }

    @Override
    public void stop(String processId) {
        if (processMap.containsKey(processId)) {
            processMap.remove(processId).stop();
        }
    }

    @Override
    public void stopAll() {
        for (Map.Entry<String,ProcessLauncher> entry: processMap.entrySet()) {
            entry.getValue().stop();
        }
    }
}
