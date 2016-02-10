/*
 * Copyright 2006-2016 the original author or authors.
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

import com.consol.citrus.admin.model.EndpointDefinition;
import com.consol.citrus.admin.model.Property;
import com.consol.citrus.model.config.jms.JmsEndpointDefinition;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author Christoph Deppisch
 */
public class JmsEndpointConverterTest {

    @Test
    public void testConvert() throws Exception {
        JmsEndpointConverter endpointConverter = new JmsEndpointConverter();
        EndpointDefinition result = endpointConverter.convert(new JmsEndpointDefinition());
        Assert.assertEquals(result.getType(), "jms");
    }

    @Test
    public void testConvertBack() throws Exception {
        JmsEndpointConverter endpointConverter = new JmsEndpointConverter();
        EndpointDefinition definition = new EndpointDefinition();
        definition.setId("newEndpoint");
        definition.setType(endpointConverter.getEndpointType());
        definition.setModelType(endpointConverter.getModelClass().getName());

        definition.add(new Property("destinationName", "destinationName", "Destination", "JMS.Queue"));
        definition.add(new Property("pubSubDomain", "pubSubDomain", "PubSubDomain", "TRUE"));

        JmsEndpointDefinition result = endpointConverter.convertBack(definition);
        Assert.assertEquals(result.getId(), "newEndpoint");
        Assert.assertEquals(result.getDestinationName(), "JMS.Queue");
        Assert.assertTrue(result.isPubSubDomain());
    }
}