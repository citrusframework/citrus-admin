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
import com.consol.citrus.model.config.core.ChannelEndpointModel;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.messaging.core.DestinationResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @author Christoph Deppisch
 */
@Component
public class ChannelEndpointConverter extends AbstractEndpointConverter<ChannelEndpointModel> {

    @Override
    public EndpointModel convert(ChannelEndpointModel model) {
        EndpointModel endpointModel = new EndpointModel(getEndpointType(), model.getId(), getSourceModelClass().getName());

        if (StringUtils.hasText(model.getChannelName())) {
            endpointModel.add(property("channelName", "Channel", model));
        } else {
            endpointModel.add(property("channel", model));
        }

        endpointModel.add(property("messagingTemplate", model)
                .optionKey(MessagingTemplate.class.getName()));
        endpointModel.add(property("channelResolver", model)
                .optionKey(DestinationResolver.class.getName()));

        addEndpointProperties(endpointModel, model);

        return endpointModel;
    }

    @Override
    public Class<ChannelEndpointModel> getSourceModelClass() {
        return ChannelEndpointModel.class;
    }

    @Override
    public String getEndpointType() {
        return "channel";
    }
}
