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

package com.consol.citrus.admin.converter.model;

import com.consol.citrus.admin.converter.model.endpoint.*;
import com.consol.citrus.camel.endpoint.CamelEndpoint;
import com.consol.citrus.channel.ChannelEndpoint;
import com.consol.citrus.endpoint.Endpoint;
import com.consol.citrus.ftp.client.FtpClient;
import com.consol.citrus.ftp.server.FtpServer;
import com.consol.citrus.http.client.HttpClient;
import com.consol.citrus.http.server.HttpServer;
import com.consol.citrus.jms.endpoint.JmsEndpoint;
import com.consol.citrus.jmx.client.JmxClient;
import com.consol.citrus.jmx.server.JmxServer;
import com.consol.citrus.mail.client.MailClient;
import com.consol.citrus.mail.server.MailServer;
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
import com.consol.citrus.rmi.client.RmiClient;
import com.consol.citrus.rmi.server.RmiServer;
import com.consol.citrus.ssh.client.SshClient;
import com.consol.citrus.ssh.server.SshServer;
import com.consol.citrus.vertx.endpoint.VertxEndpoint;
import com.consol.citrus.websocket.server.WebSocketServer;
import com.consol.citrus.ws.client.WebServiceClient;
import com.consol.citrus.ws.server.WebServiceServer;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author Christoph Deppisch
 */
public class ModelConverterTest {

    @Test(dataProvider = "converterData")
    public void testConvert(ModelConverter modelConverter, Object target, Endpoint source, String endpointType) throws Exception {
        Object result = modelConverter.convert(source);
        Assert.assertEquals(result.getClass(), target.getClass());

        String snippet = modelConverter.getJavaConfig(target);
        Assert.assertTrue(snippet.contains(endpointType), snippet);
    }

    @DataProvider
    public Object[][] converterData() {
        return new Object[][] {
            new Object[] {new JmsEndpointModelConverter(), new JmsEndpointModel(), new JmsEndpoint(), "jms().asynchronous()"},
            new Object[] {new ChannelEndpointModelConverter(), new ChannelEndpointModel(), new ChannelEndpoint(), "channel()"},
            new Object[] {new CamelEndpointModelConverter(), new CamelEndpointModel(), new CamelEndpoint(), "camel()"},
            new Object[] {new VertxEndpointModelConverter(), new VertxEndpointModel(), new VertxEndpoint(), "vertx()"},
            new Object[] {new HttpClientModelConverter(), new HttpClientModel(), new HttpClient(), "http().client()"},
            new Object[] {new HttpServerModelConverter(), new HttpServerModel(), new HttpServer(), "http().server()"},
            new Object[] {new WebServiceClientModelConverter(), new WebServiceClientModel(), new WebServiceClient(), "soap().client()"},
            new Object[] {new WebServiceServerModelConverter(), new WebServiceServerModel(), new WebServiceServer(), "soap().server()"},
            new Object[] {new WebSocketClientModelConverter(), new WebSocketClientModel(), new WebServiceClient(), "websocket().client()"},
            new Object[] {new WebSocketServerModelConverter(), new WebSocketServerModel(), new WebSocketServer(), "websocket().server()"},
            new Object[] {new FtpClientModelConverter(), new FtpClientModel(), new FtpClient(), "ftp().client()"},
            new Object[] {new FtpServerModelConverter(), new FtpServerModel(), new FtpServer(), "ftp().server()"},
            new Object[] {new SshClientModelConverter(), new SshClientModel(), new SshClient(), "ssh().client()"},
            new Object[] {new SshServerModelConverter(), new SshServerModel(), new SshServer(), "ssh().server()"},
            new Object[] {new RmiClientModelConverter(), new RmiClientModel(), new RmiClient(), "rmi().client()"},
            new Object[] {new RmiServerModelConverter(), new RmiServerModel(), new RmiServer(), "rmi().server()"},
            new Object[] {new JmxClientModelConverter(), new JmxClientModel(), new JmxClient(), "jmx().client()"},
            new Object[] {new JmxServerModelConverter(), new JmxServerModel(), new JmxServer(), "jmx().server()"},
            new Object[] {new MailClientModelConverter(), new MailClientModel(), new MailClient(), "mail().client()"},
            new Object[] {new MailServerModelConverter(), new MailServerModel(), new MailServer(), "mail().server()"}
        };
    }
}
