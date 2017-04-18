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
import com.consol.citrus.model.config.core.SchemaRepositoryModel;
import com.consol.citrus.xml.XsdSchemaRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.xml.xsd.SimpleXsdSchema;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Christoph Deppisch
 */
@Component
public class SchemaRepositoryModelConverter extends AbstractModelConverter<SchemaRepositoryModel, XsdSchemaRepository> {
    /**
     * Default constructor.
     */
    public SchemaRepositoryModelConverter() {
        super(SchemaRepositoryModel.class, XsdSchemaRepository.class);

        addDecorator(new MethodCallDecorator("setSchemas") {
            @Override
            public Object decorateArgument(Object arg) {
                getAdditionalImports().add(Collectors.class);
                getAdditionalImports().add(ClassPathResource.class);
                getAdditionalImports().add(SimpleXsdSchema.class);
                getAdditionalImports().add(Stream.class);

                SchemaRepositoryModel.Schemas schemas = (SchemaRepositoryModel.Schemas) arg;
                StringBuilder codeBuilder = new StringBuilder();

                codeBuilder.append("Stream.of(");
                schemas.getSchemas().forEach(schema -> codeBuilder.append(String.format("%n\t\t\t\tnew SimpleXsdSchema(new ClassPathResource(\"%s\")),", schema.getLocation())));

                if (CollectionUtils.isEmpty(schemas.getReferences())) {
                    codeBuilder.deleteCharAt(codeBuilder.length() - 1);
                }

                schemas.getReferences().forEach(schemaRef -> codeBuilder.append(String.format("%n\t\t\t\t%s(),", schemaRef.getSchema())));
                if (!CollectionUtils.isEmpty(schemas.getReferences())) {
                    codeBuilder.deleteCharAt(codeBuilder.length() - 1);
                }

                codeBuilder.append(String.format(")%n\t\t\t.collect(Collectors.toList())"));

                return codeBuilder.toString();
            }

            @Override
            public boolean allowMethodCall(Object arg) {
                return ((SchemaRepositoryModel.Schemas) arg).getSchemas().size() > 0;
            }
        });
    }

    @Override
    public SchemaRepositoryModel convert(String id, XsdSchemaRepository model) {
        SchemaRepositoryModel converted = convert(model);
        converted.setId(id);

        SchemaRepositoryModel.Locations locations = new SchemaRepositoryModel.Locations();
        model.getLocations().forEach(location -> {
            SchemaRepositoryModel.Locations.Location schemaLocation = new SchemaRepositoryModel.Locations.Location();
            schemaLocation.setPath(location);
            locations.getLocations().add(schemaLocation);
        });

        if (!CollectionUtils.isEmpty(locations.getLocations())) {
            converted.setLocations(locations);
        }

        SchemaRepositoryModel.Schemas schemas = new SchemaRepositoryModel.Schemas();
        model.getSchemas().forEach(schema -> {
            SchemaModel schemaModel = new SchemaModel();
            schemaModel.setId("schema:" + schema.hashCode());
            schemas.getSchemas().add(schemaModel);
        });

        if (!CollectionUtils.isEmpty(schemas.getSchemas())) {
            converted.setSchemas(schemas);
        }

        return converted;
    }

    @Override
    public String getJavaConfig(SchemaRepositoryModel model) {
        return getJavaConfig(model, model.getId());
    }
}
