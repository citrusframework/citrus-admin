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
import com.consol.citrus.model.config.camel.CamelEndpointModel;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component
public class CamelEndpointConverter extends AbstractEndpointConverter<CamelEndpointModel> {

    @Override
    public EndpointModel convert(CamelEndpointModel model) {
        EndpointModel endpointModel = new EndpointModel(getEndpointType(), model.getId(), getSourceModelClass().getName());

        endpointModel.add(property("camelContext", model));
        endpointModel.add(property("endpointUri", model, true));

        endpointModel.add(property("messageConverter", model)
                .optionKey(MessageConverter.class.getName()));

        addEndpointProperties(endpointModel, model);

        return endpointModel;
    }

    @Override
    public Class<CamelEndpointModel> getSourceModelClass() {
        return CamelEndpointModel.class;
    }

    @Override
    public String getEndpointType() {
        return "camel";
    }
}
