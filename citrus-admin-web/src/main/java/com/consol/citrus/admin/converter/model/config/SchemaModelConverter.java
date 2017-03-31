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
