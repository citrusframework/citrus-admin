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

import com.consol.citrus.endpoint.resolver.EndpointUriResolver;
import com.consol.citrus.message.ErrorHandlingStrategy;
import com.consol.citrus.model.config.ws.WebServiceClientModel;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapMessageFactory;
import org.springframework.ws.transport.WebServiceMessageSender;

import java.util.*;

/**
 * @author Christoph Deppisch
 */
@Component
public class WebServiceClientConverter extends AbstractEndpointConverter<WebServiceClientModel> {

    @Override
    protected String getId(WebServiceClientModel model) {
        return model.getId();
    }

    @Override
    protected String[] getRequiredFields() {
        return new String[] { "requestUrl" };
    }

    @Override
    protected Map<String, Object> getDefaultValueMappings() {
        Map<String, Object> mappings = super.getDefaultValueMappings();
        mappings.put("faultStrategy", ErrorHandlingStrategy.THROWS_EXCEPTION.getName());
        return mappings;
    }

    @Override
    protected Map<String, Class<?>> getOptionTypeMappings() {
        Map<String, Class<?>> mappings = super.getOptionTypeMappings();
        mappings.put("messageSender", WebServiceMessageSender.class);
        mappings.put("endpointResolver", EndpointUriResolver.class);
        mappings.put("messageFactory", SoapMessageFactory.class);
        mappings.put("webServiceTemplate", WebServiceTemplate.class);
        return mappings;
    }

    @Override
    protected Map<String, String[]> getFieldOptionMappings() {
        Map<String, String[]> mappings = new HashMap<>();
        mappings.put("faultStrategy", getErrorHandlingStrategyOptions());
        return mappings;
    }

    /**
     * Gets the error handling strategy names as list.
     * @return
     */
    private String[] getErrorHandlingStrategyOptions() {
        List<String> strategyNames = new ArrayList<String>();
        for (ErrorHandlingStrategy errorHandlingStrategy : ErrorHandlingStrategy.values()) {
            strategyNames.add(errorHandlingStrategy.getName());
        }
        return strategyNames.toArray(new String[strategyNames.size()]);
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
