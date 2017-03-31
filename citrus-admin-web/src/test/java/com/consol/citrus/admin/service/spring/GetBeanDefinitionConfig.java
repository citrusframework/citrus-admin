package com.consol.citrus.admin.service.spring;

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
}
