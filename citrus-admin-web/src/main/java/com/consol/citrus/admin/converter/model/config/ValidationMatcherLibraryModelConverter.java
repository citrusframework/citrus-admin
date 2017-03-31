package com.consol.citrus.admin.converter.model.config;

import com.consol.citrus.admin.converter.model.AbstractModelConverter;
import com.consol.citrus.model.config.core.ValidationMatcherLibraryModel;
import com.consol.citrus.validation.matcher.ValidationMatcherLibrary;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component
public class ValidationMatcherLibraryModelConverter extends AbstractModelConverter<ValidationMatcherLibraryModel, ValidationMatcherLibrary> {
    /**
     * Default constructor.
     */
    public ValidationMatcherLibraryModelConverter() {
        super(ValidationMatcherLibraryModel.class, ValidationMatcherLibrary.class);
    }

    @Override
    public ValidationMatcherLibraryModel convert(String id, ValidationMatcherLibrary model) {
        ValidationMatcherLibraryModel converted = convert(model);
        converted.setId(id);

        model.getMembers().forEach((key, value) -> {
            ValidationMatcherLibraryModel.Matcher matcher = new ValidationMatcherLibraryModel.Matcher();

            matcher.setName(key);
            matcher.setClazz(value.getClass().getName());

            converted.getMatchers().add(matcher);
        });

        return converted;
    }

    @Override
    public String getJavaConfig(ValidationMatcherLibraryModel model) {
        return getJavaConfig(model, model.getId());
    }
}
