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

package com.consol.citrus.admin.service.spring;

import com.consol.citrus.variable.dictionary.DataDictionary;
import com.consol.citrus.variable.dictionary.json.JsonMappingDataDictionary;
import com.consol.citrus.variable.dictionary.xml.NodeMappingDataDictionary;
import com.consol.citrus.variable.dictionary.xml.XpathMappingDataDictionary;
import org.junit.Assert;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;

import static org.mockito.Mockito.when;

/**
 * @author Christoph Deppisch
 */
public class DataDictionaryConfig {

    @Bean
    public NodeMappingDataDictionary xmlDataDictionary() {
        NodeMappingDataDictionary xmlDataDictionary = new NodeMappingDataDictionary();

        xmlDataDictionary.setName("xmlDataDictionary");
        xmlDataDictionary.getMappings().put("foo.text", "newFoo");
        xmlDataDictionary.getMappings().put("bar.text", "newBar");

        xmlDataDictionary.setMappingFile(mappingFile());

        return xmlDataDictionary;
    }

    @Bean
    public XpathMappingDataDictionary xpathDataDictionary() {
        XpathMappingDataDictionary xpathDataDictionary = new XpathMappingDataDictionary();

        xpathDataDictionary.setName("xpathDataDictionary");
        xpathDataDictionary.getMappings().put("//foo/text", "newFoo");
        xpathDataDictionary.getMappings().put("//bar/text", "newBar");

        xpathDataDictionary.setMappingFile(mappingFile());

        xpathDataDictionary.setGlobalScope(false);

        return xpathDataDictionary;
    }

    @Bean
    public JsonMappingDataDictionary jsonDataDictionary() {
        JsonMappingDataDictionary jsonDataDictionary = new JsonMappingDataDictionary();

        jsonDataDictionary.setName("jsonDataDictionary");
        jsonDataDictionary.getMappings().put("foo.text", "newFoo");
        jsonDataDictionary.getMappings().put("bar.text", "newBar");

        jsonDataDictionary.setMappingFile(mappingFile());

        jsonDataDictionary.setPathMappingStrategy(DataDictionary.PathMappingStrategy.ENDS_WITH);

        return jsonDataDictionary;
    }

    private Resource mappingFile() {
        Resource mappingResource = Mockito.mock(Resource.class);
        File mappingFile = Mockito.mock(File.class);

        try {
            when(mappingResource.getFile()).thenReturn(mappingFile);
            when(mappingFile.getCanonicalPath()).thenReturn("path/to/some/mapping/file.map");
        } catch (IOException e) {
            Assert.fail();
        }

        return mappingResource;
    }
}
