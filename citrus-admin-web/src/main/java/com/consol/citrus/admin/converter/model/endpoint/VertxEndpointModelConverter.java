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

import com.consol.citrus.admin.converter.model.AbstractModelConverter;
import com.consol.citrus.model.config.vertx.VertxEndpointModel;
import com.consol.citrus.vertx.endpoint.VertxEndpoint;
import com.consol.citrus.vertx.endpoint.VertxEndpointConfiguration;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component
public class VertxEndpointModelConverter extends AbstractEndpointModelConverter<VertxEndpointModel, VertxEndpoint, VertxEndpointConfiguration> {

    /**
     * Default constructor.
     */
    public VertxEndpointModelConverter() {
        super(VertxEndpointModel.class, VertxEndpoint.class, VertxEndpointConfiguration.class);

        addDecorator(new AbstractModelConverter.MethodCallDecorator("port") {
            @Override
            public Object decorateArgument(Object arg) {
                return Integer.valueOf(arg.toString());
            }
        });
    }

    @Override
    public VertxEndpointModel convert(String id, VertxEndpoint model) {
        VertxEndpointModel converted = convert(model);
        converted.setId(id);
        return converted;
    }

    @Override
    public String getJavaConfig(VertxEndpointModel model) {
        return getJavaConfig(model, model.getId(), "vertx()");
    }
}
