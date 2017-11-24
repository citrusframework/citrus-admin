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
import com.consol.citrus.admin.model.TestActionModel;
import com.consol.citrus.model.testcase.core.ActionModel;
import com.consol.citrus.model.testcase.core.ObjectFactory;

/**
 * @author Christoph Deppisch
 */
public class ActionConverter extends AbstractTestActionConverter<Object, TestAction> {

    /**
     * Default constructor.
     * @param actionType
     */
    public ActionConverter(String actionType) {
        super(actionType);
    }

    @Override
    public Object convertModel(TestAction model) {
        ActionModel action = new ObjectFactory().createActionModel();

        action.setReference(model.getName());
        action.setDescription(model.getDescription());

        return action;
    }

    @Override
    public Class<Object> getSourceModelClass() {
        return Object.class;
    }

    @Override
    public Class<TestActionModel> getTargetModelClass() {
        return TestActionModel.class;
    }

    @Override
    public Class<com.consol.citrus.TestAction> getActionModelClass() {
        return com.consol.citrus.TestAction.class;
    }
}
