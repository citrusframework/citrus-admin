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

package com.consol.citrus.admin.converter.action;

import com.consol.citrus.TestAction;
import com.consol.citrus.actions.*;
import com.consol.citrus.admin.converter.action.http.*;
import com.consol.citrus.admin.converter.action.ws.AssertSoapFaultContainerConverter;
import com.consol.citrus.admin.model.Property;
import com.consol.citrus.admin.model.TestActionModel;
import com.consol.citrus.container.*;
import com.consol.citrus.model.testcase.core.*;
import com.consol.citrus.model.testcase.http.*;
import com.consol.citrus.model.testcase.ws.AssertFaultModel;
import com.consol.citrus.ws.actions.AssertSoapFault;
import org.springframework.http.HttpStatus;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author Christoph Deppisch
 */
public class TestActionConverterTest {

    @Test(dataProvider = "converterData")
    public void testConvert(TestActionConverter actionConverter, ModelAndAssertion modelAndAssertion) throws Exception {
        TestActionModel result = actionConverter.convert(modelAndAssertion.getModel());

        modelAndAssertion.assertModel(result);

        Object modelConversion = actionConverter.convertModel(modelAndAssertion.getAction());
        Assert.assertEquals(modelConversion.getClass(), modelAndAssertion.getModel().getClass());

        modelAndAssertion.assertModel(modelConversion);
    }

    @DataProvider
    public Object[][] converterData() {
        return new Object[][] {
                new Object[] {new SendMessageActionConverter(), new ModelAndAssertion<SendModel, SendMessageAction>() {
                    @Override
                    public SendModel getModel() {
                        SendModel model = new SendModel();
                        model.setEndpoint("myEndpoint");
                        return model;
                    }

                    @Override
                    public SendMessageAction getAction() {
                        return new SendMessageAction()
                                .setEndpointUri("myEndpoint");
                    }

                    @Override
                    public void assertModel(TestActionModel model) {
                        Assert.assertEquals(model.getType(), "send");
                        Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("endpoint")).findFirst().orElse(new Property()).getValue(), "myEndpoint");
                    }

                    @Override
                    public void assertModel(SendModel model) {
                        Assert.assertEquals(model.getEndpoint(), "myEndpoint");
                    }
                }},
                new Object[] {new ReceiveMessageActionConverter(), new ModelAndAssertion<ReceiveModel, ReceiveMessageAction>() {
                    @Override
                    public ReceiveModel getModel() {
                        ReceiveModel model = new ReceiveModel();
                        model.setEndpoint("myEndpoint");
                        return model;
                    }

                    @Override
                    public ReceiveMessageAction getAction() {
                        return new ReceiveMessageAction()
                                .setEndpointUri("myEndpoint");
                    }

                    @Override
                    public void assertModel(TestActionModel model) {
                        Assert.assertEquals(model.getType(), "receive");
                        Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("endpoint")).findFirst().orElse(new Property()).getValue(), "myEndpoint");
                    }

                    @Override
                    public void assertModel(ReceiveModel model) {
                        Assert.assertEquals(model.getEndpoint(), "myEndpoint");
                    }
                }},
                new Object[] {new EchoActionConverter(), new ModelAndAssertion<EchoModel, EchoAction>() {
                    @Override
                    public EchoModel getModel() {
                        EchoModel model = new EchoModel();
                        model.setMessage("Hello Citrus!");
                        return model;
                    }

                    @Override
                    public EchoAction getAction() {
                        return new EchoAction()
                                .setMessage("Hello Citrus!");
                    }

                    @Override
                    public void assertModel(TestActionModel model) {
                        Assert.assertEquals(model.getType(), "echo");
                        Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("message")).findFirst().orElse(new Property()).getValue(), "Hello Citrus!");
                    }

                    @Override
                    public void assertModel(EchoModel model) {
                        Assert.assertEquals(model.getMessage(), "Hello Citrus!");
                    }
                }},
                new Object[] {new SleepActionConverter(), new ModelAndAssertion<SleepModel, SleepAction>() {
                    @Override
                    public SleepModel getModel() {
                        SleepModel model = new SleepModel();
                        model.setMilliseconds("3500");
                        return model;
                    }

                    @Override
                    public SleepAction getAction() {
                        return new SleepAction()
                                .setMilliseconds("3500");
                    }

                    @Override
                    public void assertModel(TestActionModel model) {
                        Assert.assertEquals(model.getType(), "sleep");
                        Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("milliseconds")).findFirst().orElse(new Property()).getValue(), "3500");
                    }

                    @Override
                    public void assertModel(SleepModel model) {
                        Assert.assertEquals(model.getMilliseconds(), "3500");
                    }
                }},
                new Object[] {new FailActionConverter(), new ModelAndAssertion<FailModel, FailAction>() {
                    @Override
                    public FailModel getModel() {
                        FailModel model = new FailModel();
                        model.setMessage("Should fail!");
                        return model;
                    }

                    @Override
                    public FailAction getAction() {
                        return new FailAction()
                                .setMessage("Should fail!");
                    }

                    @Override
                    public void assertModel(TestActionModel model) {
                        Assert.assertEquals(model.getType(), "fail");
                        Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("message")).findFirst().orElse(new Property()).getValue(), "Should fail!");
                    }

                    @Override
                    public void assertModel(FailModel model) {
                        Assert.assertEquals(model.getMessage(), "Should fail!");
                    }
                }},
                new Object[] {new ParallelContainerConverter().setActionConverter(Arrays.asList(new EchoActionConverter(), new SleepActionConverter())), new ModelAndAssertion<ParallelModel, Parallel>() {
                    @Override
                    public ParallelModel getModel() {
                        ParallelModel model = new ParallelModel();
                        EchoModel nested = new EchoModel();
                        nested.setMessage("Nested action");
                        model.getActionsAndSendsAndReceives().add(nested);

                        SleepModel nested2 = new SleepModel();
                        nested2.setMilliseconds("1000");
                        model.getActionsAndSendsAndReceives().add(nested2);

                        return model;
                    }

                    @Override
                    public Parallel getAction() {
                        Parallel container = new Parallel();
                        container.addTestAction(new EchoAction().setMessage("Nested action"));
                        container.addTestAction(new SleepAction().setMilliseconds("1000"));
                        return container;
                    }

                    @Override
                    public void assertModel(TestActionModel model) {
                        Assert.assertEquals(model.getType(), "parallel");
                        Assert.assertEquals(model.getActions().size(), 2L);
                    }

                    @Override
                    public void assertModel(ParallelModel model) {
                        Assert.assertEquals(model.getActionsAndSendsAndReceives().size(), 2L);
                    }
                }},
                new Object[] {new SequentialContainerConverter().setActionConverter(Collections.singletonList(new EchoActionConverter())), new ModelAndAssertion<SequentialModel, Sequence>() {
                    @Override
                    public SequentialModel getModel() {
                        SequentialModel model = new SequentialModel();
                        EchoModel nested = new EchoModel();
                        nested.setMessage("Nested action");
                        model.getActionsAndSendsAndReceives().add(nested);

                        SleepModel nested2 = new SleepModel();
                        nested2.setMilliseconds("1000");
                        model.getActionsAndSendsAndReceives().add(nested2);

                        return model;
                    }

                    @Override
                    public Sequence getAction() {
                        Sequence container = new Sequence();
                        container.addTestAction(new EchoAction().setMessage("Nested action"));
                        container.addTestAction(new SleepAction().setMilliseconds("1000"));
                        return container;
                    }

                    @Override
                    public void assertModel(TestActionModel model) {
                        Assert.assertEquals(model.getType(), "sequential");
                        Assert.assertEquals(model.getActions().size(), 2L);
                    }

                    @Override
                    public void assertModel(SequentialModel model) {
                        Assert.assertEquals(model.getActionsAndSendsAndReceives().size(), 2L);
                    }
                }},
                new Object[] {new IterateContainerConverter().setActionConverter(Arrays.asList(new EchoActionConverter(), new SleepActionConverter())), new ModelAndAssertion<IterateModel, Iterate>() {
                    @Override
                    public IterateModel getModel() {
                        IterateModel model = new IterateModel();
                        EchoModel nested = new EchoModel();
                        nested.setMessage("Nested action");
                        model.getActionsAndSendsAndReceives().add(nested);

                        SleepModel nested2 = new SleepModel();
                        nested2.setMilliseconds("1000");
                        model.getActionsAndSendsAndReceives().add(nested2);

                        return model;
                    }

                    @Override
                    public Iterate getAction() {
                        Iterate container = new Iterate();
                        container.addTestAction(new EchoAction().setMessage("Nested action"));
                        container.addTestAction(new SleepAction().setMilliseconds("1000"));
                        return container;
                    }

                    @Override
                    public void assertModel(TestActionModel model) {
                        Assert.assertEquals(model.getType(), "iterate");
                        Assert.assertEquals(model.getActions().size(), 2L);
                    }

                    @Override
                    public void assertModel(IterateModel model) {
                        Assert.assertEquals(model.getActionsAndSendsAndReceives().size(), 2L);
                    }
                }},
                new Object[] {new RepeatOnErrorContainerConverter().setActionConverter(Arrays.asList(new EchoActionConverter(), new SleepActionConverter())), new ModelAndAssertion<RepeatOnerrorUntilTrueModel, RepeatOnErrorUntilTrue>() {
                    @Override
                    public RepeatOnerrorUntilTrueModel getModel() {
                        RepeatOnerrorUntilTrueModel model = new RepeatOnerrorUntilTrueModel();
                        EchoModel nested = new EchoModel();
                        nested.setMessage("Nested action");
                        model.getActionsAndSendsAndReceives().add(nested);

                        SleepModel nested2 = new SleepModel();
                        nested2.setMilliseconds("1000");
                        model.getActionsAndSendsAndReceives().add(nested2);

                        return model;
                    }

                    @Override
                    public RepeatOnErrorUntilTrue getAction() {
                        RepeatOnErrorUntilTrue container = new RepeatOnErrorUntilTrue();
                        container.addTestAction(new EchoAction().setMessage("Nested action"));
                        container.addTestAction(new SleepAction().setMilliseconds("1000"));
                        return container;
                    }

                    @Override
                    public void assertModel(TestActionModel model) {
                        Assert.assertEquals(model.getType(), "repeat-on-error");
                        Assert.assertEquals(model.getActions().size(), 2L);
                    }

                    @Override
                    public void assertModel(RepeatOnerrorUntilTrueModel model) {
                        Assert.assertEquals(model.getActionsAndSendsAndReceives().size(), 2L);
                    }
                }},
                new Object[] {new RepeatContainerConverter().setActionConverter(Arrays.asList(new EchoActionConverter(), new SleepActionConverter())), new ModelAndAssertion<RepeatUntilTrueModel, RepeatUntilTrue>() {
                    @Override
                    public RepeatUntilTrueModel getModel() {
                        RepeatUntilTrueModel model = new RepeatUntilTrueModel();
                        EchoModel nested = new EchoModel();
                        nested.setMessage("Nested action");
                        model.getActionsAndSendsAndReceives().add(nested);

                        SleepModel nested2 = new SleepModel();
                        nested2.setMilliseconds("1000");
                        model.getActionsAndSendsAndReceives().add(nested2);

                        return model;
                    }

                    @Override
                    public RepeatUntilTrue getAction() {
                        RepeatUntilTrue container = new RepeatUntilTrue();
                        container.addTestAction(new EchoAction().setMessage("Nested action"));
                        container.addTestAction(new SleepAction().setMilliseconds("1000"));
                        return container;
                    }

                    @Override
                    public void assertModel(TestActionModel model) {
                        Assert.assertEquals(model.getType(), "repeat");
                        Assert.assertEquals(model.getActions().size(), 2L);
                    }

                    @Override
                    public void assertModel(RepeatUntilTrueModel model) {
                        Assert.assertEquals(model.getActionsAndSendsAndReceives().size(), 2L);
                    }
                }},
                new Object[] {new TimerContainerConverter().setActionConverter(Arrays.asList(new EchoActionConverter(), new SleepActionConverter())), new ModelAndAssertion<TimerModel, Timer>() {
                    @Override
                    public TimerModel getModel() {
                        TimerModel model = new TimerModel();
                        EchoModel nested = new EchoModel();
                        nested.setMessage("Nested action");
                        model.getActionsAndSendsAndReceives().add(nested);

                        SleepModel nested2 = new SleepModel();
                        nested2.setMilliseconds("1000");
                        model.getActionsAndSendsAndReceives().add(nested2);

                        return model;
                    }

                    @Override
                    public Timer getAction() {
                        Timer container = new Timer();
                        container.addTestAction(new EchoAction().setMessage("Nested action"));
                        container.addTestAction(new SleepAction().setMilliseconds("1000"));
                        return container;
                    }

                    @Override
                    public void assertModel(TestActionModel model) {
                        Assert.assertEquals(model.getType(), "timer");
                        Assert.assertEquals(model.getActions().size(), 2L);
                    }

                    @Override
                    public void assertModel(TimerModel model) {
                        Assert.assertEquals(model.getActionsAndSendsAndReceives().size(), 2L);
                    }
                }},
                new Object[] {new ConditionalContainerConverter().setActionConverter(Arrays.asList(new EchoActionConverter(), new SleepActionConverter())), new ModelAndAssertion<ConditionalModel, Conditional>() {
                    @Override
                    public ConditionalModel getModel() {
                        ConditionalModel model = new ConditionalModel();
                        EchoModel nested = new EchoModel();
                        nested.setMessage("Nested action");
                        model.getActionsAndSendsAndReceives().add(nested);

                        SleepModel nested2 = new SleepModel();
                        nested2.setMilliseconds("1000");
                        model.getActionsAndSendsAndReceives().add(nested2);

                        return model;
                    }

                    @Override
                    public Conditional getAction() {
                        Conditional container = new Conditional();
                        container.addTestAction(new EchoAction().setMessage("Nested action"));
                        container.addTestAction(new SleepAction().setMilliseconds("1000"));
                        return container;
                    }

                    @Override
                    public void assertModel(TestActionModel model) {
                        Assert.assertEquals(model.getType(), "conditional");
                        Assert.assertEquals(model.getActions().size(), 2L);
                    }

                    @Override
                    public void assertModel(ConditionalModel model) {
                        Assert.assertEquals(model.getActionsAndSendsAndReceives().size(), 2L);
                    }
                }},
                new Object[] {new CatchContainerConverter().setActionConverter(Arrays.asList(new FailActionConverter())), new ModelAndAssertion<CatchModel, Catch>() {
                    @Override
                    public CatchModel getModel() {
                        CatchModel model = new CatchModel();
                        FailModel nested = new FailModel();
                        nested.setMessage("Should fail!");
                        model.getActionsAndSendsAndReceives().add(nested);

                        return model;
                    }

                    @Override
                    public Catch getAction() {
                        Catch container = new Catch();
                        container.addTestAction(new FailAction().setMessage("Should fail!"));
                        return container;
                    }

                    @Override
                    public void assertModel(TestActionModel model) {
                        Assert.assertEquals(model.getType(), "catch");
                        Assert.assertEquals(model.getActions().size(), 1L);
                    }

                    @Override
                    public void assertModel(CatchModel model) {
                        Assert.assertEquals(model.getActionsAndSendsAndReceives().size(), 1L);
                    }
                }},
                new Object[] {new AssertContainerConverter().setActionConverter(Collections.singletonList(new EchoActionConverter())), new ModelAndAssertion<AssertModel, com.consol.citrus.container.Assert>() {
                    @Override
                    public AssertModel getModel() {
                        AssertModel model = new AssertModel();
                        EchoModel nested = new EchoModel();
                        nested.setMessage("Nested action");
                        model.setWhen(new AssertModel.When());
                        model.getWhen().setEcho(nested);
                        return model;
                    }

                    @Override
                    public com.consol.citrus.container.Assert getAction() {
                        com.consol.citrus.container.Assert action = new com.consol.citrus.container.Assert();
                        action.setAction(new EchoAction().setMessage("Should raise a fault!"));
                        return action;
                    }

                    @Override
                    public void assertModel(TestActionModel model) {
                        Assert.assertEquals(model.getType(), "assert");
                        Assert.assertEquals(model.getActions().size(), 1L);
                    }

                    @Override
                    public void assertModel(AssertModel model) {
                        Assert.assertNotNull(model.getWhen().getEcho());
                    }
                }},
                new Object[] {new AssertSoapFaultContainerConverter().setActionConverter(Collections.singletonList(new EchoActionConverter())), new ModelAndAssertion<AssertFaultModel, AssertSoapFault>() {
                    @Override
                    public AssertFaultModel getModel() {
                        AssertFaultModel model = new AssertFaultModel();
                        EchoModel nested = new EchoModel();
                        nested.setMessage("Nested action");
                        model.setWhen(new AssertFaultModel.When());
                        model.getWhen().setEcho(nested);
                        return model;
                    }

                    @Override
                    public AssertSoapFault getAction() {
                        return new AssertSoapFault()
                                .setAction(new EchoAction().setMessage("Should raise a fault!"));
                    }

                    @Override
                    public void assertModel(TestActionModel model) {
                        Assert.assertEquals(model.getType(), "assert-fault");
                        Assert.assertEquals(model.getActions().size(), 1L);
                    }

                    @Override
                    public void assertModel(AssertFaultModel model) {
                        Assert.assertNotNull(model.getWhen().getEcho());
                        Assert.assertEquals(model.getWhen().getEcho().getMessage(), "Should raise a fault!");
                    }
                }},
                new Object[] {new SendRequestActionConverter(), new ModelAndAssertion<SendRequestModel, SendMessageAction>() {
                    @Override
                    public SendRequestModel getModel() {
                        SendRequestModel model = new SendRequestModel();
                        model.setClient("myEndpoint");
                        ClientRequestType request = new ClientRequestType();
                        request.setBody(new ClientRequestType.Body());
                        request.getBody().setData("Hello Citrus!");
                        model.setPOST(request);
                        return model;
                    }

                    @Override
                    public SendMessageAction getAction() {
                        return new SendMessageAction()
                                .setEndpointUri("myEndpoint");
                    }

                    @Override
                    public void assertModel(TestActionModel model) {
                        Assert.assertEquals(model.getType(), "send");
                        Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("endpoint")).findFirst().orElse(new Property()).getValue(), "myEndpoint");
                        Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("message")).findFirst().orElse(new Property()).getValue(), "Hello Citrus!");
                    }

                    @Override
                    public void assertModel(SendRequestModel model) {
                        Assert.assertEquals(model.getClient(), "myEndpoint");
                    }
                }},
                new Object[] {new ReceiveResponseActionConverter(), new ModelAndAssertion<ReceiveResponseModel, ReceiveMessageAction>() {
                    @Override
                    public ReceiveResponseModel getModel() {
                        ReceiveResponseModel model = new ReceiveResponseModel();
                        model.setClient("myEndpoint");

                        ResponseHeadersType headers = new ResponseHeadersType();
                        headers.setStatus(HttpStatus.OK.toString());
                        model.setHeaders(headers);
                        return model;
                    }

                    @Override
                    public ReceiveMessageAction getAction() {
                        return new ReceiveMessageAction()
                                .setEndpointUri("myEndpoint");
                    }

                    @Override
                    public void assertModel(TestActionModel model) {
                        Assert.assertEquals(model.getType(), "receive");
                        Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("endpoint")).findFirst().orElse(new Property()).getValue(), "myEndpoint");
                        Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("status")).findFirst().orElse(new Property()).getValue(), "200");
                        Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("version")).findFirst().orElse(new Property()).getValue(), "HTTP/1.1");
                    }

                    @Override
                    public void assertModel(ReceiveResponseModel model) {
                        Assert.assertEquals(model.getClient(), "myEndpoint");
                    }
                }},
                new Object[] {new ReceiveRequestActionConverter(), new ModelAndAssertion<ReceiveRequestModel, ReceiveMessageAction>() {
                    @Override
                    public ReceiveRequestModel getModel() {
                        ReceiveRequestModel model = new ReceiveRequestModel();
                        model.setServer("myEndpoint");
                        ServerRequestType request = new ServerRequestType();
                        request.setBody(new ServerRequestType.Body());
                        request.getBody().setData("Hello Citrus!");
                        model.setPOST(request);
                        return model;
                    }

                    @Override
                    public ReceiveMessageAction getAction() {
                        return new ReceiveMessageAction()
                                .setEndpointUri("myEndpoint");
                    }

                    @Override
                    public void assertModel(TestActionModel model) {
                        Assert.assertEquals(model.getType(), "receive");
                        Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("endpoint")).findFirst().orElse(new Property()).getValue(), "myEndpoint");
                        Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("message")).findFirst().orElse(new Property()).getValue(), "Hello Citrus!");
                    }

                    @Override
                    public void assertModel(ReceiveRequestModel model) {
                        Assert.assertEquals(model.getServer(), "myEndpoint");
                    }
                }},
                new Object[] {new SendResponseActionConverter(), new ModelAndAssertion<SendResponseModel, SendMessageAction>() {
                    @Override
                    public SendResponseModel getModel() {
                        SendResponseModel model = new SendResponseModel();
                        model.setServer("myEndpoint");

                        ResponseHeadersType headers = new ResponseHeadersType();
                        headers.setStatus(HttpStatus.OK.toString());
                        model.setHeaders(headers);
                        return model;
                    }

                    @Override
                    public SendMessageAction getAction() {
                        return new SendMessageAction()
                                .setEndpointUri("myEndpoint");
                    }

                    @Override
                    public void assertModel(TestActionModel model) {
                        Assert.assertEquals(model.getType(), "send");
                        Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("endpoint")).findFirst().orElse(new Property()).getValue(), "myEndpoint");
                        Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("status")).findFirst().orElse(new Property()).getValue(), "200");
                        Assert.assertEquals(model.getProperties().stream().filter(property -> property.getName().equals("version")).findFirst().orElse(new Property()).getValue(), "HTTP/1.1");
                    }

                    @Override
                    public void assertModel(SendResponseModel model) {
                        Assert.assertEquals(model.getServer(), "myEndpoint");
                    }
                }},
                new Object[] {new ActionConverter("sample"), new ModelAndAssertion<ActionModel, EchoAction>() {
                    @Override
                    public ActionModel getModel() {
                        return new ActionModel();
                    }

                    @Override
                    public EchoAction getAction() {
                        return new EchoAction()
                                .setMessage("Hello Citrus!");
                    }

                    @Override
                    public void assertModel(TestActionModel model) {
                        Assert.assertEquals(model.getType(), "sample");
                    }

                    @Override
                    public void assertModel(ActionModel model) {
                        Assert.assertNotNull(model.getReference());
                    }
                }},
        };
    }

    /**
     * Model assertion interface.
     */
    interface ModelAndAssertion<T, A extends TestAction> {

        void assertModel(T action);

        void assertModel(TestActionModel model);

        T getModel();

        A getAction();
    }
}