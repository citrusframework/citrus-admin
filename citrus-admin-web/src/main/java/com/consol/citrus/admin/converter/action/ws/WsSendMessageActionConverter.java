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

package com.consol.citrus.admin.converter.action.ws;

import com.consol.citrus.Citrus;
import com.consol.citrus.admin.converter.action.AbstractTestActionConverter;
import com.consol.citrus.admin.model.Property;
import com.consol.citrus.admin.model.TestActionModel;
import com.consol.citrus.config.xml.PayloadElementParser;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.model.testcase.ws.ObjectFactory;
import com.consol.citrus.model.testcase.ws.SendModel;
import com.consol.citrus.ws.actions.SendSoapMessageAction;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Christoph Deppisch
 */
@Component
public class WsSendMessageActionConverter extends AbstractTestActionConverter<SendModel, SendSoapMessageAction> {

    /**
     * Default constructor using action type reference.
     */
    public WsSendMessageActionConverter() {
        super("ws:send");
    }

    @Override
    public TestActionModel convert(SendModel model) {
        TestActionModel action = super.convert(model);

        if (model.getMessage() != null) {
            action.add(new Property<>("message.name", "message.name", "MessageName", model.getMessage().getName(), false));

            action.add(new Property<>("message.type", "message.type", "MessageType", Optional.ofNullable(model.getMessage().getType()).orElse(Citrus.DEFAULT_MESSAGE_TYPE).toLowerCase(), true)
                                .options(Stream.of(MessageType.values()).map(MessageType::name).map(String::toLowerCase).collect(Collectors.toList())));
        }

        return action;
    }

    @Override
    protected <V> V resolvePropertyExpression(V value) {
        if (value instanceof SendModel.Message) {
            SendModel.Message message = (SendModel.Message) value;

            if (StringUtils.hasText(message.getData())) {
                return (V) message.getData().trim();
            }

            if (message.getPayload()!= null) {
                return (V) PayloadElementParser.parseMessagePayload(message.getPayload().getAnies().get(0));
            }

            if (message.getResource() != null &&
                    StringUtils.hasText(message.getResource().getFile())) {
                return (V) message.getResource().getFile();
            }

            if (message.getBuilder() != null) {
                return (V) message.getBuilder().getValue().trim();
            }
        }

        if (value instanceof SendModel.Header) {
            return (V) ((SendModel.Header) value).getElements().stream().map(header -> header.getName() + "=" + header.getValue()).collect(Collectors.joining(","));
        }

        return super.resolvePropertyExpression(value);
    }

    @Override
    protected Map<String, Object> getDefaultValueMappings() {
        Map<String, Object> mappings = super.getDefaultValueMappings();
        mappings.put("fork", FALSE);
        return mappings;
    }

    @Override
    public SendModel convertModel(SendSoapMessageAction model) {
        SendModel action = new ObjectFactory().createSendModel();

        if (model.getActor() != null) {
            action.setActor(model.getActor().getName());
        } else if (model.getEndpoint() != null && model.getEndpoint().getActor() != null) {
            action.setActor(model.getEndpoint().getActor().getName());
        }

        action.setDescription(model.getDescription());
        action.setEndpoint(model.getEndpoint() != null ? model.getEndpoint().getName() : model.getEndpointUri());
        action.setFork(model.isForkMode());

        return action;
    }

    @Override
    public Class<SendModel> getSourceModelClass() {
        return SendModel.class;
    }

    @Override
    public Class<SendSoapMessageAction> getActionModelClass() {
        return SendSoapMessageAction.class;
    }
}
