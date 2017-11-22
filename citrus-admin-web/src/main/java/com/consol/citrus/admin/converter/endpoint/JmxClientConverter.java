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

import com.consol.citrus.model.config.jmx.JmxClientModel;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author Christoph Deppisch
 */
@Component
public class JmxClientConverter extends AbstractEndpointConverter<JmxClientModel> {

    @Override
    protected String getId(JmxClientModel model) {
        return model.getId();
    }

    @Override
    protected Map<String, Object> getDefaultValueMappings() {
        Map<String, Object> mappings = super.getDefaultValueMappings();
        mappings.put("serverUrl", "platform");
        return mappings;
    }

    @Override
    protected String[] getRequiredFields() {
        return new String[] { "serverUrl" };
    }

    @Override
    public Class<JmxClientModel> getSourceModelClass() {
        return JmxClientModel.class;
    }

    @Override
    public String getEndpointType() {
        return "jmx-client";
    }
}
