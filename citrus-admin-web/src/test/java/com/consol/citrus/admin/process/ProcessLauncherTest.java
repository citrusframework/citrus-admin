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
import com.consol.citrus.admin.process.local.LocalProcessLauncher;
import com.consol.citrus.admin.service.command.AbstractTerminalCommand;
import com.consol.citrus.admin.service.command.TerminalCommand;
import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.util.*;

/**
 * Tests the process launcher functionality.
 *
 * This test is more an integration test than a unit test, since it tests the threading behaviour, process monitor
 * interaction as well.
 *
 * @author Martin.Maher@consol.de
 */
public class ProcessLauncherTest {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(ProcessLauncherTest.class);

    private final static int STARTED = 0;
    private final static int SUCCESS = 1;
    private final static int FAILED_EXIT_CODE = 2;
    private final static int FAILED_EXCEPTION = 3;

    private List<Boolean> callbacks = Arrays.asList(false, false, false, false);

    private ProcessMonitor processMonitor;

    @BeforeMethod
    public void setUp() throws Exception {
        processMonitor = new ProcessMonitorImpl();
    }

    @Test
    public void testSyncSuccess_noMaxExecutionTime() throws Exception {
        TerminalCommand command = getSleepCommand(1);
        launchAndWait(command, "sync-success-no-timeout");
        assertSuccess(true);
    }

    @Test
    public void testSyncSuccess_withMaxExecutionTime() throws Exception {
        TerminalCommand command = getSleepCommand(1);
        launchAndWait(command, "sync-success-with-timeout", 5);
        assertSuccess(true);
    }

    @Test
    public void testSyncFailed_exceededMaxExecutionTime() throws Exception {
        TerminalCommand command = getSleepCommand(5);
        launchAndWait(command, "sync-failed-timeout", 2);
        assertFailed(true);
    }

    @Test
    public void testAsyncSuccess_noMaxExecutionTime() throws Exception {
        TerminalCommand command = getSleepCommand(3);
        ProcessLauncher pl = launchAndContinue(command, "async-success-no-timeout");

        // check started
        Thread.sleep(1000);
        assertStarted();

        // check completed successfully
        Thread.sleep(3000);
        assertSuccess(false);

        // check calling stop on stopped process is OK
        pl.stop();
        pl.stop();
    }

    @Test
    public void testAsyncSuccess_withMaxExecutionTime() throws Exception {
        TerminalCommand command = getSleepCommand(2);
        launchAndContinue(command, "async-success-with-timeout", 3);

        // check started
        Thread.sleep(1000);
        assertStarted();

        // check completed successfully
        Thread.sleep(2000);
        assertSuccess(false);
    }

    @Test
    public void testAsyncFailed_exceededMaxExecutionTime() throws Exception {
        TerminalCommand command = getSleepCommand(5);
        launchAndContinue(command, "async-failed-timeout", 2);

        // check started
        Thread.sleep(1000);
        assertStarted();

        // check failed
        Thread.sleep(5000);
        assertFailed(false);
    }

    @Test
    public void testAsyncFailed_stopped() throws Exception {
        TerminalCommand command = getSleepCommand(5);
        ProcessLauncher pl = launchAndContinue(command, "async-failed-stopped", 2);

        // check started
        Thread.sleep(1000);
        assertStarted();

        // stop
        pl.stop();

        // check failed
        Thread.sleep(5000);
        assertFailed(true);
    }

    private TerminalCommand getSleepCommand(int sleepInSeconds) throws InterruptedException {
        String command;
        if (SystemUtils.IS_OS_UNIX) {
            command = String.format("ping -c %s 127.0.0.1", sleepInSeconds);
        } else {
            command = String.format("ping -n %s 127.0.0.1", sleepInSeconds);
        }
        return new AbstractTerminalCommand(new File(System.getProperty("user.dir"))) { public String buildCommand() { return command; } };
    }

    private ProcessLauncher launchAndWait(TerminalCommand command, String processName) throws InterruptedException {
        return launchAndWait(command, processName, 0);
    }

    private ProcessLauncher launchAndWait(TerminalCommand command, String processName, int maxExecutionTime) throws InterruptedException {
        ProcessListener pli = getProcessListener(callbacks);
        LocalProcessLauncher pla = new LocalProcessLauncher(processMonitor, processName);
        pla.launchAndWait(command, maxExecutionTime, pli);
        return pla;
    }

    private ProcessLauncher launchAndContinue(TerminalCommand command, String processName) throws InterruptedException {
        return launchAndContinue(command, processName, 0);
    }

    private ProcessLauncher launchAndContinue(TerminalCommand command, String processName, int maxExecutionTime) throws InterruptedException {
        ProcessListener pli = getProcessListener(callbacks);
        LocalProcessLauncher pla = new LocalProcessLauncher(processMonitor, processName);
        pla.launchAndContinue(command, maxExecutionTime, pli);
        return pla;
    }

    private ProcessListener getProcessListener(final List<Boolean> callbacks) {
        // reset callbacks
        for (int i = 0; i < callbacks.size(); i++) {
            callbacks.set(i, false);
        }

        return new ProcessListener() {
            public void onProcessStart(String processId) {
                System.out.println("Starting:" + processId + ", " + new Date());
                callbacks.set(STARTED, Boolean.TRUE);
            }

            public void onProcessSuccess(String processId) {
                System.out.println("Success:" + processId);
                callbacks.set(SUCCESS, Boolean.TRUE);
            }

            public void onProcessFail(String processId, int exitCode) {
                System.err.println("Failed:" + processId + ", errorCode:" + exitCode);
                callbacks.set(FAILED_EXIT_CODE, Boolean.TRUE);
            }

            public void onProcessFail(String processId, Throwable e) {
                System.err.println("Failed:" + processId + ", ex:" + e.getLocalizedMessage());
                log.error("Failed", e);
                callbacks.set(FAILED_EXCEPTION, Boolean.TRUE);
            }

            public void onProcessOutput(String processId, String output) {
                //do nothing as activity was already printed
            }

            public void onProcessActivity(String processId, String output) {
                System.out.println(processId + ":" + output);
            }
        };
    }

    private void assertStarted() {
        Assert.assertTrue(callbacks.get(STARTED));
        Assert.assertEquals(processMonitor.getProcessIds().size(), 1);
    }

    private void assertSuccess(boolean processMonitorStopped) {
        Assert.assertTrue(callbacks.get(SUCCESS));
        if (processMonitorStopped) {
            Assert.assertTrue(processMonitor.getProcessIds().isEmpty());
        } else {
            Assert.assertFalse(processMonitor.getProcessIds().isEmpty());
        }
    }

    private void assertFailed(boolean processMonitorStopped) {
        Assert.assertTrue(callbacks.get(FAILED_EXIT_CODE) || callbacks.get(FAILED_EXCEPTION));
        if (processMonitorStopped) {
            Assert.assertTrue(processMonitor.getProcessIds().isEmpty());
        } else {
            Assert.assertFalse(processMonitor.getProcessIds().isEmpty());
        }
    }

}
