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

package com.consol.citrus.admin.converter.model;

import com.consol.citrus.admin.converter.ObjectConverter;

import java.util.List;

/**
 * @author Christoph Deppisch
 */
public interface ModelConverter<T, S> extends ObjectConverter<T, S> {

    /**
     * Converts a configuration definition object to desired object.
     * @param id
     * @param model
     * @return
     */
    T convert(String id, S model);

    /**
     * Constructs proper Java config code snippet.
     * @param model
     * @return
     */
    String getJavaConfig(T model);

    /**
     * Gets the additional imports that this converter should add to the target code.
     * @return
     */
    List<Class<?>> getAdditionalImports();

}
