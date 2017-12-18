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

package com.consol.citrus.admin.marshal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;

import javax.xml.bind.Marshaller;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Christoph Deppisch
 */
@Component
public class TestActionMarshaller extends Jaxb2Marshaller {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(TestActionMarshaller.class);

    private TestActionNamespacePrefixMapper namespacePrefixMapper = new TestActionNamespacePrefixMapper();

    public TestActionMarshaller() {
        setContextPaths("com.consol.citrus.model.testcase.core",
                "com.consol.citrus.model.testcase.http",
                "com.consol.citrus.model.testcase.ws",
                "com.consol.citrus.model.testcase.jms",
                "com.consol.citrus.model.testcase.docker",
                "com.consol.citrus.model.testcase.kubernetes",
                "com.consol.citrus.model.testcase.selenium");

        Map<String, Object> marshallerProperties = new HashMap<>();
        marshallerProperties.put(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshallerProperties.put(Marshaller.JAXB_ENCODING, "UTF-8");
        marshallerProperties.put(Marshaller.JAXB_FRAGMENT, true);

        marshallerProperties.put("com.sun.xml.bind.namespacePrefixMapper", namespacePrefixMapper);
        setMarshallerProperties(marshallerProperties);

        try {
            afterPropertiesSet();
        } catch (Exception e) {
            log.warn("Failed to setup configuration component marshaller", e);
        }
    }

    /**
     * Gets the namespacePrefixMapper.
     *
     * @return
     */
    public TestActionNamespacePrefixMapper getNamespacePrefixMapper() {
        return namespacePrefixMapper;
    }

    /**
     * Sets the namespacePrefixMapper.
     *
     * @param namespacePrefixMapper
     */
    public void setNamespacePrefixMapper(TestActionNamespacePrefixMapper namespacePrefixMapper) {
        this.namespacePrefixMapper = namespacePrefixMapper;
    }
}
