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
import com.consol.citrus.model.config.camel.CamelEndpointDefinition;
import com.consol.citrus.model.config.core.ChannelEndpointDefinition;
import com.consol.citrus.model.config.ftp.FtpClientDefinition;
import com.consol.citrus.model.config.ftp.FtpServerDefinition;
import com.consol.citrus.model.config.http.HttpClientDefinition;
import com.consol.citrus.model.config.http.HttpServerDefinition;
import com.consol.citrus.model.config.jms.JmsEndpointDefinition;
import com.consol.citrus.model.config.jmx.JmxClientDefinition;
import com.consol.citrus.model.config.jmx.JmxServerDefinition;
import com.consol.citrus.model.config.mail.MailClientDefinition;
import com.consol.citrus.model.config.mail.MailServerDefinition;
import com.consol.citrus.model.config.rmi.RmiClientDefinition;
import com.consol.citrus.model.config.rmi.RmiServerDefinition;
import com.consol.citrus.model.config.ssh.SshClientDefinition;
import com.consol.citrus.model.config.ssh.SshServerDefinition;
import com.consol.citrus.model.config.vertx.VertxEndpointDefinition;
import com.consol.citrus.model.config.websocket.WebSocketClientDefinition;
import com.consol.citrus.model.config.websocket.WebSocketServerDefinition;
import com.consol.citrus.model.config.ws.WebServiceClientDefinition;
import com.consol.citrus.model.config.ws.WebServiceServerDefinition;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author Christoph Deppisch
 */
public class EndpointConverterTest {

    @Test(dataProvider = "converterData")
    public void testConvert(EndpointConverter endpointConverter, Object definition, String endpointType) throws Exception {
        EndpointDefinition result = endpointConverter.convert(definition);
        Assert.assertEquals(result.getType(), endpointType);

        Object roundTrip = endpointConverter.convertBack(result);
        Assert.assertEquals(roundTrip.getClass(), definition.getClass());
    }

    @DataProvider
    public Object[][] converterData() {
        return new Object[][] {
            new Object[] {new JmsEndpointConverter(), new JmsEndpointDefinition(), "jms"},
            new Object[] {new ChannelEndpointConverter(), new ChannelEndpointDefinition(), "channel"},
            new Object[] {new CamelEndpointConverter(), new CamelEndpointDefinition(), "camel"},
            new Object[] {new VertxEndpointConverter(), new VertxEndpointDefinition(), "vertx"},
            new Object[] {new HttpClientConverter(), new HttpClientDefinition(), "http-client"},
            new Object[] {new HttpServerConverter(), new HttpServerDefinition(), "http-server"},
            new Object[] {new WebServiceClientConverter(), new WebServiceClientDefinition(), "ws-client"},
            new Object[] {new WebServiceServerConverter(), new WebServiceServerDefinition(), "ws-server"},
            new Object[] {new WebSocketClientConverter(), new WebSocketClientDefinition(), "websocket-client"},
            new Object[] {new WebSocketServerConverter(), new WebSocketServerDefinition(), "websocket-server"},
            new Object[] {new FtpClientConverter(), new FtpClientDefinition(), "ftp-client"},
            new Object[] {new FtpServerConverter(), new FtpServerDefinition(), "ftp-server"},
            new Object[] {new SshClientConverter(), new SshClientDefinition(), "ssh-client"},
            new Object[] {new SshServerConverter(), new SshServerDefinition(), "ssh-server"},
            new Object[] {new RmiClientConverter(), new RmiClientDefinition(), "rmi-client"},
            new Object[] {new RmiServerConverter(), new RmiServerDefinition(), "rmi-server"},
            new Object[] {new JmxClientConverter(), new JmxClientDefinition(), "jmx-client"},
            new Object[] {new JmxServerConverter(), new JmxServerDefinition(), "jmx-server"},
            new Object[] {new MailClientConverter(), new MailClientDefinition(), "mail-client"},
            new Object[] {new MailServerConverter(), new MailServerDefinition(), "mail-server"}
        };
    }
}
