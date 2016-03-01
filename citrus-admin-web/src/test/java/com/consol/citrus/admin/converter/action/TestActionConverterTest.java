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

package com.consol.citrus.admin.converter.action;

import com.consol.citrus.actions.*;
import com.consol.citrus.admin.model.TestAction;
import com.consol.citrus.model.testcase.core.*;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author Christoph Deppisch
 */
public class TestActionConverterTest {

    @Test(dataProvider = "converterData")
    public void testConvert(TestActionConverter actionConverter, Object definition, com.consol.citrus.TestAction actionModel, String actionType) throws Exception {
        TestAction result = actionConverter.convert(definition);
        Assert.assertEquals(result.getType(), actionType);

        Object modelConversion = actionConverter.convertModel(actionModel);
        Assert.assertEquals(modelConversion.getClass(), definition.getClass());
    }

    @DataProvider
    public Object[][] converterData() {
        return new Object[][] {
                new Object[] {new SendMessageActionConverter(), new SendDefinition(), new SendMessageAction(), "send"},
                new Object[] {new ReceiveMessageActionConverter(), new ReceiveDefinition(), new ReceiveMessageAction(), "receive"},
                new Object[] {new EchoActionConverter(), new EchoDefinition(), new EchoAction(), "echo"},
                new Object[] {new SleepActionConverter(), new SleepDefinition(), new SleepAction(), "sleep"},
                new Object[] {new ActionConverter("sample"), new ActionDefinition(), new EchoAction(), "sample"},
        };
    }
}