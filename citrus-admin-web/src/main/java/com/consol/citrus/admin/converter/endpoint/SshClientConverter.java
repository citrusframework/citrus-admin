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
import com.consol.citrus.message.MessageCorrelator;
import com.consol.citrus.model.config.ssh.SshClientModel;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component
public class SshClientConverter extends AbstractEndpointConverter<SshClientModel> {

    @Override
    public EndpointModel convert(SshClientModel model) {
        EndpointModel endpointModel = new EndpointModel(getEndpointType(), model.getId(), getSourceModelClass());

        endpointModel.add(property("host", model, "localhost", true));
        endpointModel.add(property("port", model, "2222", true));
        endpointModel.add(property("user", model));
        endpointModel.add(property("password", model));
        endpointModel.add(property("strictHostChecking", model, "false")
                .options("true", "false"));
        endpointModel.add(property("knownHostsPath", model));
        endpointModel.add(property("commandTimeout", model));
        endpointModel.add(property("connectionTimeout", model));
        endpointModel.add(property("messageCorrelator", model)
                .optionKey(MessageCorrelator.class.getName()));
        endpointModel.add(property("pollingInterval", model, "500"));

        addEndpointProperties(endpointModel, model);

        return endpointModel;
    }

    @Override
    public Class<SshClientModel> getSourceModelClass() {
        return SshClientModel.class;
    }

    @Override
    public String getEndpointType() {
        return "ssh-client";
    }
}
