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

package com.consol.citrus.admin.configuration;

import com.consol.citrus.admin.exception.ApplicationRuntimeException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * @author Christoph Deppisch
 */
public class ConfigurationProvider {

    /**
     * Applies configuration to system properties setting {@link SystemProperty} annotated fields as system properties.
     * @param configurationHolder
     */
    public static void apply(Object configurationHolder) {
        SystemConfigurable systemConfigurable = AnnotationUtils.findAnnotation(configurationHolder.getClass(), SystemConfigurable.class);
        
        ReflectionUtils.doWithFields(configurationHolder.getClass(), field -> {
            if (field.getAnnotation(SystemProperty.class) != null) {
                SystemProperty configProperty = field.getAnnotation(SystemProperty.class);
                if (StringUtils.hasText(configProperty.name())) {
                    Method getter = ReflectionUtils.findMethod(configurationHolder.getClass(), "get" + StringUtils.capitalize(field.getName()));
                    if (getter != null) {
                        System.setProperty((systemConfigurable != null ? systemConfigurable.prefix() : "") + configProperty.name(), String.valueOf(ReflectionUtils.invokeMethod(getter, configurationHolder)));
                    }
                }
            }
        });
    }

    /**
     * Instantiates given configuration target and loads system properties and/or environment variables as field value when applicable.
     *
     * @param configurationTarget
     * @param <T>
     * @return
     */
    public static <T> T load(final Class<T> configurationTarget) {
        try {
            return load(configurationTarget.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ApplicationRuntimeException("Failed to instantiate configurable", e);
        }
    }

    /**
     * Enriches configuration instance with system properties and/or environment variables as field value when applicable.
     *
     * @param configuration
     * @param <T>
     * @return
     */
    public static <T> T load(final T configuration) {
        SystemConfigurable systemConfigurable = AnnotationUtils.findAnnotation(configuration.getClass(), SystemConfigurable.class);

        ReflectionUtils.doWithFields(configuration.getClass(), field -> {
            if (field.getAnnotation(SystemProperty.class) != null) {
                SystemProperty systemProperty = field.getAnnotation(SystemProperty.class);
                Method setter = ReflectionUtils.findMethod(configuration.getClass(), "set" + StringUtils.capitalize(field.getName()), field.getType());

                if (setter != null) {
                    if (StringUtils.hasText(systemProperty.name())) {
                        ReflectionUtils.invokeMethod(setter, configuration, System.getProperty((systemConfigurable != null ? systemConfigurable.prefix() : "") + systemProperty.name(), Optional.ofNullable(System.getenv((systemConfigurable != null ? systemConfigurable.environmentPrefix() : "") + systemProperty.environment()))
                                .orElse(getDefaultValue(configuration.getClass(), field, systemProperty, configuration))));
                    } else if (StringUtils.hasText(systemProperty.environment())) {
                        ReflectionUtils.invokeMethod(setter, configuration, Optional.ofNullable(System.getenv((systemConfigurable != null ? systemConfigurable.environmentPrefix() : "") + systemProperty.environment()))
                                .orElse(getDefaultValue(configuration.getClass(), field, systemProperty, configuration)));
                    } else {
                        ReflectionUtils.invokeMethod(setter, configuration, getDefaultValue(configuration.getClass(), field, systemProperty, configuration));
                    }
                }
            }
        });

        return configuration;
    }

    /**
     * Gets default property value. Either uses default value setting or invokes default value supplier. If non of these is set to return a value other than null the
     * default property member initial value is used.
     *
     * @param configurationTarget
     * @param field
     * @param configProperty
     * @param configuration
     * @return
     */
    private static String getDefaultValue(Class<?> configurationTarget, Field field, SystemProperty configProperty, Object configuration) {
        if (StringUtils.hasText(configProperty.defaultValue())) {
            return configProperty.defaultValue();
        } else {
            try {
                Supplier<String> supplier = configProperty.defaultValueSupplier().newInstance();
                String supplied = supplier.get();

                Method getter = ReflectionUtils.findMethod(configurationTarget, "get" + StringUtils.capitalize(field.getName()));
                if (getter != null && supplied == null) {
                    Object defaultValue = ReflectionUtils.invokeMethod(getter, configuration);
                    if (defaultValue != null) {
                        return String.valueOf(defaultValue);
                    }
                }

                return supplied;
            } catch (InstantiationException |IllegalAccessException e) {
                throw new ApplicationRuntimeException("Failed to instantiate default value supplier", e);
            }
        }
    }
}
