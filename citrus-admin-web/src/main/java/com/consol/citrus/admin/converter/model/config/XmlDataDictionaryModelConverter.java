package com.consol.citrus.admin.converter.model.config;

import com.consol.citrus.model.config.core.XmlDataDictionaryModel;
import com.consol.citrus.variable.dictionary.xml.NodeMappingDataDictionary;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * @author Christoph Deppisch
 */
@Component
public class XmlDataDictionaryModelConverter extends AbstractDataDictionaryModelConverter<XmlDataDictionaryModel, NodeMappingDataDictionary> {
    /**
     * Default constructor.
     */
    public XmlDataDictionaryModelConverter() {
        super(XmlDataDictionaryModel.class, NodeMappingDataDictionary.class);
    }

    @Override
    public XmlDataDictionaryModel convert(String id, NodeMappingDataDictionary model) {
        XmlDataDictionaryModel converted = convert(model);
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
    public String getJavaConfig(XmlDataDictionaryModel model) {
        return getJavaConfig(model, model.getId());
    }
}
