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
