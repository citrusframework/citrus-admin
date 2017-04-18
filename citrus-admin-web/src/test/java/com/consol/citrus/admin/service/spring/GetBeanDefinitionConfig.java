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

package com.consol.citrus.admin.service.spring;

import com.consol.citrus.TestActor;
import com.consol.citrus.dsl.endpoint.CitrusEndpoints;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.jms.endpoint.JmsEndpoint;
import com.consol.citrus.jms.endpoint.JmsSyncEndpoint;
import org.springframework.context.annotation.Bean;

/**
 * @author Christoph Deppisch
 */
public class GetBeanDefinitionConfig {

    @Bean
    public JmsEndpoint jmsInboundEndpoint() {
        return CitrusEndpoints
                .jms()
                .asynchronous()
                .destination("jms.inbound.queue")
                .build();
    }

    @Bean
    public JmsEndpoint jmsOutboundEndpoint() {
        return CitrusEndpoints
                .jms()
                .asynchronous()
                .destination("jms.outbound.queue")
                .build();
    }

    @Bean("httpClient")
    public HttpClient client() {
        return CitrusEndpoints
                .http()
                .client()
                .requestUrl("http://localhost:8080/foo")
                .build();
    }

    @Bean(name = "jmsSyncEndpoint")
    public JmsSyncEndpoint jmsEndpoint() {
        return CitrusEndpoints
                .jms()
                .synchronous()
                .destination("jms.inbound.sync.queue")
                .build();
    }

    @Bean
    public TestActor testActor() {
        TestActor actor = new TestActor();
        actor.setName("testActor");
        return actor;
    }
}
