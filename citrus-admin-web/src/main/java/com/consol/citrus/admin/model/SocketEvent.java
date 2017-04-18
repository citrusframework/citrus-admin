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

package com.consol.citrus.admin.model;

/**
 * All socket event types with proper JSON data generation.
 *
 * @author Christoph Deppisch
 */
public class SocketEvent {

    private String processId;
    private EventType type;
    private String msg;

    /**
     * Creates proper JSON message for socket event.
     * @param processId the process id
     * @param eventType the type of event
     * @param message the event message
     * @return a json representation of the message
     */
    @SuppressWarnings("unchecked")
    public static SocketEvent createEvent(String processId, EventType eventType, String message) {
        SocketEvent event = new SocketEvent();
        event.setProcessId(processId);
        event.setType(eventType);
        event.setMsg(message);

        return event;
    }

    public enum EventType {
        PING,
        LOG_MESSAGE,
        TEST_START,
        TEST_SUCCESS,
        TEST_FAILED,
        TEST_FINISHED,
        TEST_SKIP,
        TEST_ACTION_START,
        TEST_ACTION_FINISH,
        TEST_ACTION_SKIP,
        PROCESS_START,
        PROCESS_SUCCESS,
        PROCESS_FAILED;
    }

    /**
     * Gets the processId.
     *
     * @return
     */
    public String getProcessId() {
        return processId;
    }

    /**
     * Sets the processId.
     *
     * @param processId
     */
    public void setProcessId(String processId) {
        this.processId = processId;
    }

    /**
     * Gets the type.
     *
     * @return
     */
    public EventType getType() {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param type
     */
    public void setType(EventType type) {
        this.type = type;
    }

    /**
     * Gets the msg.
     *
     * @return
     */
    public String getMsg() {
        return msg;
    }

    /**
     * Sets the msg.
     *
     * @param msg
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }
}
