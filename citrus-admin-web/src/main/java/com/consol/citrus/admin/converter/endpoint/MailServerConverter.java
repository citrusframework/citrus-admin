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

package com.consol.citrus.admin.converter.endpoint;

import com.consol.citrus.admin.model.EndpointDefinition;
import com.consol.citrus.endpoint.EndpointAdapter;
import com.consol.citrus.message.MessageConverter;
import com.consol.citrus.model.config.mail.MailServerDefinition;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * @author Christoph Deppisch
 */
@Component
public class MailServerConverter extends AbstractEndpointConverter<MailServerDefinition> {

    public static final String TRUE = "true";
    public static final String FALSE = "false";

    @Override
    public EndpointDefinition convert(MailServerDefinition server) {
        EndpointDefinition endpointData = new EndpointDefinition(getEndpointType(), server.getId(), getModelClass().getName());

        endpointData.add(property("port", server));
        endpointData.add(property("autoStart", server, TRUE)
                .options(TRUE, FALSE));
        endpointData.add(property("autoAccept", server, TRUE)
                .options(TRUE, FALSE));
        endpointData.add(property("splitMultipart", server, TRUE)
                .options(TRUE, FALSE));
        endpointData.add(property("messageConverter", server)
                .optionKey(MessageConverter.class.getName()));
        endpointData.add(property("endpointAdapter", server)
                .optionKey(EndpointAdapter.class.getName()));
        endpointData.add(property("mailProperties", server)
                .optionKey(Properties.class.getName()));

        endpointData.add(property("timeout", server, "5000"));

        return endpointData;
    }

    @Override
    public Class<MailServerDefinition> getModelClass() {
        return MailServerDefinition.class;
    }

    @Override
    public String getEndpointType() {
        return "mail-server";
    }
}
