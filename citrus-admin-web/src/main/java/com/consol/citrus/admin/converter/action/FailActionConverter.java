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

import com.consol.citrus.actions.FailAction;
import com.consol.citrus.model.testcase.core.FailModel;
import com.consol.citrus.model.testcase.core.ObjectFactory;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component
public class FailActionConverter extends AbstractTestActionConverter<FailModel, FailAction> {

    public FailActionConverter() {
        super("fail");
    }

    @Override
    public FailModel convertModel(FailAction model) {
        FailModel action = new ObjectFactory().createFailModel();

        action.setDescription(model.getDescription());
        action.setMessage(model.getMessage());

        return action;
    }

    @Override
    public Class<FailModel> getSourceModelClass() {
        return FailModel.class;
    }

    @Override
    public Class<FailAction> getActionModelClass() {
        return FailAction.class;
    }
}
