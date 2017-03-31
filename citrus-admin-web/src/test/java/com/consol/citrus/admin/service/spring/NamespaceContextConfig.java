package com.consol.citrus.admin.service.spring;

import com.consol.citrus.xml.namespace.NamespaceContextBuilder;
import org.springframework.context.annotation.Bean;

/**
 * @author Christoph Deppisch
 */
public class NamespaceContextConfig {

    @Bean
    public NamespaceContextBuilder namespaceContext() {
        NamespaceContextBuilder namespaceContext = new NamespaceContextBuilder();

        namespaceContext.getNamespaceMappings().put("foo", "http://sample.namespaces.com/foo");
        namespaceContext.getNamespaceMappings().put("bar", "http://sample.namespaces.com/bar");

        return namespaceContext;
    }
}
