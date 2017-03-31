package com.consol.citrus.admin.converter.model.config;

import com.consol.citrus.admin.converter.model.AbstractModelConverter;
import com.consol.citrus.model.config.core.DataDictionaryType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.Map;

/**
 * @author Christoph Deppisch
 */
public abstract class AbstractDataDictionaryModelConverter<T, S> extends AbstractModelConverter<T, S> {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(AbstractDataDictionaryModelConverter.class);

    /**
     * Default constructor using source and target model types.
     *
     * @param targetModelType
     * @param sourceModelType
     */
    public AbstractDataDictionaryModelConverter(Class<T> targetModelType, Class<S> sourceModelType) {
        super(targetModelType, sourceModelType);
    }

    /**
     * Create mappings model.
     * @param mappings
     */
    protected DataDictionaryType.Mappings createMappings(Map<String, String> mappings) {
        DataDictionaryType.Mappings mappingsModel = new DataDictionaryType.Mappings();
        mappings.forEach((key, value) -> {
            DataDictionaryType.Mappings.Mapping mapping = new DataDictionaryType.Mappings.Mapping();
            mapping.setPath(key);
            mapping.setValue(value);

            mappingsModel.getMappings().add(mapping);
        });

        return mappingsModel;
    }

    /**
     * Create mapping file model.
     * @param mappingFile
     * @return
     */
    protected DataDictionaryType.MappingFile createMappingFile(Resource mappingFile) {
        DataDictionaryType.MappingFile mappingFileModel = new DataDictionaryType.MappingFile();
        try {
            mappingFileModel.setPath(mappingFile.getFile().getCanonicalPath());
        } catch (IOException e) {
            log.warn("Failed to access mapping file resource", e);
            mappingFileModel.setPath(mappingFile.toString());
        }

        return mappingFileModel;
    }
}
