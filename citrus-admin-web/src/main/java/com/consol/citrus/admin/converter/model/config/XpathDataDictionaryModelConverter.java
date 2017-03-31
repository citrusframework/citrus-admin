package com.consol.citrus.admin.converter.model.config;

import com.consol.citrus.model.config.core.XpathDataDictionaryModel;
import com.consol.citrus.variable.dictionary.xml.XpathMappingDataDictionary;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * @author Christoph Deppisch
 */
@Component
public class XpathDataDictionaryModelConverter extends AbstractDataDictionaryModelConverter<XpathDataDictionaryModel, XpathMappingDataDictionary> {
    /**
     * Default constructor.
     */
    public XpathDataDictionaryModelConverter() {
        super(XpathDataDictionaryModel.class, XpathMappingDataDictionary.class);
    }

    @Override
    public XpathDataDictionaryModel convert(String id, XpathMappingDataDictionary model) {
        XpathDataDictionaryModel converted = convert(model);
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
    public String getJavaConfig(XpathDataDictionaryModel model) {
        return getJavaConfig(model, model.getId());
    }
}
