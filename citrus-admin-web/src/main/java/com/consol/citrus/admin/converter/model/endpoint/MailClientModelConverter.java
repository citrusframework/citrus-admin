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
import com.consol.citrus.mail.client.MailClient;
import com.consol.citrus.mail.client.MailEndpointConfiguration;
import com.consol.citrus.model.config.mail.MailClientModel;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component
public class MailClientModelConverter extends AbstractEndpointModelConverter<MailClientModel, MailClient, MailEndpointConfiguration> {

    /**
     * Default constructor.
     */
    public MailClientModelConverter() {
        super(MailClientModel.class, MailClient.class, MailEndpointConfiguration.class);

        addDecorator(new AbstractModelConverter.MethodCallDecorator("port") {
            @Override
            public Object decorateArgument(Object arg) {
                return Integer.valueOf(arg.toString());
            }
        });
    }

    @Override
    public MailClientModel convert(String id, MailClient model) {
        MailClientModel converted = convert(model);
        converted.setId(id);
        return converted;
    }

    @Override
    public String getJavaConfig(MailClientModel model) {
        return getJavaConfig(model, model.getId(), "mail().client()");
    }
}
