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

package com.consol.citrus.admin.web;

import com.consol.citrus.admin.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author Christoph Deppisch
 */
@Controller
@RequestMapping("api/connector")
public class ConnectorController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @RequestMapping(value = "/status")
    public ResponseEntity status() {
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/result", method = RequestMethod.POST)
    public ResponseEntity testResult(@RequestBody TestResult result) {
        messagingTemplate.convertAndSend("/topic/results", result);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/test-event", method = RequestMethod.POST)
    public ResponseEntity testResult(@RequestBody SocketEvent event) {
        messagingTemplate.convertAndSend("/topic/test-events", event);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/message/inbound", method = RequestMethod.POST)
    public ResponseEntity inboundMessage(@RequestParam("processId") String processId, @RequestBody String messageData) {
        messagingTemplate.convertAndSend("/topic/messages", MessageEvent.createEvent(processId, MessageEvent.INBOUND, messageData));
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/message/outbound", method = RequestMethod.POST)
    public ResponseEntity outboundMessage(@RequestParam("processId") String processId, @RequestBody String messageData) {
        messagingTemplate.convertAndSend("/topic/messages", MessageEvent.createEvent(processId, MessageEvent.OUTBOUND, messageData));
        return ResponseEntity.ok().build();
    }
}
