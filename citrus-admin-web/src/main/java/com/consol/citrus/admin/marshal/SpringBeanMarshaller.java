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
public class SpringBeanMarshaller extends Jaxb2Marshaller {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(SpringBeanMarshaller.class);

    private NamespacePrefixMapper namespacePrefixMapper = new NamespacePrefixMapper();

    public SpringBeanMarshaller() {
        setContextPaths("com.consol.citrus.admin.model.spring",
                "com.consol.citrus.model.config.core",
                "com.consol.citrus.model.config.jms",
                "com.consol.citrus.model.config.ws",
                "com.consol.citrus.model.config.websocket",
                "com.consol.citrus.model.config.mail",
                "com.consol.citrus.model.config.ssh",
                "com.consol.citrus.model.config.vertx",
                "com.consol.citrus.model.config.ftp",
                "com.consol.citrus.model.config.mail",
                "com.consol.citrus.model.config.docker",
                "com.consol.citrus.model.config.kubernetes",
                "com.consol.citrus.model.config.selenium",
                "com.consol.citrus.model.config.rmi",
                "com.consol.citrus.model.config.jmx",
                "com.consol.citrus.model.config.http");

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
    public NamespacePrefixMapper getNamespacePrefixMapper() {
        return namespacePrefixMapper;
    }

    /**
     * Sets the namespacePrefixMapper.
     *
     * @param namespacePrefixMapper
     */
    public void setNamespacePrefixMapper(NamespacePrefixMapper namespacePrefixMapper) {
        this.namespacePrefixMapper = namespacePrefixMapper;
    }
}
