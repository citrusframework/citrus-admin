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
