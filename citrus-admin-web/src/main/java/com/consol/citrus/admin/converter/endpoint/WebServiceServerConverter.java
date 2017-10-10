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
import com.consol.citrus.model.config.ws.WebServiceServerModel;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component
public class WebServiceServerConverter extends AbstractEndpointConverter<WebServiceServerModel> {

    public static final String TRUE = "true";
    public static final String FALSE = "false";

    @Override
    public EndpointModel convert(WebServiceServerModel model) {
        EndpointModel endpointModel = new EndpointModel(getEndpointType(), model.getId(), getSourceModelClass());

        endpointModel.add(property("port", model, true));
        endpointModel.add(property("autoStart", model, TRUE)
                .options(TRUE, FALSE));
        endpointModel.add(property("resourceBase", model));
        endpointModel.add(property("contextPath", model));
        endpointModel.add(property("rootParentContext", model, TRUE)
                .options(TRUE, FALSE));
        endpointModel.add(property("handleMimeHeaders", model, TRUE)
                .options(TRUE, FALSE));
        endpointModel.add(property("messageConverter", model)
                .optionKey(MessageConverter.class.getName()));
        endpointModel.add(property("endpointAdapter", model));
        endpointModel.add(property("securityHandler", model)
                .optionKey(SecurityHandler.class.getName()));
        endpointModel.add(property("servletHandler", model)
                .optionKey(ServletHandler.class.getName()));
        endpointModel.add(property("connector", model)
                .optionKey(Connector.class.getName()));
        endpointModel.add(property("connectors", model));
        endpointModel.add(property("servletName", model));
        endpointModel.add(property("servletMappingPath", model));
        endpointModel.add(property("interceptors", model));
        endpointModel.add(property("soapHeaderNamespace", model));
        endpointModel.add(property("soapHeaderPrefix", model));

        endpointModel.add(property("timeout", model, "5000"));

        return endpointModel;
    }

    @Override
    public Class<WebServiceServerModel> getSourceModelClass() {
        return WebServiceServerModel.class;
    }

    @Override
    public String getEndpointType() {
        return "ws-server";
    }
}
