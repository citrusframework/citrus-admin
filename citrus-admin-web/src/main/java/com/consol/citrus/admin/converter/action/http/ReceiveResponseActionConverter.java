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

package com.consol.citrus.admin.converter.action.http;

import com.consol.citrus.actions.ReceiveMessageAction;
import com.consol.citrus.admin.converter.action.AbstractTestActionConverter;
import com.consol.citrus.admin.model.Property;
import com.consol.citrus.admin.model.TestActionModel;
import com.consol.citrus.config.xml.PayloadElementParser;
import com.consol.citrus.model.testcase.http.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Christoph Deppisch
 */
@Component
public class ReceiveResponseActionConverter extends AbstractTestActionConverter<ReceiveResponseModel, ReceiveMessageAction> {

    /**
     * Default constructor using action type reference.
     */
    public ReceiveResponseActionConverter() {
        super("http-client:receive");
    }

    @Override
    public TestActionModel convert(ReceiveResponseModel model) {
        TestActionModel action = super.convert(model);

        ResponseHeadersType headers = model.getHeaders();
        if (headers != null) {
            action.add(new Property<>("status", "status", "Status", model.getHeaders().getStatus(), true)
                        .options(Stream.of(HttpStatus.values()).map(HttpStatus::toString).collect(Collectors.toList())));
            action.add(new Property<>("reason", "reason", "Reason", model.getHeaders().getReasonPhrase(), false)
                        .options(Stream.of(HttpStatus.values()).map(HttpStatus::getReasonPhrase).collect(Collectors.toList())));

            action.add(new Property<>("version", "version", "Version", Optional.ofNullable(model.getHeaders().getVersion()).orElse("HTTP/1.1"), false));
        }

        return action;
    }

    @Override
    protected Map<String, String> getFieldNameMappings() {
        Map<String, String> mappings = super.getFieldNameMappings();

        mappings.put("client", "endpoint");
        mappings.put("uri", "endpointUri");

        return mappings;
    }

    @Override
    protected <V> V resolvePropertyExpression(V value) {
        if (value instanceof ReceiveResponseModel.Body) {
            ReceiveResponseModel.Body body = (ReceiveResponseModel.Body) value;

            if (body != null) {
                if (StringUtils.hasText(body.getData())) {
                    return (V) body.getData().trim();
                } else if (body.getPayload() != null) {
                    return (V) PayloadElementParser.parseMessagePayload(body.getPayload().getAnies().get(0));
                } else if (body.getResource() != null &&
                        StringUtils.hasText(body.getResource().getFile())) {
                    return (V) body.getResource().getFile();
                } else {
                    return null;
                }
            }
        }

        if (value instanceof ResponseHeadersType) {
            return (V) ((ResponseHeadersType) value).getHeaders().stream().map(header -> header.getName() + "=" + header.getValue()).collect(Collectors.joining(","));
        }

        return super.resolvePropertyExpression(value);
    }

    @Override
    public ReceiveResponseModel convertModel(ReceiveMessageAction model) {
        ReceiveResponseModel action = new ObjectFactory().createReceiveResponseModel();

        if (model.getActor() != null) {
            action.setActor(model.getActor().getName());
        } else if (model.getEndpoint() != null && model.getEndpoint().getActor() != null) {
            action.setActor(model.getEndpoint().getActor().getName());
        }

        action.setClient(model.getEndpoint() != null ? model.getEndpoint().getName() : model.getEndpointUri());
        ClientRequestType request = new ClientRequestType();
        request.setDescription(model.getDescription());

        return action;
    }

    @Override
    public Class<ReceiveResponseModel> getSourceModelClass() {
        return ReceiveResponseModel.class;
    }

    @Override
    public Class<ReceiveMessageAction> getActionModelClass() {
        return ReceiveMessageAction.class;
    }
}
