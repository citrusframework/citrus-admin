package com.consol.citrus.admin.converter.model;

import com.consol.citrus.admin.converter.ObjectConverter;

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

}
