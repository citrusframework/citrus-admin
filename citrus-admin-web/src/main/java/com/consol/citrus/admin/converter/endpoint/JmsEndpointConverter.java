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

import com.consol.citrus.admin.model.EndpointModel;
import com.consol.citrus.message.MessageConverter;
import com.consol.citrus.model.config.jms.JmsEndpointModel;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;

/**
 * @author Christoph Deppisch
 */
@Component
public class JmsEndpointConverter extends AbstractEndpointConverter<JmsEndpointModel> {

    @Override
    public EndpointModel convert(JmsEndpointModel model) {
        EndpointModel endpointModel = new EndpointModel(getEndpointType(), model.getId(), getSourceModelClass().getName());

        endpointModel.add(property("destinationName", "Destination Name", model));
        endpointModel.add(property("destination", "Destination", model)
                .optionKey(Destination.class.getName()));

        endpointModel.add(property("connectionFactory", model)
                .optionKey(ConnectionFactory.class.getName()));
        endpointModel.add(property("messageConverter", model)
                .optionKey(MessageConverter.class.getName()));
        endpointModel.add(property("jmsTemplate", model)
                .optionKey(JmsTemplate.class.getName()));
        endpointModel.add(property("pubSubDomain", model, "false")
                .options("true", "false"));

        addEndpointProperties(endpointModel, model);

        return endpointModel;
    }

    @Override
    public Class<JmsEndpointModel> getSourceModelClass() {
        return JmsEndpointModel.class;
    }
}
