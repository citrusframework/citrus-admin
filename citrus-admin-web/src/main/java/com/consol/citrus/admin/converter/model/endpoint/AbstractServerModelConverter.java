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
import com.consol.citrus.endpoint.EndpointConfiguration;
import com.consol.citrus.server.Server;

/**
 * @author Christoph Deppisch
 * @since 2.7
 */
public abstract class AbstractServerModelConverter<T, S extends Server> extends AbstractModelConverter<T, S> {

    private final AbstractEndpointModelConverter<T, S, EndpointConfiguration> delegate;

    /**
     * Default constructor using source and target model types.
     *
     * @param targetModelType
     * @param sourceModelType
     */
    protected AbstractServerModelConverter(Class<T> targetModelType, Class<S> sourceModelType) {
        super(targetModelType, sourceModelType);

        this.delegate = new AbstractEndpointModelConverter<T, S, EndpointConfiguration>(targetModelType, sourceModelType, EndpointConfiguration.class) {
            @Override
            public String getJavaConfig(T model) {
                return getJavaConfig(model, getId(model), getEndpointType());
            }
        };

        delegate.addDecorator(new AbstractModelConverter.MethodCallDecorator("port") {
            @Override
            public Object decorateArgument(Object arg) {
                return Long.valueOf(arg.toString());
            }
        });
    }

    protected abstract String getEndpointType();

    protected abstract String getId(T model);

    @Override
    public String getJavaConfig(T model) {
        return delegate.getJavaConfig(model);
    }
}
