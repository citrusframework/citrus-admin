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

import com.consol.citrus.container.Iterate;
import com.consol.citrus.model.testcase.core.IterateModel;
import com.consol.citrus.model.testcase.core.ObjectFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author Christoph Deppisch
 * @since 2.6
 */
@Component
public class IterateContainerConverter extends AbstractTestContainerConverter<IterateModel, Iterate> {

    public IterateContainerConverter() {
        super("iterate");
    }

    @Override
    protected List<Object> getNestedActions(IterateModel model) {
        return model.getActionsAndSendsAndReceives();
    }

    @Override
    public IterateModel convertModel(Iterate model) {
        IterateModel action = new ObjectFactory().createIterateModel();
        action.setDescription(model.getDescription());
        convertActions(model, action.getActionsAndSendsAndReceives());

        return action;
    }

    @Override
    protected boolean include(IterateModel model, Field field) {
        return super.include(model, field) && !field.getName().equals("actionsAndSendsAndReceives");
    }

    @Override
    public Class<Iterate> getActionModelClass() {
        return Iterate.class;
    }

    @Override
    public Class<IterateModel> getSourceModelClass() {
        return IterateModel.class;
    }
}
