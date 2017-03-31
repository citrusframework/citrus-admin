package com.consol.citrus.admin.service.spring;

import com.consol.citrus.xml.XsdSchemaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.xml.xsd.SimpleXsdSchema;

import java.util.Collections;

/**
 * @author Christoph Deppisch
 */
public class SchemaRepositoryConfig {

    @Bean("mySchemaRepository")
    public XsdSchemaRepository schemaRepository() {
        XsdSchemaRepository schemaRepository = new XsdSchemaRepository();

        schemaRepository.setSchemas(Collections.singletonList(mySchema()));
        schemaRepository.setLocations(Collections.singletonList("classpath*:com/consol/citrus/schemas/*.xsd"));

        return schemaRepository;
    }

    @Bean
    public SimpleXsdSchema mySchema() {
        return new SimpleXsdSchema();
    }
}
