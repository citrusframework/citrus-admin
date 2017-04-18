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

package com.consol.citrus.admin.converter.model.config;

import com.consol.citrus.admin.converter.model.AbstractModelConverter;
import com.consol.citrus.functions.FunctionLibrary;
import com.consol.citrus.model.config.core.FunctionLibraryModel;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component
public class FunctionLibraryModelConverter extends AbstractModelConverter<FunctionLibraryModel, FunctionLibrary> {
    /**
     * Default constructor.
     */
    public FunctionLibraryModelConverter() {
        super(FunctionLibraryModel.class, FunctionLibrary.class);
    }

    @Override
    public FunctionLibraryModel convert(String id, FunctionLibrary model) {
        FunctionLibraryModel converted = convert(model);
        converted.setId(id);

        model.getMembers().forEach((key, value) -> {
            FunctionLibraryModel.Function function = new FunctionLibraryModel.Function();

            function.setName(key);
            function.setClazz(value.getClass().getName());
            
            converted.getFunctions().add(function);
        });

        return converted;
    }

    @Override
    public String getJavaConfig(FunctionLibraryModel model) {
        return getJavaConfig(model, model.getId());
    }
}
