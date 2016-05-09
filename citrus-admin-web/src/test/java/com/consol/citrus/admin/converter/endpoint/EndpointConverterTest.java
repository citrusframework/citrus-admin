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

import com.consol.citrus.admin.model.EndpointModel;
import com.consol.citrus.model.config.camel.CamelEndpointModel;
import com.consol.citrus.model.config.core.ChannelEndpointModel;
import com.consol.citrus.model.config.ftp.FtpClientModel;
import com.consol.citrus.model.config.ftp.FtpServerModel;
import com.consol.citrus.model.config.http.HttpClientModel;
import com.consol.citrus.model.config.http.HttpServerModel;
import com.consol.citrus.model.config.jms.JmsEndpointModel;
import com.consol.citrus.model.config.jmx.JmxClientModel;
import com.consol.citrus.model.config.jmx.JmxServerModel;
import com.consol.citrus.model.config.mail.MailClientModel;
import com.consol.citrus.model.config.mail.MailServerModel;
import com.consol.citrus.model.config.rmi.RmiClientModel;
import com.consol.citrus.model.config.rmi.RmiServerModel;
import com.consol.citrus.model.config.ssh.SshClientModel;
import com.consol.citrus.model.config.ssh.SshServerModel;
import com.consol.citrus.model.config.vertx.VertxEndpointModel;
import com.consol.citrus.model.config.websocket.WebSocketClientModel;
import com.consol.citrus.model.config.websocket.WebSocketServerModel;
import com.consol.citrus.model.config.ws.WebServiceClientModel;
import com.consol.citrus.model.config.ws.WebServiceServerModel;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author Christoph Deppisch
 */
public class EndpointConverterTest {

    @Test(dataProvider = "converterData")
    public void testConvert(EndpointConverter endpointConverter, Object definition, String endpointType) throws Exception {
        EndpointModel result = endpointConverter.convert(definition);
        Assert.assertEquals(result.getType(), endpointType);

        Object roundTrip = endpointConverter.convertBack(result);
        Assert.assertEquals(roundTrip.getClass(), definition.getClass());
    }

    @DataProvider
    public Object[][] converterData() {
        return new Object[][] {
            new Object[] {new JmsEndpointConverter(), new JmsEndpointModel(), "jms"},
            new Object[] {new ChannelEndpointConverter(), new ChannelEndpointModel(), "channel"},
            new Object[] {new CamelEndpointConverter(), new CamelEndpointModel(), "camel"},
            new Object[] {new VertxEndpointConverter(), new VertxEndpointModel(), "vertx"},
            new Object[] {new HttpClientConverter(), new HttpClientModel(), "http-client"},
            new Object[] {new HttpServerConverter(), new HttpServerModel(), "http-server"},
            new Object[] {new WebServiceClientConverter(), new WebServiceClientModel(), "ws-client"},
            new Object[] {new WebServiceServerConverter(), new WebServiceServerModel(), "ws-server"},
            new Object[] {new WebSocketClientConverter(), new WebSocketClientModel(), "websocket-client"},
            new Object[] {new WebSocketServerConverter(), new WebSocketServerModel(), "websocket-server"},
            new Object[] {new FtpClientConverter(), new FtpClientModel(), "ftp-client"},
            new Object[] {new FtpServerConverter(), new FtpServerModel(), "ftp-server"},
            new Object[] {new SshClientConverter(), new SshClientModel(), "ssh-client"},
            new Object[] {new SshServerConverter(), new SshServerModel(), "ssh-server"},
            new Object[] {new RmiClientConverter(), new RmiClientModel(), "rmi-client"},
            new Object[] {new RmiServerConverter(), new RmiServerModel(), "rmi-server"},
            new Object[] {new JmxClientConverter(), new JmxClientModel(), "jmx-client"},
            new Object[] {new JmxServerConverter(), new JmxServerModel(), "jmx-server"},
            new Object[] {new MailClientConverter(), new MailClientModel(), "mail-client"},
            new Object[] {new MailServerConverter(), new MailServerModel(), "mail-server"}
        };
    }
}
