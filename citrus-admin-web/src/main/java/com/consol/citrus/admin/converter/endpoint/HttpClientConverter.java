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

import com.consol.citrus.message.ErrorHandlingStrategy;
import com.consol.citrus.model.config.http.HttpClientModel;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * @author Christoph Deppisch
 */
@Component
public class HttpClientConverter extends AbstractEndpointConverter<HttpClientModel> {

    @Override
    protected String getId(HttpClientModel model) {
        return model.getId();
    }

    @Override
    protected Map<String, Object> getDefaultValueMappings() {
        Map<String, Object> mappings = super.getDefaultValueMappings();
        mappings.put("requestMethod", HttpMethod.POST.name());
        mappings.put("errorStrategy", ErrorHandlingStrategy.PROPAGATE.getName());
        return mappings;
    }

    @Override
    protected Map<String, Class<?>> getOptionTypeMappings() {
        Map<String, Class<?>> mappings = super.getOptionTypeMappings();
        mappings.put("requestFactory", ClientHttpRequestFactory.class);
        mappings.put("restTemplate", RestTemplate.class);
        return mappings;
    }

    @Override
    protected Map<String, String[]> getFieldOptionMappings() {
        Map<String, String[]> mappings = new HashMap<>();
        mappings.put("requestMethod", getHttpMethodOptions());
        mappings.put("errorStrategy", getErrorHandlingStrategyOptions());
        return mappings;
    }

    @Override
    protected String[] getRequiredFields() {
        return new String[] { "requestUrl" };
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

    /**
     * Gets the available Http request method names as list.
     * @return
     */
    private String[] getHttpMethodOptions() {
        List<String> methodNames = new ArrayList<String>();
        for (HttpMethod method : HttpMethod.values()) {
            methodNames.add(method.name());
        }
        return methodNames.toArray(new String[methodNames.size()]);
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
