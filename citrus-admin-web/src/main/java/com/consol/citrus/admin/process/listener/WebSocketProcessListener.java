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

package com.consol.citrus.admin.process.listener;

import com.consol.citrus.admin.exception.ApplicationRuntimeException;
import com.consol.citrus.admin.model.*;
import com.consol.citrus.admin.service.ProjectService;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author Christoph Deppisch
 */
@Component
public class WebSocketProcessListener extends AbstractProcessListener {

    private static final String TOPIC_LOG_OUTPUT = "/topic/log-output";
    private static final String TOPIC_TEST_EVENTS = "/topic/test-events";
    private static final String TOPIC_MESSAGES = "/topic/messages";
    private static final String TOPIC_TEST_RESULTS = "/topic/results";

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ProjectService projectService;

    /** Last message data collected by multiple lines of process output */
    private JSONObject messageEvent;

    @Override
    public void onProcessActivity(String processId, String output) {
        // first check if we have a pending message data event to handle
        if (messageEvent != null) {
            if (isProcessOutputLine(output)) {
                // message data collecting is obviously finished so push event now
                messagingTemplate.convertAndSend(TOPIC_MESSAGES, messageEvent);
                messageEvent = null; // reset data event storage
            } else {
                // collect another line of message data
                messageEvent.put("msg", messageEvent.get("msg") + System.getProperty("line.separator") + output);
            }
        }

        if (!projectService.getActiveProject().getSettings().isUseConnector() ||
                (projectService.getActiveProject().getSettings().isUseConnector() &&
                !projectService.getActiveProject().getSettings().isConnectorActive())) {
            handleTestEvent(processId, output);
            handleMessageEvent(processId, output);
        }
    }

    private void handleTestEvent(String processId, String output) {
        if (output.contains("STARTING TEST")) {
            messagingTemplate.convertAndSend(TOPIC_TEST_EVENTS, SocketEvent.createEvent(processId, SocketEvent.EventType.TEST_START, output));
        } else if (output.contains("TEST SUCCESS")) {
            messagingTemplate.convertAndSend(TOPIC_TEST_EVENTS, SocketEvent.createEvent(processId, SocketEvent.EventType.TEST_SUCCESS, output));
            messagingTemplate.convertAndSend(TOPIC_TEST_RESULTS, getTestResult(processId, output, TestStatus.PASS));
        } else if (output.contains("TEST FAILED")) {
            messagingTemplate.convertAndSend(TOPIC_TEST_EVENTS, SocketEvent.createEvent(processId, SocketEvent.EventType.TEST_FAILED, output));
            messagingTemplate.convertAndSend(TOPIC_TEST_RESULTS, getTestResult(processId, output, TestStatus.FAIL));
        }  else if (output.contains("SKIPPING TEST")) {
            messagingTemplate.convertAndSend(TOPIC_TEST_EVENTS, SocketEvent.createEvent(processId, SocketEvent.EventType.TEST_SKIP, output));
            messagingTemplate.convertAndSend(TOPIC_TEST_RESULTS, getTestResult(processId, output, TestStatus.SKIP));
        } else if (output.contains("TEST STEP") && output.contains("SUCCESS")) {
            String actionIndex = output.substring(output.indexOf("TEST STEP") + 9, (output.indexOf("SUCCESS") - 1));
            SocketEvent event = SocketEvent.createEvent(processId, SocketEvent.EventType.TEST_ACTION_FINISH,
                    "TEST ACTION " + actionIndex.trim());
            messagingTemplate.convertAndSend(TOPIC_TEST_EVENTS, event);
        }
    }

    private TestResult getTestResult(String processId, String output, TestStatus status) {
        String lineMarker;
        String packageMarker;
        if (status.equals(TestStatus.PASS)) {
            lineMarker =  "TEST SUCCESS";
            packageMarker =  "()";
        } else if (status.equals(TestStatus.FAIL)) {
            lineMarker =  "TEST FAILED";
            packageMarker =  "<>";
        } else if (status.equals(TestStatus.SKIP)) {
            lineMarker =  "SKIPPING TEST";
            packageMarker =  null;
        } else {
            throw new ApplicationRuntimeException("Unsupported test status: " + status);
        }

        String testName;
        String testPackage;
        if (StringUtils.hasText(packageMarker)) {
            testName = output.substring(output.indexOf(lineMarker) + lineMarker.length(), output.indexOf(packageMarker.charAt(0))).trim();
            testPackage = output.substring(output.indexOf(packageMarker.charAt(0)) + 1, output.indexOf(packageMarker.charAt(1))).trim();
        } else {
            testName = output.substring(output.indexOf(lineMarker) + lineMarker.length()).trim();
            testPackage = "";
        }

        TestResult result = new TestResult();
        Test test = new Test();
        test.setType(TestType.JAVA);
        test.setName(testName);
        test.setPackageName(testPackage);
        result.setTest(test);

        result.setStatus(status);
        result.setProcessId(processId);
        return result;
    }

    private void handleMessageEvent(String processId, String output) {
        if (output.contains("Logger.Message_OUT")) {
            messageEvent = MessageEvent.createEvent(processId, MessageEvent.OUTBOUND, output.substring(output.indexOf("Logger.Message_OUT") + 20));
        } else if (output.contains("Logger.Message_IN")) {
            messageEvent = MessageEvent.createEvent(processId, MessageEvent.INBOUND, output.substring(output.indexOf("Logger.Message_IN") + 19));
        }
    }

    @Override
    public void onProcessOutput(String processId, String output) {
        messagingTemplate.convertAndSend(TOPIC_LOG_OUTPUT, SocketEvent.createEvent(processId, SocketEvent.EventType.LOG_MESSAGE, output));
    }

    @Override
    public void onProcessStart(String processId) {
        messagingTemplate.convertAndSend(TOPIC_LOG_OUTPUT, SocketEvent.createEvent(processId, SocketEvent.EventType.PROCESS_START, "process started" + System.lineSeparator()));
    }

    @Override
    public void onProcessSuccess(String processId) {
        messagingTemplate.convertAndSend(TOPIC_LOG_OUTPUT, SocketEvent.createEvent(processId, SocketEvent.EventType.PROCESS_SUCCESS, "process completed successfully" + System.lineSeparator()));
    }

    @Override
    public void onProcessFail(String processId, int exitCode) {
        messagingTemplate.convertAndSend(TOPIC_LOG_OUTPUT, SocketEvent.createEvent(processId, SocketEvent.EventType.PROCESS_FAILED, "process failed with exit code " + exitCode + System.lineSeparator()));
    }

    @Override
    public void onProcessFail(String processId, Throwable e) {
        messagingTemplate.convertAndSend(TOPIC_LOG_OUTPUT, SocketEvent.createEvent(processId, SocketEvent.EventType.PROCESS_FAILED, "process failed with exception " + e.getLocalizedMessage() + System.lineSeparator()));
    }

    /**
     * Checks if output line is normal log output in log4j format.
     * @param output the output to check
     * @return true if normal log4j output
     */
    private boolean isProcessOutputLine(String output) {
        return output.contains("INFO") || output.contains("DEBUG") || output.contains("ERROR") || output.contains("WARN") || output.contains("TRACE");
    }
}
