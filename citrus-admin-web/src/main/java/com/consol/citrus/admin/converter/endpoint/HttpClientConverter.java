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
import com.consol.citrus.message.*;
import com.consol.citrus.model.config.http.HttpClientModel;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Christoph Deppisch
 */
@Component
public class HttpClientConverter extends AbstractEndpointConverter<HttpClientModel> {

    @Override
    public EndpointModel convert(HttpClientModel model) {
        EndpointModel endpointModel = new EndpointModel(getEndpointType(), model.getId(), getSourceModelClass());

        endpointModel.add(property("requestUrl", model, true));
        endpointModel.add(property("requestMethod", model, HttpMethod.POST.name())
                .options(getHttpMethodOptions()));
        endpointModel.add(property("errorStrategy", model, ErrorHandlingStrategy.PROPAGATE.getName())
                .options(getErrorHandlingStrategyOptions()));
        endpointModel.add(property("pollingInterval", model, "500"));
        endpointModel.add(property("messageCorrelator", model)
                .optionKey(MessageCorrelator.class.getName()));
        endpointModel.add(property("messageConverter", model)
                .optionKey(MessageConverter.class.getName()));
        endpointModel.add(property("requestFactory", model)
                .optionKey(ClientHttpRequestFactory.class.getName()));
        endpointModel.add(property("restTemplate", model)
                .optionKey(RestTemplate.class.getName()));
        endpointModel.add(property("charset", model));
        endpointModel.add(property("contentType", model));
        endpointModel.add(property("interceptors", model));

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

    /**
     * Gets the available Http request method names as list.
     * @return
     */
    private List<String> getHttpMethodOptions() {
        List<String> methodNames = new ArrayList<String>();
        for (HttpMethod method : HttpMethod.values()) {
            methodNames.add(method.name());
        }
        return methodNames;
    }

    @Override
    public Class<HttpClientModel> getSourceModelClass() {
        return HttpClientModel.class;
    }

    @Override
    public String getEndpointType() {
        return "http-client";
    }
}
