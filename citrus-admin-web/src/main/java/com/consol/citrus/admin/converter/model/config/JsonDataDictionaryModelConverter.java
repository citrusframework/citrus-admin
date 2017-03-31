package com.consol.citrus.admin.converter.model.config;

import com.consol.citrus.model.config.core.JsonDataDictionaryModel;
import com.consol.citrus.variable.dictionary.json.JsonMappingDataDictionary;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * @author Christoph Deppisch
 */
@Component
public class JsonDataDictionaryModelConverter extends AbstractDataDictionaryModelConverter<JsonDataDictionaryModel, JsonMappingDataDictionary> {
    /**
     * Default constructor.
     */
    public JsonDataDictionaryModelConverter() {
        super(JsonDataDictionaryModel.class, JsonMappingDataDictionary.class);
    }

    @Override
    public JsonDataDictionaryModel convert(String id, JsonMappingDataDictionary model) {
        JsonDataDictionaryModel converted = convert(model);
        converted.setId(id);

        if (!CollectionUtils.isEmpty(model.getMappings())) {
            converted.setMappings(createMappings(model.getMappings()));
        }

        if (model.getMappingFile() != null) {
            converted.setMappingFile(createMappingFile(model.getMappingFile()));
        }

        converted.setMappingStrategy(model.getPathMappingStrategy().name());

        return converted;
    }

    @Override
    public String getJavaConfig(JsonDataDictionaryModel model) {
        return getJavaConfig(model, model.getId());
    }
}
