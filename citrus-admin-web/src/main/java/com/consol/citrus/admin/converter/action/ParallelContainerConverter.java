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

import com.consol.citrus.admin.model.TestAction;
import com.consol.citrus.container.Parallel;
import com.consol.citrus.model.testcase.core.ObjectFactory;
import com.consol.citrus.model.testcase.core.ParallelModel;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 * @since 2.6
 */
@Component
public class ParallelContainerConverter extends AbstractTestContainerConverter<ParallelModel, Parallel> {

    public ParallelContainerConverter() {
        super("parallel");
    }

    @Override
    public TestAction convert(ParallelModel model) {
        TestAction action = new TestAction(getActionType(), getSourceModelClass());
        addActionProperties(action, model);

        action.setActions(getNestedActions(model.getActionsAndSendsAndReceives()));

        return action;
    }

    @Override
    public ParallelModel convertModel(Parallel model) {
        ParallelModel action = new ObjectFactory().createParallelModel();
        action.setDescription(model.getDescription());
        convertActions(model, action.getActionsAndSendsAndReceives());

        return action;
    }

    @Override
    public Class<Parallel> getActionModelClass() {
        return Parallel.class;
    }

    @Override
    public Class<ParallelModel> getSourceModelClass() {
        return ParallelModel.class;
    }
}
