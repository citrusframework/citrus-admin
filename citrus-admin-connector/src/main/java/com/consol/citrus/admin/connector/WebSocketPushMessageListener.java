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

import com.consol.citrus.Citrus;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.message.Message;
import com.consol.citrus.report.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

/**
 * @author Christoph Deppisch
 */
public class WebSocketPushMessageListener implements MessageListener, InitializingBean {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(WebSocketPushMessageListener.class);

    /** Admin connector host and port */
    private String host = "localhost";
    private int port = 8080;

    /** Rest template instance */
    private RestTemplate restTemplate;

    /** Disable web socket push feature when connection failure */
    private boolean disabled = false;

    @Override
    public void onInboundMessage(Message message, TestContext testContext) {
        push(getProcessId(testContext), message, "inbound");
    }

    @Override
    public void onOutboundMessage(Message message, TestContext testContext) {
        push(getProcessId(testContext), message, "outbound");
    }

    /**
     * Push message to citrus-admin connector via REST API.
     * @param processId
     * @param message
     * @param direction
     */
    protected void push(String processId, Message message, String direction) {
        try {
            if (!disabled) {
                ResponseEntity<String> response = getRestTemplate().exchange(String.format("http://%s:%s/connector/message/%s?processId=%s", host, port, direction, processId), HttpMethod.POST, new HttpEntity(message.toString()), String.class);

                if (response.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                    disabled = true;
                }
            }
        } catch (ResourceAccessException e) {
            log.error("Failed to push message to citrus-admin connector", e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String statusUrl = String.format("http://%s:%s/connector/status", host, port);
        try {
            ResponseEntity response = getRestTemplate().getForEntity(statusUrl, String.class);
            disabled = !response.getStatusCode().equals(HttpStatus.OK);
        } catch (ResourceAccessException e) {
            log.warn(String.format("Failed to connect to citrus-admin connector: '%s' - disabling citrus-admin connector features", statusUrl));
            disabled = true;
        }
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
