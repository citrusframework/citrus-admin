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

package com.consol.citrus.admin.process.local;

import com.consol.citrus.admin.exception.ApplicationRuntimeException;
import com.consol.citrus.admin.process.*;
import com.consol.citrus.admin.process.listener.ProcessListener;
import com.consol.citrus.admin.service.command.TerminalCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.*;

/**
 * @author Martin.Maher@consol.de
 */
public class LocalProcessLauncher extends AbstractProcessLauncher {

    /** Logger */
    private static final Logger LOG = LoggerFactory.getLogger(LocalProcessLauncher.class);

    private ExecutorService executorService = Executors.newFixedThreadPool(5);

    private Process process;
    private InputStreamPumper pumper;

    /**
     * Default constructor.
     * @param processMonitor
     * @param processId
     */
    public LocalProcessLauncher(ProcessMonitor processMonitor, String processId) {
        super(processMonitor, processId);
    }

    @Override
    public void attach(TerminalCommand command, ProcessListener ... processListeners) {
        if (process != null && process.isAlive()) {
            String cmd = command.buildCommand();
            addProcessListeners(processListeners);
            
            if (hasSubProcess() && cmd.charAt(0) == '\u0003') {
                notifyOutput("^C");
                processMonitor.stop(this.getProcessId());
                return;
            }

            LOG.info("Attaching command: '" + cmd + "'");

            notifyOutput(cmd);
            notifyStart(true);

            try {
                process.getOutputStream().write(cmd.getBytes());
                process.getOutputStream().flush();
            } catch (IOException e) {
                notifyFail(e);
            }
        } else {
            throw new ApplicationRuntimeException(String.format("Unable to attach command '%s' to finished process", command.buildCommand()));
        }
    }

    @Override
    public void launchAndWait(TerminalCommand command, int timeout, ProcessListener... processListeners) {
        addProcessListeners(processListeners);
        addShellListeners(command.getShellListeners());

        notifyStart(false);
        ProcessBuilder processBuilder = command.getProcessBuilder();
        try {
            processBuilder.redirectErrorStream(true);
            LOG.info("Starting process: " + processBuilder.command());

            startProcessMonitoring();
            process = processBuilder.start();

            pumper = new InputStreamPumper(process.getInputStream()) {
                @Override
                public void onActivity(String line) {
                    notifyActivity(line);
                }

                @Override
                public void onOutput(String data) {
                    notifyOutput(data);
                }
            };
            executorService.submit(pumper);

            if (timeout > 0 && process.waitFor(timeout, TimeUnit.SECONDS)) {
                int result = process.exitValue();
                LOG.info("Process completed: " + result);

                if (result == 0) {
                    notifySuccess();
                } else {
                    notifyFail(process.exitValue());
                }
            } else if (timeout <= 0) {
                int result = process.waitFor();
                LOG.info("Process completed: " + result);

                if (result == 0) {
                    notifySuccess();
                } else {
                    notifyFail(process.exitValue());
                }
            } else {
                LOG.info("Process timeout!");
                throw new TimeoutException(String.format("Process did not return after %s seconds", timeout));
            }
        } catch (Exception e) {
            notifyFail(e);
        } finally {
            stop();
        }
    }

    @Override
    public void launchAndContinue(TerminalCommand command, int timeout, ProcessListener ... processListeners) {
        addProcessListeners(processListeners);
        addShellListeners(command.getShellListeners());
        notifyStart(true);

        ProcessBuilder processBuilder = command.getProcessBuilder();
        try {
            processBuilder.redirectErrorStream(true);
            LOG.info("Starting process: " + processBuilder.command());

            startProcessMonitoring();
            process = processBuilder.start();

            pumper = new InputStreamPumper(process.getInputStream()) {
                @Override
                public void onActivity(String line) {
                    notifyActivity(line);
                }

                @Override
                public void onOutput(String data) {
                    notifyOutput(data);
                }
            };
            executorService.submit(pumper);

            executorService.submit(() -> {
                try {
                    if (timeout > 0 && process.waitFor(timeout, TimeUnit.SECONDS)) {
                        int result = process.exitValue();
                        LOG.info("Process completed: " + result);

                        if (result == 0) {
                            notifySuccess();
                        } else {
                            notifyFail(process.exitValue());
                        }
                    } else if (timeout <= 0) {
                        int result = process.waitFor();
                        LOG.info("Process completed: " + result);

                        if (result == 0) {
                            notifySuccess();
                        } else {
                            notifyFail(process.exitValue());
                        }
                    } else {
                        LOG.info("Process timeout!");
                        notifyFail(new TimeoutException(String.format("Process did not return after %s seconds", timeout)));
                    }
                } catch (InterruptedException e) {
                    notifyFail(e);
                }
            });
        } catch (Exception e) {
            notifyFail(e);
        }
    }

    @Override
    public void stop() {
        super.stop();

        if (pumper != null) {
            try {
                pumper.close();
            } catch (Exception e) {
                // ignore
                LOG.warn("Error destroying input stream pumper", e);
            }
        }

        if (process != null) {
            try {
                process.destroy();
            } catch (Exception e) {
                // ignore
                LOG.warn("Error destroying process", e);
            }
        }
    }
}
