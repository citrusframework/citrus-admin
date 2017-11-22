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

import com.consol.citrus.model.config.websocket.WebSocketServerModel;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Christoph Deppisch
 */
@Component
public class WebSocketServerConverter extends AbstractEndpointConverter<WebSocketServerModel> {

    @Override
    protected String getId(WebSocketServerModel model) {
        return model.getId();
    }

    @Override
    protected String[] getRequiredFields() {
        return new String[] { "port" };
    }

    @Override
    protected Map<String, Object> getDefaultValueMappings() {
        Map<String, Object> mappings = super.getDefaultValueMappings();
        mappings.put("rootParentContext", TRUE);
        return mappings;
    }

    @Override
    protected Map<String, Class<?>> getOptionTypeMappings() {
        Map<String, Class<?>> mappings = super.getOptionTypeMappings();
        mappings.put("securityHandler", SecurityHandler.class);
        mappings.put("servletHandler", ServletHandler.class);
        mappings.put("connector", Connector.class);
        return mappings;
    }

    @Override
    public Class<WebSocketServerModel> getSourceModelClass() {
        return WebSocketServerModel.class;
    }

    @Override
    public String getEndpointType() {
        return "websocket-server";
    }
}
