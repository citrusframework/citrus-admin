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

import com.consol.citrus.actions.EchoAction;
import com.consol.citrus.admin.model.TestAction;
import com.consol.citrus.model.testcase.core.*;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component
public class EchoActionConverter extends AbstractTestActionConverter<EchoModel, EchoAction> {

    public EchoActionConverter() {
        super("echo");
    }

    @Override
    public TestAction convert(EchoModel model) {
        TestAction action = new TestAction(getActionType(), getSourceModelClass());

        addActionProperties(action, model);

        action.add(property("message", model));

        return action;
    }

    @Override
    public EchoModel convertModel(EchoAction model) {
        EchoModel action = new ObjectFactory().createEchoModel();

        action.setDescription(model.getDescription());
        action.setMessage(model.getMessage());

        return action;
    }

    @Override
    public Class<EchoModel> getSourceModelClass() {
        return EchoModel.class;
    }

    @Override
    public Class<EchoAction> getActionModelClass() {
        return EchoAction.class;
    }
}
