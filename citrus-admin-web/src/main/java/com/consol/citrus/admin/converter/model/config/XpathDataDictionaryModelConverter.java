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
