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
