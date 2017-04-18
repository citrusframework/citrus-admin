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
import com.consol.citrus.variable.dictionary.AbstractDataDictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        addDecorator(new MethodCallDecorator("setMappingStrategy", "setPathMappingStrategy") {
            @Override
            public Object decorateArgument(Object arg) {
                getAdditionalImports().add(AbstractDataDictionary.class);
                return "AbstractDataDictionary.PathMappingStrategy." + arg.toString();
            }
        });

        addDecorator(new MethodCallDecorator("setMappings") {
            @Override
            public Object decorateArgument(Object arg) {
                getAdditionalImports().add(AbstractMap.class);
                getAdditionalImports().add(Collectors.class);
                getAdditionalImports().add(Stream.class);

                DataDictionaryType.Mappings mappings = (DataDictionaryType.Mappings) arg;
                StringBuilder codeBuilder = new StringBuilder();

                codeBuilder.append("Stream.of(");
                mappings.getMappings().forEach(mapping -> codeBuilder.append(String.format("%n\t\t\t\tnew AbstractMap.SimpleEntry<>(\"%s\", \"%s\"),", mapping.getPath(), mapping.getValue())));
                codeBuilder.deleteCharAt(codeBuilder.length() - 1);
                codeBuilder.append(String.format(")%n\t\t\t.collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue))"));

                return codeBuilder.toString();
            }

            @Override
            public boolean allowMethodCall(Object arg) {
                return arg != null && ((DataDictionaryType.Mappings)arg).getMappings().size() > 0;
            }
        });
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
