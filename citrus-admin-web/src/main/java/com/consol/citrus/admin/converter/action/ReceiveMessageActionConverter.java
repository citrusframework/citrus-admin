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

package com.consol.citrus.admin.converter.action;

import com.consol.citrus.Citrus;
import com.consol.citrus.actions.ReceiveMessageAction;
import com.consol.citrus.admin.model.Property;
import com.consol.citrus.admin.model.TestActionModel;
import com.consol.citrus.config.xml.PayloadElementParser;
import com.consol.citrus.message.MessageType;
import com.consol.citrus.model.testcase.core.ObjectFactory;
import com.consol.citrus.model.testcase.core.ReceiveModel;
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
public class ReceiveMessageActionConverter extends AbstractTestActionConverter<ReceiveModel, ReceiveMessageAction> {

    /**
     * Default constructor using action type reference.
     */
    public ReceiveMessageActionConverter() {
        super("receive");
    }

    @Override
    public TestActionModel convert(ReceiveModel model) {
        TestActionModel action = super.convert(model);

        if (model.getMessage() != null) {
            action.add(new Property<>("message.name", "message.name", "MessageName", model.getMessage().getName(), false));

            action.add(new Property<>("message.type", "message.type", "MessageType", Optional.ofNullable(model.getMessage().getType()).orElse(Citrus.DEFAULT_MESSAGE_TYPE).toLowerCase(), true)
                    .options(Stream.of(MessageType.values()).map(MessageType::name).map(String::toLowerCase).collect(Collectors.toList())));
        } else {
            action.add(new Property<>("message.name", "message.name", "MessageName", null, false));
            action.add(new Property<>("message.type", "message.type", "MessageType", Citrus.DEFAULT_MESSAGE_TYPE.toLowerCase(), true)
                    .options(Stream.of(MessageType.values()).map(MessageType::name).map(String::toLowerCase).collect(Collectors.toList())));
        }

        return action;
    }

    @Override
    protected Map<String, String> getDisplayNameMappings() {
        Map<String, String> mappings = super.getDisplayNameMappings();
        mappings.put("header", "Headers");
        return mappings;
    }

    @Override
    protected <V> V resolvePropertyExpression(V value) {
        if (value instanceof ReceiveModel.Message) {
            ReceiveModel.Message message = (ReceiveModel.Message) value;

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

        if (value instanceof ReceiveModel.Header) {
            return (V) ((ReceiveModel.Header) value).getElements().stream().map(header -> header.getName() + "=" + header.getValue()).collect(Collectors.joining(","));
        }

        if (value instanceof ReceiveModel.Selector) {
            ReceiveModel.Selector selector = (ReceiveModel.Selector) value;

            if (StringUtils.hasText(selector.getSelectorValue())) {
                return (V) selector.getSelectorValue().trim();
            } else {
                return (V) selector.getElements().stream().map(header -> header.getName() + "=" + header.getValue()).collect(Collectors.joining(","));
            }
        }

        return super.resolvePropertyExpression(value);
    }

    @Override
    public ReceiveModel convertModel(ReceiveMessageAction model) {
        ReceiveModel action = new ObjectFactory().createReceiveModel();

        if (model.getActor() != null) {
            action.setActor(model.getActor().getName());
        } else if (model.getEndpoint() != null && model.getEndpoint().getActor() != null) {
            action.setActor(model.getEndpoint().getActor().getName());
        }

        action.setDescription(model.getDescription());
        action.setEndpoint(model.getEndpoint() != null ? model.getEndpoint().getName() : model.getEndpointUri());

        return action;
    }

    @Override
    public Class<ReceiveModel> getSourceModelClass() {
        return ReceiveModel.class;
    }

    @Override
    public Class<ReceiveMessageAction> getActionModelClass() {
        return ReceiveMessageAction.class;
    }
}
