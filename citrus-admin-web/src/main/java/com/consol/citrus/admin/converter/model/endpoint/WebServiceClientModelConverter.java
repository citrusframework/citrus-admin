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

package com.consol.citrus.admin.converter.model.endpoint;

import com.consol.citrus.model.config.ws.WebServiceClientModel;
import com.consol.citrus.ws.client.WebServiceClient;
import com.consol.citrus.ws.client.WebServiceEndpointConfiguration;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component
public class WebServiceClientModelConverter extends AbstractEndpointModelConverter<WebServiceClientModel, WebServiceClient, WebServiceEndpointConfiguration> {

    /**
     * Default constructor.
     */
    public WebServiceClientModelConverter() {
        super(WebServiceClientModel.class, WebServiceClient.class, WebServiceEndpointConfiguration.class);
    }

    @Override
    public WebServiceClientModel convert(String id, WebServiceClient model) {
        WebServiceClientModel converted = convert(model);
        converted.setId(id);
        return converted;
    }

    @Override
    public String getJavaConfig(WebServiceClientModel model) {
        return getJavaConfig(model, model.getId(), "soap().client()");
    }
}
