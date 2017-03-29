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
import com.consol.citrus.TestCase;
import com.consol.citrus.context.TestContext;
import com.consol.citrus.message.DefaultMessage;
import com.consol.citrus.message.Message;
import org.mockito.Mockito;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author Christoph Deppisch
 */
public class WebSocketPushEventsListenerTest {

    private WebSocketPushEventsListener pushMessageListener = new WebSocketPushEventsListener();

    private TestContext context = Mockito.mock(TestContext.class);
    private RestTemplate restTemplate = Mockito.mock(RestTemplate.class);

    @BeforeClass
    public void setup() {
        pushMessageListener.setRestTemplate(restTemplate);
    }

    @Test
    public void testOnTestResult() throws Exception {
        TestCase test = new TestCase();
        test.setName("MyTestIT");

        reset(restTemplate);
        when(restTemplate.exchange(eq("http://localhost:8080/api/connector/result"), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class))).thenAnswer(invocation -> {
            HttpEntity request = (HttpEntity) invocation.getArguments()[2];

            Assert.assertEquals(request.getBody().toString(), "{\"test\":{\"name\":\"MyTestIT\",\"className\":\"TestCase\",\"packageName\":\"com.consol.citrus\",\"type\":\"JAVA\"},\"processId\":\"MyTestIT\",\"success\":true}");

            return ResponseEntity.ok().build();
        });

        when(restTemplate.exchange(eq("http://localhost:8080/api/connector/test-event"), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class))).thenAnswer(invocation -> {
            HttpEntity request = (HttpEntity) invocation.getArguments()[2];

            Assert.assertEquals(request.getBody().toString(), "{\"msg\":\"MyTestIT\",\"processId\":\"MyTestIT\",\"type\":\"TEST_SUCCESS\"}");

            return ResponseEntity.ok().build();
        });

        pushMessageListener.onTestSuccess(test);
        verify(restTemplate).exchange(eq("http://localhost:8080/api/connector/result"), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
        verify(restTemplate).exchange(eq("http://localhost:8080/api/connector/test-event"), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
    }

    @Test
    public void testOnTestErrorResult() throws Exception {
        TestCase test = new TestCase();
        test.setName("MyTestIT");

        Throwable cause = Mockito.mock(Throwable.class);

        reset(restTemplate);

        when(cause.getMessage()).thenReturn("Something went wrong!");
        when(cause.getCause()).thenReturn(new NullPointerException());

        when(restTemplate.exchange(eq("http://localhost:8080/api/connector/result"), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class))).thenAnswer(invocation -> {
            HttpEntity request = (HttpEntity) invocation.getArguments()[2];

            Assert.assertEquals(request.getBody().toString(), "{\"errorCause\":\"java.lang.NullPointerException\",\"test\":{\"name\":\"MyTestIT\",\"className\":\"TestCase\",\"packageName\":\"com.consol.citrus\",\"type\":\"JAVA\"},\"processId\":\"MyTestIT\",\"success\":false,\"errorMessage\":\"Something went wrong!\",\"stackTrace\":\"\"}");

            return ResponseEntity.ok().build();
        });

        when(restTemplate.exchange(eq("http://localhost:8080/api/connector/test-event"), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class))).thenAnswer(invocation -> {
            HttpEntity request = (HttpEntity) invocation.getArguments()[2];

            Assert.assertEquals(request.getBody().toString(), "{\"msg\":\"MyTestIT\",\"processId\":\"MyTestIT\",\"type\":\"TEST_FAILED\"}");

            return ResponseEntity.ok().build();
        });

        pushMessageListener.onTestFailure(test, cause);
        verify(restTemplate).exchange(eq("http://localhost:8080/api/connector/result"), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
        verify(restTemplate).exchange(eq("http://localhost:8080/api/connector/test-event"), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
    }

    @Test
    public void testOnInboundMessage() throws Exception {
        Message inbound = new DefaultMessage("Hello Citrus!");

        reset(restTemplate, context);
        when(restTemplate.exchange(eq("http://localhost:8080/api/connector/message/inbound?processId=MySampleTest"), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class))).thenAnswer(invocation -> {
            HttpEntity request = (HttpEntity) invocation.getArguments()[2];

            Assert.assertEquals(request.getBody().toString(), inbound.toString());

            return ResponseEntity.ok().build();
        });

        when(context.getVariables()).thenReturn(Collections.singletonMap(Citrus.TEST_NAME_VARIABLE, "MySampleTest"));
        when(context.getVariable(Citrus.TEST_NAME_VARIABLE)).thenReturn("MySampleTest");

        pushMessageListener.onInboundMessage(inbound, context);
        verify(restTemplate).exchange(eq("http://localhost:8080/api/connector/message/inbound?processId=MySampleTest"), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
    }

    @Test
    public void testOnOutboundMessage() throws Exception {
        Message outbound = new DefaultMessage("Hello Citrus!");

        reset(restTemplate, context);
        when(restTemplate.exchange(eq("http://localhost:8080/api/connector/message/outbound?processId=MySampleTest"), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class))).thenAnswer(invocation -> {
            HttpEntity request = (HttpEntity) invocation.getArguments()[2];

            Assert.assertEquals(request.getBody().toString(), outbound.toString());

            return ResponseEntity.ok().build();
        });

        when(context.getVariables()).thenReturn(Collections.singletonMap(Citrus.TEST_NAME_VARIABLE, "MySampleTest"));
        when(context.getVariable(Citrus.TEST_NAME_VARIABLE)).thenReturn("MySampleTest");

        pushMessageListener.onOutboundMessage(outbound, context);
        verify(restTemplate).exchange(eq("http://localhost:8080/api/connector/message/outbound?processId=MySampleTest"), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
    }

    @Test
    public void testNoContext() throws Exception {
        Message inbound = new DefaultMessage("Hello Citrus!");

        reset(restTemplate, context);
        when(restTemplate.exchange(eq("http://localhost:8080/api/connector/message/inbound?processId="), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class))).thenAnswer(invocation -> {
            HttpEntity request = (HttpEntity) invocation.getArguments()[2];

            Assert.assertEquals(request.getBody().toString(), inbound.toString());

            return ResponseEntity.ok().build();
        });

        pushMessageListener.onInboundMessage(inbound, null);
        pushMessageListener.onInboundMessage(inbound, context);

        verify(restTemplate, times(2)).exchange(eq("http://localhost:8080/api/connector/message/inbound?processId="), eq(HttpMethod.POST), any(HttpEntity.class), eq(String.class));
    }
}