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

import com.consol.citrus.container.RepeatUntilTrue;
import com.consol.citrus.model.testcase.core.*;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author Christoph Deppisch
 * @since 2.6
 */
@Component
public class RepeatContainerConverter extends AbstractTestContainerConverter<RepeatUntilTrueModel, RepeatUntilTrue> {

    public RepeatContainerConverter() {
        super("repeat");
    }

    @Override
    protected List<Object> getNestedActions(RepeatUntilTrueModel model) {
        return model.getActionsAndSendsAndReceives();
    }

    @Override
    public RepeatUntilTrueModel convertModel(RepeatUntilTrue model) {
        RepeatUntilTrueModel action = new ObjectFactory().createRepeatUntilTrueModel();
        action.setDescription(model.getDescription());
        convertActions(model, action.getActionsAndSendsAndReceives());

        return action;
    }

    @Override
    protected boolean include(RepeatUntilTrueModel model, Field field) {
        return super.include(model, field) && !field.getName().equals("actionsAndSendsAndReceives");
    }

    @Override
    public Class<RepeatUntilTrue> getActionModelClass() {
        return RepeatUntilTrue.class;
    }

    @Override
    public Class<RepeatUntilTrueModel> getSourceModelClass() {
        return RepeatUntilTrueModel.class;
    }
}
