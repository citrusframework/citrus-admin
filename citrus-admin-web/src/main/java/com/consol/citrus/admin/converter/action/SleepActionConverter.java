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

import com.consol.citrus.actions.SleepAction;
import com.consol.citrus.admin.model.TestAction;
import com.consol.citrus.model.testcase.core.*;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component
public class SleepActionConverter extends AbstractTestActionConverter<SleepModel, SleepAction> {

    public SleepActionConverter() {
        super("sleep");
    }

    @Override
    public TestAction convert(SleepModel model) {
        TestAction action = new TestAction(getActionType(), getSourceModelClass());

        addActionProperties(action, model);

        action.add(property("milliseconds", model));
        action.add(property("seconds", model));

        return action;
    }

    @Override
    public SleepModel convertModel(SleepAction model) {
        SleepModel action = new ObjectFactory().createSleepModel();

        action.setDescription(model.getDescription());
        action.setMilliseconds(model.getMilliseconds());
        action.setSeconds(model.getSeconds());

        return action;
    }

    @Override
    public Class<SleepModel> getSourceModelClass() {
        return SleepModel.class;
    }

    @Override
    public Class<SleepAction> getActionModelClass() {
        return SleepAction.class;
    }
}
