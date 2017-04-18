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
import com.consol.citrus.model.config.core.NamespaceContextModel;
import com.consol.citrus.xml.namespace.NamespaceContextBuilder;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component
public class NamespaceContextModelConverter extends AbstractModelConverter<NamespaceContextModel, NamespaceContextBuilder> {
    /**
     * Default constructor.
     */
    public NamespaceContextModelConverter() {
        super(NamespaceContextModel.class, NamespaceContextBuilder.class);
    }

    @Override
    public NamespaceContextModel convert(NamespaceContextBuilder model) {
        NamespaceContextModel converted = super.convert(model);

        model.getNamespaceMappings().forEach((key, value) -> {
            NamespaceContextModel.Namespace namespace = new NamespaceContextModel.Namespace();
            namespace.setPrefix(key);
            namespace.setUri(value);
            converted.getNamespaces().add(namespace);
        });

        return converted;
    }

    @Override
    public String getJavaConfig(NamespaceContextModel model) {
        return getJavaConfig(model, "namespaceContext");
    }
}
