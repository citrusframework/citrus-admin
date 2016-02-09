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
import com.consol.citrus.message.MessageConverter;
import com.consol.citrus.model.config.jms.JmsEndpointDefinition;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.jms.ConnectionFactory;

/**
 * @author Christoph Deppisch
 */
@Component
public class JmsEndpointConverter extends AbstractEndpointConverter<JmsEndpointDefinition> {

    @Override
    public EndpointDefinition convert(JmsEndpointDefinition definition) {
        EndpointDefinition endpointData = new EndpointDefinition(getEndpointType(), definition.getId(), getModelClass());

        if (StringUtils.hasText(definition.getDestinationName())) {
            endpointData.add(property("destinationName", "Destination", definition));
        } else {
            endpointData.add(property("destination", definition.getDestination(), definition));
        }

        endpointData.add(property("connectionFactory", definition)
                .optionKey(ConnectionFactory.class.getName()));
        endpointData.add(property("messageConverter", definition)
                .optionKey(MessageConverter.class.getName()));
        endpointData.add(property("jmsTemplate", definition)
                .optionKey(JmsTemplate.class.getName()));
        endpointData.add(property("pubSubDomain", definition, "false")
                .options("true", "false"));

        addEndpointProperties(endpointData, definition);

        return endpointData;
    }

    @Override
    public Class<JmsEndpointDefinition> getModelClass() {
        return JmsEndpointDefinition.class;
    }
}
