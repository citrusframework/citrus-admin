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

import com.consol.citrus.admin.process.listener.ProcessListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author Christoph Deppisch
 */
public abstract class AbstractProcessLauncher implements ProcessLauncher {

    /** Process id and monitor */
    private String processId;
    protected ProcessMonitor processMonitor;

    /** Listeners get informed on process or test events */
    private List<ProcessListener> shellListeners = new ArrayList<>();
    private List<ProcessListener> processListeners = new ArrayList<>();

    /**
     * Marks running sub process.
     */
    private boolean subProcess = false;

    /**
     * Default constructor using fields.
     * @param processMonitor
     * @param processId
     */
    public AbstractProcessLauncher(ProcessMonitor processMonitor, String processId) {
        this.processId = processId;
        this.processMonitor = processMonitor;
    }

    @Override
    public String getProcessId() {
        return processId;
    }

    @Override
    public void stop() {
        stopProcessMonitoring();
    }

    /**
     * Start to monitor the process.
     */
    protected void startProcessMonitoring() {
        this.processMonitor.add(this);
    }

    /**
     * Stop to monitor the process.
     */
    protected void stopProcessMonitoring() {
        this.processMonitor.remove(this);
    }

    protected synchronized void notifyStart(boolean subprocess) {
        if (subprocess) {
            subProcess = true;
        }

        for (ProcessListener processListener : shellListeners) {
            processListener.onProcessStart(processId);
        }

        for (ProcessListener processListener : processListeners) {
            processListener.onProcessStart(processId);
        }
    }

    protected synchronized void notifySuccess() {
        subProcess = false;

        for (ProcessListener processListener : shellListeners) {
            processListener.onProcessSuccess(processId);
        }

        for (ProcessListener processListener : processListeners) {
            processListener.onProcessSuccess(processId);
        }
        processListeners.clear();
    }

    protected synchronized void notifyFail(int exitCode) {
        subProcess = false;

        for (ProcessListener processListener : shellListeners) {
            processListener.onProcessFail(processId, exitCode);
        }

        for (ProcessListener processListener : processListeners) {
            processListener.onProcessFail(processId, exitCode);
        }
        processListeners.clear();
    }

    protected synchronized void notifyFail(Exception e) {
        subProcess = false;

        for (ProcessListener processListener : shellListeners) {
            processListener.onProcessFail(processId, e);
        }

        for (ProcessListener processListener : processListeners) {
            processListener.onProcessFail(processId, e);
        }
        processListeners.clear();
    }

    protected synchronized void notifyOutput(String output) {
        for (ProcessListener processListener : shellListeners) {
            processListener.onProcessOutput(processId, output);
        }

        for (ProcessListener processListener : processListeners) {
            processListener.onProcessOutput(processId, output);
        }
    }

    protected synchronized void notifyActivity(String output) {
        for (ProcessListener processListener : shellListeners) {
            processListener.onProcessActivity(processId, output);
        }

        for (ProcessListener processListener : processListeners) {
            processListener.onProcessActivity(processId, output);
        }
    }

    /**
     * Adds shell listeners.
     * @param processListeners
     */
    protected void addShellListeners(ProcessListener... processListeners) {
        Stream.of(processListeners).forEach(listener -> this.shellListeners.add(listener));
    }

    /**
     * Adds process listeners.
     * @param processListeners
     */
    protected void addProcessListeners(ProcessListener... processListeners) {
        Stream.of(processListeners).forEach(listener -> this.processListeners.add(listener));
    }

    /**
     * Gets the subProcess.
     *
     * @return
     */
    public synchronized boolean hasSubProcess() {
        return subProcess;
    }
}
