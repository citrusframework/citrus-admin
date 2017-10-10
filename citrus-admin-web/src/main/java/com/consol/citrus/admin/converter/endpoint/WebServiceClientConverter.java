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
import com.consol.citrus.endpoint.resolver.EndpointUriResolver;
import com.consol.citrus.message.*;
import com.consol.citrus.model.config.ws.WebServiceClientModel;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapMessageFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Christoph Deppisch
 */
@Component
public class WebServiceClientConverter extends AbstractEndpointConverter<WebServiceClientModel> {

    @Override
    public EndpointModel convert(WebServiceClientModel model) {
        EndpointModel endpointModel = new EndpointModel(getEndpointType(), model.getId(), getSourceModelClass());

        endpointModel.add(property("requestUrl", model, true));
        endpointModel.add(property("webServiceTemplate", model)
                .optionKey(WebServiceTemplate.class.getName()));
        endpointModel.add(property("messageFactory", model)
                .optionKey(SoapMessageFactory.class.getName()));
        endpointModel.add(property("messageSender", model));
        endpointModel.add(property("messageCorrelator", model)
                .optionKey(MessageCorrelator.class.getName()));
        endpointModel.add(property("interceptors", model));
        endpointModel.add(property("endpointResolver", model)
                .optionKey(EndpointUriResolver.class.getName()));
        endpointModel.add(property("messageConverter", model)
                .optionKey(MessageConverter.class.getName()));
        endpointModel.add(property("faultStrategy", model, ErrorHandlingStrategy.THROWS_EXCEPTION.getName())
                .options(getErrorHandlingStrategyOptions()));
        endpointModel.add(property("pollingInterval", model));

        addEndpointProperties(endpointModel, model);

        return endpointModel;
    }

    /**
     * Gets the error handling strategy names as list.
     * @return
     */
    private List<String> getErrorHandlingStrategyOptions() {
        List<String> strategyNames = new ArrayList<String>();
        for (ErrorHandlingStrategy errorHandlingStrategy : ErrorHandlingStrategy.values()) {
            strategyNames.add(errorHandlingStrategy.getName());
        }
        return strategyNames;
    }

    @Override
    public Class<WebServiceClientModel> getSourceModelClass() {
        return WebServiceClientModel.class;
    }

    @Override
    public String getEndpointType() {
        return "ws-client";
    }
}
