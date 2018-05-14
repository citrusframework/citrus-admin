/*
 * Copyright 2006-2018 the original author or authors.
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

import com.consol.citrus.model.config.core.MessageChannelAdapterType;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.messaging.core.DestinationResolver;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Christoph Deppisch
 */
@Component
public abstract class ChannelAdapterConverter<T extends MessageChannelAdapterType> extends AbstractEndpointConverter<T> {

    @Override
    protected String getId(T model) {
        return model.getId();
    }

    @Override
    protected Map<String, Class<?>> getOptionTypeMappings() {
        Map<String, Class<?>> mappings = super.getOptionTypeMappings();
        mappings.put("messagingTemplate", MessagingTemplate.class);
        mappings.put("channelResolver", DestinationResolver.class);
        return mappings;
    }
}
