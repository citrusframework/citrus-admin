package com.consol.citrus.admin.converter.model.config;

import com.consol.citrus.admin.converter.model.AbstractModelConverter;
import com.consol.citrus.model.config.core.SchemaModel;
import com.consol.citrus.model.config.core.SchemaRepositoryModel;
import com.consol.citrus.xml.XsdSchemaRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

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
