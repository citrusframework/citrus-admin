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

import com.consol.citrus.container.Parallel;
import com.consol.citrus.model.testcase.core.*;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;

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
    protected List<Object> getNestedActions(ParallelModel model) {
        return model.getActionsAndSendsAndReceives();
    }

    @Override
    public ParallelModel convertModel(Parallel model) {
        ParallelModel action = new ObjectFactory().createParallelModel();
        action.setDescription(model.getDescription());
        convertActions(model, action.getActionsAndSendsAndReceives());

        return action;
    }

    @Override
    protected boolean include(ParallelModel model, Field field) {
        return super.include(model, field) && !field.getName().equals("actionsAndSendsAndReceives");
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
