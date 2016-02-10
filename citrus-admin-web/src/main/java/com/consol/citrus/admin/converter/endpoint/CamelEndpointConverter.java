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
import com.consol.citrus.model.config.camel.CamelEndpointDefinition;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component
public class CamelEndpointConverter extends AbstractEndpointConverter<CamelEndpointDefinition> {

    @Override
    public EndpointDefinition convert(CamelEndpointDefinition definition) {
        EndpointDefinition endpointData = new EndpointDefinition(getEndpointType(), definition.getId(), getModelClass().getName());

        endpointData.add(property("camelContext", definition));
        endpointData.add(property("endpointUri", definition));

        endpointData.add(property("messageConverter", definition)
                .optionKey(MessageConverter.class.getName()));

        addEndpointProperties(endpointData, definition);

        return endpointData;
    }

    @Override
    public Class<CamelEndpointDefinition> getModelClass() {
        return CamelEndpointDefinition.class;
    }

    @Override
    public String getEndpointType() {
        return "camel";
    }
}
