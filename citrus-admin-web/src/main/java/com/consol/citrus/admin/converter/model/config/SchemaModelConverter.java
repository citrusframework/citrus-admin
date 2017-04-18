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
import com.consol.citrus.model.config.core.SchemaModel;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.xml.xsd.SimpleXsdSchema;

/**
 * @author Christoph Deppisch
 */
@Component
public class SchemaModelConverter extends AbstractModelConverter<SchemaModel, SimpleXsdSchema> {
    /**
     * Default constructor.
     */
    public SchemaModelConverter() {
        super(SchemaModel.class, SimpleXsdSchema.class);

        addDecorator(new MethodCallDecorator("setLocation", "setXsd") {
            @Override
            public Object decorateArgument(Object arg) {
                getAdditionalImports().add(ClassPathResource.class);
                return "new ClassPathResource(\"" + arg.toString() + "\")";
            }
        });
    }

    @Override
    public SchemaModel convert(String id, SimpleXsdSchema model) {
        SchemaModel converted = convert(model);
        converted.setId(id);
        return converted;
    }

    @Override
    public String getJavaConfig(SchemaModel model) {
        return getJavaConfig(model, model.getId());
    }
}
