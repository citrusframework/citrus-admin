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
import com.consol.citrus.http.message.HttpMessageHeaders;
import com.consol.citrus.model.testcase.http.*;
import com.consol.citrus.validation.builder.PayloadTemplateMessageBuilder;
import com.consol.citrus.validation.builder.StaticMessageContentBuilder;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Christoph Deppisch
 */
@Component
public class ReceiveRequestActionConverter extends AbstractTestActionConverter<ReceiveRequestModel, ReceiveMessageAction> {

    /**
     * Default constructor using action type reference.
     */
    public ReceiveRequestActionConverter() {
        super("http-server:receive");
    }

    @Override
    public TestActionModel convert(ReceiveRequestModel model) {
        TestActionModel action = super.convert(model);

        action.add(new Property<>("method", "method", "Method", getRequestMethod(model), true)
                .options(Stream.of(RequestMethod.values()).map(RequestMethod::name).collect(Collectors.toList())));

        ServerRequestType request = getRequestType(model);
        if (request != null) {
            action.add(new Property<>("path", "path", "Path", request.getPath(), false));

            if (request.getBody() != null) {
                if (StringUtils.hasText(request.getBody().getData())) {
                    action.add(new Property<>("body", "body", "Body", request.getBody().getData().trim(), false));
                } else if (request.getBody().getPayload() != null) {
                    action.add(new Property<>("body", "body", "Body", PayloadElementParser.parseMessagePayload(request.getBody().getPayload().getAnies().get(0)), false));
                } else if (request.getBody().getResource() != null &&
                        StringUtils.hasText(request.getBody().getResource().getFile())) {
                    action.add(new Property<>("body", "body", "Body", request.getBody().getResource().getFile(), false));
                } else {
                    action.add(new Property<>("body", "body", "Body", null, false));
                }
            } else {
                action.add(new Property<>("body", "body", "Body", null, false));
            }

            if (request.getHeaders() != null) {
                action.add(new Property<>("headers", "headers", "Headers", request.getHeaders().getHeaders().stream().map(header -> header.getName() + "=" + header.getValue()).collect(Collectors.joining(",")), false));
            } else {
                action.add(new Property<>("headers", "headers", "Headers", null, false));
            }

            if (!CollectionUtils.isEmpty(request.getParams())) {
                action.add(new Property<>("query.params", "query.params", "QueryParams", request.getParams().stream().map(param -> param.getName() + "=" + param.getValue()).collect(Collectors.joining(",")), false));
            }
        }

        return action;
    }

    @Override
    protected boolean include(ReceiveRequestModel model, Field field) {
        return !field.getType().equals(ServerRequestType.class) && super.include(model, field);

    }

    @Override
    protected Map<String, String> getFieldNameMappings() {
        Map<String, String> mappings = super.getFieldNameMappings();
        mappings.put("server", "endpoint");
        return mappings;
    }

    @Override
    protected Map<String, Object> getDefaultValueMappings() {
        Map<String, Object> mappings = super.getDefaultValueMappings();
        mappings.put("fork", FALSE);
        return mappings;
    }

    @Override
    public ReceiveRequestModel convertModel(ReceiveMessageAction model) {
        ReceiveRequestModel action = new ObjectFactory().createReceiveRequestModel();

        if (model.getActor() != null) {
            action.setActor(model.getActor().getName());
        } else if (model.getEndpoint() != null && model.getEndpoint().getActor() != null) {
            action.setActor(model.getEndpoint().getActor().getName());
        }

        action.setServer(model.getEndpoint() != null ? model.getEndpoint().getName() : model.getEndpointUri());
        ServerRequestType request = new ServerRequestType();
        request.setDescription(model.getDescription());

        String method = RequestMethod.POST.name();
        if (model.getMessageBuilder() instanceof StaticMessageContentBuilder) {
            method = ((StaticMessageContentBuilder) model.getMessageBuilder()).getMessage().getHeader(HttpMessageHeaders.HTTP_REQUEST_METHOD).toString();
        } else if (model.getMessageBuilder() instanceof PayloadTemplateMessageBuilder) {
            if (((PayloadTemplateMessageBuilder) model.getMessageBuilder()).getMessageHeaders().containsKey(HttpMessageHeaders.HTTP_REQUEST_METHOD)) {
                method = ((PayloadTemplateMessageBuilder) model.getMessageBuilder()).getMessageHeaders().get(HttpMessageHeaders.HTTP_REQUEST_METHOD).toString();
            }
        }

        if (RequestMethod.GET.name().equals(method)) {
            action.setGET(request);
        } else if (RequestMethod.POST.name().equals(method)) {
            action.setPOST(request);
        } else if (RequestMethod.PUT.name().equals(method)) {
            action.setPUT(request);
        } else if (RequestMethod.DELETE.name().equals(method)) {
            action.setDELETE(request);
        } else if (RequestMethod.HEAD.name().equals(method)) {
            action.setHEAD(request);
        } else if (RequestMethod.OPTIONS.name().equals(method)) {
            action.setPOST(request);
        } else if (RequestMethod.PATCH.name().equals(method)) {
            action.setPATCH(request);
        } else if (RequestMethod.TRACE.name().equals(method)) {
            action.setTRACE(request);
        }

        return action;
    }

    @Override
    protected String getterMethodName(Field field, String fieldName) {
        if (field.getType().equals(ServerRequestType.class)) {
            return super.getterMethodName(field, fieldName.toUpperCase());
        }
        
        return super.getterMethodName(field, fieldName);
    }

    /**
     * Gets request type from model.
     * @param model
     * @return
     */
    private ServerRequestType getRequestType(ReceiveRequestModel model) {
        if (model.getGET() != null) {
            return model.getGET();
        } else if (model.getPOST() != null) {
            return model.getPOST();
        } else if (model.getPUT() != null) {
            return model.getPUT();
        } else if (model.getDELETE() != null) {
            return model.getDELETE();
        } else if (model.getHEAD() != null) {
            return model.getHEAD();
        } else if (model.getOPTIONS() != null) {
            return model.getOPTIONS();
        } else if (model.getPATCH() != null) {
            return model.getPATCH();
        } else if (model.getTRACE() != null) {
            return model.getTRACE();
        }

        return null;
    }

    /**
     * Gets request method from model request type.
     * @param model
     * @return
     */
    private String getRequestMethod(ReceiveRequestModel model) {
        if (model.getGET() != null) {
            return RequestMethod.GET.name();
        } else if (model.getPOST() != null) {
            return RequestMethod.POST.name();
        } else if (model.getPUT() != null) {
            return RequestMethod.PUT.name();
        } else if (model.getDELETE() != null) {
            return RequestMethod.DELETE.name();
        } else if (model.getHEAD() != null) {
            return RequestMethod.HEAD.name();
        } else if (model.getOPTIONS() != null) {
            return RequestMethod.OPTIONS.name();
        } else if (model.getPATCH() != null) {
            return RequestMethod.PATCH.name();
        } else if (model.getTRACE() != null) {
            return RequestMethod.TRACE.name();
        }

        return null;
    }

    @Override
    public Class<ReceiveRequestModel> getSourceModelClass() {
        return ReceiveRequestModel.class;
    }

    @Override
    public Class<ReceiveMessageAction> getActionModelClass() {
        return ReceiveMessageAction.class;
    }
}
