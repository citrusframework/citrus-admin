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

package com.consol.citrus.admin.connector;

import com.consol.citrus.*;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.message.Message;
import com.consol.citrus.report.*;
import net.minidev.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.xml.transform.StringResult;

import java.io.PrintWriter;

/**
 * @author Christoph Deppisch
 */
public class WebSocketPushEventsListener extends AbstractTestListener implements MessageListener, TestListener, TestActionListener, InitializingBean {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(WebSocketPushEventsListener.class);

    /** Admin connector host and port */
    private String host = "localhost";
    private int port = 8080;

    /** Rest template instance */
    private RestTemplate restTemplate;

    /** Disable web socket push feature when connection failure */
    private boolean disabled = false;

    @Override
    public void onTestStart(TestCase test) {
        pushTestEvent(getTestEvent(test, "TEST_START", test.getName()));
    }

    @Override
    public void onTestFailure(TestCase test, Throwable cause) {
        pushTestEvent(getTestEvent(test, "TEST_FAILED", test.getName()));
        pushResult(getTestResult(test, cause, false));
    }

    @Override
    public void onTestSuccess(TestCase test) {
        pushTestEvent(getTestEvent(test, "TEST_SUCCESS", test.getName()));
        pushResult(getTestResult(test, null, true));
    }

    @Override
    public void onInboundMessage(Message message, TestContext testContext) {
        pushMessage(getProcessId(testContext), message, "inbound");
    }

    @Override
    public void onOutboundMessage(Message message, TestContext testContext) {
        pushMessage(getProcessId(testContext), message, "outbound");
    }

    @Override
    public void onTestActionStart(TestCase test, TestAction testAction) {
        pushTestEvent(getTestEvent(test, "TEST_ACTION_START", testAction.getName()));
    }

    @Override
    public void onTestActionFinish(TestCase test, TestAction testAction) {
        pushTestEvent(getTestEvent(test, "TEST_ACTION_FINISH", testAction.getName()));
    }

    @Override
    public void onTestActionSkipped(TestCase test, TestAction testAction) {
    }

    /**
     * Push test result to citrus-admin connector via REST API.
     * @param testResult
     */
    protected void pushResult(String testResult) {
        try {
            if (!disabled) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> httpRequest = new HttpEntity<>(testResult, headers);

                ResponseEntity<String> response = getRestTemplate().exchange(getConnectorBaseUrl() + "/result", HttpMethod.POST, httpRequest, String.class);

                if (response.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                    disabled = true;
                }
            }
        } catch (RestClientException e) {
            log.error("Failed to push test result to citrus-admin connector", e);
        }
    }

    /**
     * Push test event to citrus-admin connector via REST API.
     * @param event
     */
    protected void pushTestEvent(String event) {
        try {
            if (!disabled) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> httpRequest = new HttpEntity<>(event, headers);

                ResponseEntity<String> response = getRestTemplate().exchange(getConnectorBaseUrl() + "/test-event", HttpMethod.POST, httpRequest, String.class);

                if (response.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                    disabled = true;
                }
            }
        } catch (RestClientException e) {
            log.error("Failed to push test result to citrus-admin connector", e);
        }
    }

    /**
     * Push message to citrus-admin connector via REST API.
     * @param processId
     * @param message
     * @param direction
     */
    protected void pushMessage(String processId, Message message, String direction) {
        try {
            if (!disabled) {
                ResponseEntity<String> response = getRestTemplate().exchange(getConnectorBaseUrl() + String.format("/message/%s?processId=%s", direction, processId), HttpMethod.POST, new HttpEntity<>(message.toString()), String.class);

                if (response.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                    disabled = true;
                }
            }
        } catch (RestClientException e) {
            log.error("Failed to push message to citrus-admin connector", e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            ResponseEntity response = getRestTemplate().getForEntity(getConnectorBaseUrl() + "/status", String.class);
            disabled = !response.getStatusCode().equals(HttpStatus.OK);
        } catch (RestClientException e) {
            log.warn(String.format("Failed to connect to citrus-admin connector: '%s' - disabling citrus-admin connector features", getConnectorBaseUrl() + "/status"));
            disabled = true;
        }
    }

    /**
     * Construct base url with host and port.
     * @return
     */
    protected String getConnectorBaseUrl() {
        return String.format("http://%s:%s/api/connector", host, port);
    }

    /**
     * Construct JSON test result from given test data.
     * @param test
     * @param cause
     * @param success
     * @return
     */
    protected String getTestResult(TestCase test, Throwable cause, boolean success) {
        JSONObject resultObject = new JSONObject();
        JSONObject testObject = new JSONObject();

        testObject.put("name", test.getName());
        testObject.put("type", "JAVA");
        testObject.put("className", test.getTestClass().getSimpleName());
        testObject.put("packageName", test.getPackageName());

        resultObject.put("test", testObject);
        resultObject.put("processId", test.getName());
        resultObject.put("success", success);

        if (cause != null) {
            resultObject.put("errorMessage", cause.getMessage());
            resultObject.put("errorCause", cause.getCause() != null ? cause.getCause().getClass().getName() : cause.getClass().getName());

            StringResult stackTrace = new StringResult();
            cause.printStackTrace(new PrintWriter(stackTrace.getWriter()));
            resultObject.put("stackTrace", stackTrace.toString());
        }

        return resultObject.toJSONString();
    }

    /**
     * Construct JSON test event from given test data and event name.
     * @param test
     * @param eventName
     * @param msg
     * @return
     */
    protected String getTestEvent(TestCase test, String eventName, String msg) {
        JSONObject eventObject = new JSONObject();
        eventObject.put("processId", test.getName());
        eventObject.put("type", eventName);
        eventObject.put("msg", msg);

        return eventObject.toJSONString();
    }

    /**
     * Extract process id from test context when available.
     * @param testContext
     * @return
     */
    private String getProcessId(TestContext testContext) {
        if (testContext != null && testContext.getVariables().containsKey(Citrus.TEST_NAME_VARIABLE)) {
            return testContext.getVariable(Citrus.TEST_NAME_VARIABLE);
        } else {
            return "";
        }
    }

    /**
     * Initializes rest template if not set yet.
     * @return
     */
    public RestTemplate getRestTemplate() {
        if (restTemplate == null) {
            restTemplate = new RestTemplate();
        }
        return restTemplate;
    }

    /**
     * Sets the host property.
     *
     * @param host
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Sets the port property.
     *
     * @param port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Sets the restTemplate property.
     *
     * @param restTemplate
     */
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
}
