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

import com.consol.citrus.container.Catch;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.model.testcase.core.CatchModel;
import com.consol.citrus.model.testcase.core.ObjectFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

/**
 * @author Christoph Deppisch
 * @since 2.7
 */
@Component
public class CatchContainerConverter extends AbstractTestContainerConverter<CatchModel, Catch> {

    public CatchContainerConverter() {
        super("catch");
    }

    @Override
    protected List<Object> getNestedActions(CatchModel model) {
        return model.getActionsAndSendsAndReceives();
    }

    @Override
    public CatchModel convertModel(Catch model) {
        CatchModel action = new ObjectFactory().createCatchModel();
        action.setDescription(model.getDescription());
        convertActions(model, action.getActionsAndSendsAndReceives());

        return action;
    }

    @Override
    protected Map<String, Object> getDefaultValueMappings() {
        Map<String, Object> mappings = super.getDefaultValueMappings();
        mappings.put("exception", CitrusRuntimeException.class.getName());
        return mappings;
    }

    @Override
    protected boolean include(CatchModel model, Field field) {
        return super.include(model, field) && !field.getName().equals("actionsAndSendsAndReceives");
    }

    @Override
    public Class<Catch> getActionModelClass() {
        return Catch.class;
    }

    @Override
    public Class<CatchModel> getSourceModelClass() {
        return CatchModel.class;
    }
}
