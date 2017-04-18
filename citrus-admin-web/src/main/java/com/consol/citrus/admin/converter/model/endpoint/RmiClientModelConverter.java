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

import com.consol.citrus.model.config.rmi.RmiClientModel;
import com.consol.citrus.rmi.client.RmiClient;
import com.consol.citrus.rmi.endpoint.RmiEndpointConfiguration;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component
public class RmiClientModelConverter extends AbstractEndpointModelConverter<RmiClientModel, RmiClient, RmiEndpointConfiguration> {

    /**
     * Default constructor.
     */
    public RmiClientModelConverter() {
        super(RmiClientModel.class, RmiClient.class, RmiEndpointConfiguration.class);
    }

    @Override
    public RmiClientModel convert(String id, RmiClient model) {
        RmiClientModel converted = convert(model);
        converted.setId(id);
        return converted;
    }

    @Override
    public String getJavaConfig(RmiClientModel model) {
        return getJavaConfig(model, model.getId(), "rmi().client()");
    }
}
