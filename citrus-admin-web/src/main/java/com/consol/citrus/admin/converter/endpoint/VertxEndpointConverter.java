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

package com.consol.citrus.admin.converter.endpoint;

import com.consol.citrus.admin.model.EndpointModel;
import com.consol.citrus.message.MessageConverter;
import com.consol.citrus.model.config.vertx.VertxEndpointModel;
import com.consol.citrus.vertx.factory.VertxInstanceFactory;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component
public class VertxEndpointConverter extends AbstractEndpointConverter<VertxEndpointModel> {

    @Override
    public EndpointModel convert(VertxEndpointModel model) {
        EndpointModel endpointModel = new EndpointModel(getEndpointType(), model.getId(), getSourceModelClass());

        endpointModel.add(property("host", model, true));
        endpointModel.add(property("port", model, true));
        endpointModel.add(property("address", model, true));
        endpointModel.add(property("pollingInterval", model, "500"));

        endpointModel.add(property("messageConverter", model)
                .optionType(MessageConverter.class));

        endpointModel.add(property("vertxFactory", model)
                .optionType(VertxInstanceFactory.class));

        endpointModel.add(property("pubSubDomain", model, "false")
                .options("true", "false"));

        addEndpointProperties(endpointModel, model);

        return endpointModel;
    }

    @Override
    public Class<VertxEndpointModel> getSourceModelClass() {
        return VertxEndpointModel.class;
    }

    @Override
    public String getEndpointType() {
        return "vertx";
    }
}
