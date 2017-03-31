package com.consol.citrus.admin.service.spring;

import org.springframework.context.annotation.Bean;

/**
 * @author Christoph Deppisch
 */
public class ChildConfig {

    @Bean
    public String sampleBean() {
        return "bean";
    }
}
