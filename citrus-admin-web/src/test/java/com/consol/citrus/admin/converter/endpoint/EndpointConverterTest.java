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
import com.consol.citrus.admin.model.Property;
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

import javax.jms.ConnectionFactory;

/**
 * @author Christoph Deppisch
 */
public class EndpointConverterTest {

    @Test(dataProvider = "converterData")
    public void testConvert(EndpointConverter endpointConverter, ModelAndAssertion modelAndAssertion) throws Exception {
        EndpointModel result = endpointConverter.convert(modelAndAssertion.getModel());

        modelAndAssertion.assertModel(result);

        Object roundTrip = endpointConverter.convertBack(result);
        Assert.assertEquals(roundTrip.getClass(), modelAndAssertion.getModel().getClass());

        modelAndAssertion.assertModel(roundTrip);
    }

    @DataProvider
    public Object[][] converterData() {
        return new Object[][] {
            new Object[] {new JmsEndpointConverter(), new ModelAndAssertion<JmsEndpointModel>() {
                @Override
                public JmsEndpointModel getModel() {
                    JmsEndpointModel model = new JmsEndpointModel();
                    model.setDestinationName("jms.foo.queue");
                    model.setConnectionFactory("myConnectionFactory");
                    return model;
                }

                @Override
                public void assertModel(EndpointModel model) {
                    Assert.assertEquals(model.getType(), "jms");
                    Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("timeout")).findFirst().orElse(new Property()).getValue(), "5000");
                    Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("destination-name")).findFirst().orElse(new Property()).getValue(), "jms.foo.queue");
                    Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("destination-name")).findFirst().orElse(new Property()).getDisplayName(), "DestinationName");
                    Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("connection-factory")).findFirst().orElse(new Property()).getValue(), "myConnectionFactory");
                    Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("connection-factory")).findFirst().orElse(new Property()).getOptionType(), ConnectionFactory.class);
                }

                @Override
                public void assertModel(JmsEndpointModel model) {
                    Assert.assertEquals(model.getTimeout(), "5000");
                    Assert.assertEquals(model.getDestinationName(), "jms.foo.queue");
                    Assert.assertEquals(model.getConnectionFactory(), "myConnectionFactory");
                }
            }},
            new Object[] {new ChannelEndpointConverter(), new ModelAndAssertion<ChannelEndpointModel>() {
                @Override
                public ChannelEndpointModel getModel() {
                    ChannelEndpointModel model = new ChannelEndpointModel();
                    model.setChannelName("fooChannel");
                    return model;
                }

                @Override
                public void assertModel(EndpointModel model) {
                    Assert.assertEquals(model.getType(), "channel");
                    Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("timeout")).findFirst().orElse(new Property()).getValue(), "5000");
                    Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("channel-name")).findFirst().orElse(new Property()).getValue(), "fooChannel");
                    Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("channel-name")).findFirst().orElse(new Property()).getDisplayName(), "ChannelName");
                }

                @Override
                public void assertModel(ChannelEndpointModel model) {
                    Assert.assertEquals(model.getTimeout(), "5000");
                    Assert.assertEquals(model.getChannelName(), "fooChannel");
                }
            }},
            new Object[] {new CamelEndpointConverter(), new ModelAndAssertion<CamelEndpointModel>() {
                @Override
                public CamelEndpointModel getModel() {
                    return new CamelEndpointModel();
                }

                @Override
                public void assertModel(EndpointModel model) {
                    Assert.assertEquals(model.getType(), "camel");
                    Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("timeout")).findFirst().orElse(new Property()).getValue(), "5000");
                }

                @Override
                public void assertModel(CamelEndpointModel model) {
                    Assert.assertEquals(model.getTimeout(), "5000");
                }
            }},
            new Object[] {new VertxEndpointConverter(), new ModelAndAssertion<VertxEndpointModel>() {
                @Override
                public VertxEndpointModel getModel() {
                    return new VertxEndpointModel();
                }

                @Override
                public void assertModel(EndpointModel model) {
                    Assert.assertEquals(model.getType(), "vertx");
                    Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("timeout")).findFirst().orElse(new Property()).getValue(), "5000");
                }

                @Override
                public void assertModel(VertxEndpointModel model) {
                    Assert.assertEquals(model.getTimeout(), "5000");
                }
            }},
            new Object[] {new HttpClientConverter(), new ModelAndAssertion<HttpClientModel>() {
                @Override
                public HttpClientModel getModel() {
                    HttpClientModel model = new HttpClientModel();
                    model.setRequestUrl("http://localhost:8080/test");
                    return model;
                }

                @Override
                public void assertModel(EndpointModel model) {
                    Assert.assertEquals(model.getType(), "http-client");
                    Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("timeout")).findFirst().orElse(new Property()).getValue(), "5000");
                    Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("request-url")).findFirst().orElse(new Property()).getValue(), "http://localhost:8080/test");
                }

                @Override
                public void assertModel(HttpClientModel model) {
                    Assert.assertEquals(model.getTimeout(), "5000");
                    Assert.assertEquals(model.getRequestUrl(), "http://localhost:8080/test");
                }
            }},
            new Object[] {new HttpServerConverter(), new ModelAndAssertion<HttpServerModel>() {
                @Override
                public HttpServerModel getModel() {
                    HttpServerModel model = new HttpServerModel();
                    model.setPort("8080");
                    return model;
                }

                @Override
                public void assertModel(EndpointModel model) {
                    Assert.assertEquals(model.getType(), "http-server");
                    Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("timeout")).findFirst().orElse(new Property()).getValue(), "5000");
                    Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("port")).findFirst().orElse(new Property()).getValue(), "8080");
                    Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("auto-start")).findFirst().orElse(new Property()).getValue(), "true");
                }

                @Override
                public void assertModel(HttpServerModel model) {
                    Assert.assertEquals(model.getTimeout(), "5000");
                    Assert.assertEquals(model.getPort(), "8080");
                    Assert.assertTrue(model.isAutoStart());
                }
            }},
            new Object[] {new WebServiceClientConverter(), new ModelAndAssertion<WebServiceClientModel>() {
                @Override
                public WebServiceClientModel getModel() {
                    WebServiceClientModel model = new WebServiceClientModel();
                    model.setRequestUrl("http://localhost:8080/services/ws");
                    return model;
                }

                @Override
                public void assertModel(EndpointModel model) {
                    Assert.assertEquals(model.getType(), "ws-client");
                    Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("timeout")).findFirst().orElse(new Property()).getValue(), "5000");
                    Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("request-url")).findFirst().orElse(new Property()).getValue(), "http://localhost:8080/services/ws");
                }

                @Override
                public void assertModel(WebServiceClientModel model) {
                    Assert.assertEquals(model.getTimeout(), "5000");
                    Assert.assertEquals(model.getRequestUrl(), "http://localhost:8080/services/ws");
                }
            }},
            new Object[] {new WebServiceServerConverter(), new ModelAndAssertion<WebServiceServerModel>() {
                @Override
                public WebServiceServerModel getModel() {
                    WebServiceServerModel model = new WebServiceServerModel();
                    model.setPort("8080");
                    return model;
                }

                @Override
                public void assertModel(EndpointModel model) {
                    Assert.assertEquals(model.getType(), "ws-server");
                    Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("timeout")).findFirst().orElse(new Property()).getValue(), "5000");
                    Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("port")).findFirst().orElse(new Property()).getValue(), "8080");
                    Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("auto-start")).findFirst().orElse(new Property()).getValue(), "true");
                }

                @Override
                public void assertModel(WebServiceServerModel model) {
                    Assert.assertEquals(model.getTimeout(), "5000");
                    Assert.assertEquals(model.getPort(), "8080");
                    Assert.assertTrue(model.isAutoStart());
                }
            }},
            new Object[] {new WebSocketClientConverter(), new ModelAndAssertion<WebSocketClientModel>() {
                @Override
                public WebSocketClientModel getModel() {
                    return new WebSocketClientModel();
                }

                @Override
                public void assertModel(EndpointModel model) {
                    Assert.assertEquals(model.getType(), "websocket-client");
                    Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("timeout")).findFirst().orElse(new Property()).getValue(), "5000");
                }

                @Override
                public void assertModel(WebSocketClientModel model) {
                    Assert.assertEquals(model.getTimeout(), "5000");
                }
            }},
            new Object[] {new WebSocketServerConverter(), new ModelAndAssertion<WebSocketServerModel>() {
                @Override
                public WebSocketServerModel getModel() {
                    return new WebSocketServerModel();
                }

                @Override
                public void assertModel(EndpointModel model) {
                    Assert.assertEquals(model.getType(), "websocket-server");
                    Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("timeout")).findFirst().orElse(new Property()).getValue(), "5000");
                }

                @Override
                public void assertModel(WebSocketServerModel model) {
                    Assert.assertEquals(model.getTimeout(), "5000");
                }
            }},
            new Object[] {new FtpClientConverter(), new ModelAndAssertion<FtpClientModel>() {
                @Override
                public FtpClientModel getModel() {
                    return new FtpClientModel();
                }

                @Override
                public void assertModel(EndpointModel model) {
                    Assert.assertEquals(model.getType(), "ftp-client");
                    Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("timeout")).findFirst().orElse(new Property()).getValue(), "5000");
                }

                @Override
                public void assertModel(FtpClientModel model) {
                    Assert.assertEquals(model.getTimeout(), "5000");
                }
            }},
            new Object[] {new FtpServerConverter(), new ModelAndAssertion<FtpServerModel>() {
                @Override
                public FtpServerModel getModel() {
                    return new FtpServerModel();
                }

                @Override
                public void assertModel(EndpointModel model) {
                    Assert.assertEquals(model.getType(), "ftp-server");
                    Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("timeout")).findFirst().orElse(new Property()).getValue(), "5000");
                }

                @Override
                public void assertModel(FtpServerModel model) {
                    Assert.assertEquals(model.getTimeout(), "5000");
                }
            }},
            new Object[] {new SshClientConverter(), new ModelAndAssertion<SshClientModel>() {
                @Override
                public SshClientModel getModel() {
                    return new SshClientModel();
                }

                @Override
                public void assertModel(EndpointModel model) {
                    Assert.assertEquals(model.getType(), "ssh-client");
                    Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("timeout")).findFirst().orElse(new Property()).getValue(), "5000");
                }

                @Override
                public void assertModel(SshClientModel model) {
                    Assert.assertEquals(model.getTimeout(), "5000");
                }
            }},
            new Object[] {new SshServerConverter(), new ModelAndAssertion<SshServerModel>() {
                @Override
                public SshServerModel getModel() {
                    return new SshServerModel();
                }

                @Override
                public void assertModel(EndpointModel model) {
                    Assert.assertEquals(model.getType(), "ssh-server");
                    Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("port")).findFirst().orElse(new Property()).getValue(), "22");
                }

                @Override
                public void assertModel(SshServerModel model) {
                    Assert.assertEquals(model.getPort(), "22");
                }
            }},
            new Object[] {new RmiClientConverter(), new ModelAndAssertion<RmiClientModel>() {
                @Override
                public RmiClientModel getModel() {
                    return new RmiClientModel();
                }

                @Override
                public void assertModel(EndpointModel model) {
                    Assert.assertEquals(model.getType(), "rmi-client");
                    Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("timeout")).findFirst().orElse(new Property()).getValue(), "5000");
                }

                @Override
                public void assertModel(RmiClientModel model) {
                    Assert.assertEquals(model.getTimeout(), "5000");
                }
            }},
            new Object[] {new RmiServerConverter(), new ModelAndAssertion<RmiServerModel>() {
                @Override
                public RmiServerModel getModel() {
                    return new RmiServerModel();
                }

                @Override
                public void assertModel(EndpointModel model) {
                    Assert.assertEquals(model.getType(), "rmi-server");
                    Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("timeout")).findFirst().orElse(new Property()).getValue(), "5000");
                }

                @Override
                public void assertModel(RmiServerModel model) {
                    Assert.assertEquals(model.getTimeout(), "5000");
                }
            }},
            new Object[] {new JmxClientConverter(), new ModelAndAssertion<JmxClientModel>() {
                @Override
                public JmxClientModel getModel() {
                    return new JmxClientModel();
                }

                @Override
                public void assertModel(EndpointModel model) {
                    Assert.assertEquals(model.getType(), "jmx-client");
                    Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("timeout")).findFirst().orElse(new Property()).getValue(), "5000");
                }

                @Override
                public void assertModel(JmxClientModel model) {
                    Assert.assertEquals(model.getTimeout(), "5000");
                }
            }},
            new Object[] {new JmxServerConverter(), new ModelAndAssertion<JmxServerModel>() {
                @Override
                public JmxServerModel getModel() {
                    return new JmxServerModel();
                }

                @Override
                public void assertModel(EndpointModel model) {
                    Assert.assertEquals(model.getType(), "jmx-server");
                    Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("timeout")).findFirst().orElse(new Property()).getValue(), "5000");
                }

                @Override
                public void assertModel(JmxServerModel model) {
                    Assert.assertEquals(model.getTimeout(), "5000");
                }
            }},
            new Object[] {new MailClientConverter(), new ModelAndAssertion<MailClientModel>() {
                @Override
                public MailClientModel getModel() {
                    return new MailClientModel();
                }

                @Override
                public void assertModel(EndpointModel model) {
                    Assert.assertEquals(model.getType(), "mail-client");
                    Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("port")).findFirst().orElse(new Property()).getValue(), "2222");
                }

                @Override
                public void assertModel(MailClientModel model) {
                    Assert.assertEquals(model.getPort(), "2222");
                }
            }},
            new Object[] {new MailServerConverter(), new ModelAndAssertion<MailServerModel>() {
                @Override
                public MailServerModel getModel() {
                    MailServerModel model = new MailServerModel();
                    model.setPort("2222");
                    return model;
                }

                @Override
                public void assertModel(EndpointModel model) {
                    Assert.assertEquals(model.getType(), "mail-server");
                    Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("timeout")).findFirst().orElse(new Property()).getValue(), "5000");
                    Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("port")).findFirst().orElse(new Property()).getValue(), "2222");
                    Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("auto-start")).findFirst().orElse(new Property()).getValue(), "true");
                    Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("auto-accept")).findFirst().orElse(new Property()).getValue(), "true");
                }

                @Override
                public void assertModel(MailServerModel model) {
                    Assert.assertEquals(model.getTimeout(), "5000");
                    Assert.assertEquals(model.getPort(), "2222");
                    Assert.assertTrue(model.isAutoAccept());
                    Assert.assertTrue(model.isAutoStart());
                }
            }}
        };
    }

    /**
     * Model assertion interface.
     */
    interface ModelAndAssertion<T> {

        void assertModel(EndpointModel model);

        void assertModel(T model);

        T getModel();
    }
}
