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

package com.consol.citrus.admin.converter.action;

import com.consol.citrus.actions.ReceiveMessageAction;
import com.consol.citrus.admin.model.Property;
import com.consol.citrus.admin.model.TestAction;
import com.consol.citrus.config.xml.PayloadElementParser;
import com.consol.citrus.model.testcase.core.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
    public TestAction convert(ReceiveModel model) {
        TestAction action = new TestAction(getActionType(), getSourceModelClass());

        action.add(property("endpoint", model));

        if (model.getMessage() != null) {
            if (StringUtils.hasText(model.getMessage().getData())) {
                action.add(new Property("message.data", "message.data", "Message Data", model.getMessage().getData(), false));
            }

            if (model.getMessage().getPayload()!= null) {
                action.add(new Property("message.payload", "message.payload", "Message Payload", PayloadElementParser.parseMessagePayload(model.getMessage().getPayload().getAnies().get(0)), false));
            }

            if (model.getMessage().getResource() != null &&
                    StringUtils.hasText(model.getMessage().getResource().getFile())) {
                action.add(new Property("message.resource", "message.resource", "Message Resource", model.getMessage().getResource().getFile(), false));
            }
        }

        action.add(property("actor", "TestActor", model));

        return action;
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
