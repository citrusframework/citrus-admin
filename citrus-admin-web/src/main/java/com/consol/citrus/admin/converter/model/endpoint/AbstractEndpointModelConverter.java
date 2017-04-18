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

package com.consol.citrus.admin.converter.model.endpoint;

import com.consol.citrus.admin.converter.model.AbstractModelConverter;
import com.consol.citrus.admin.converter.model.ModelConverter;
import com.consol.citrus.admin.exception.ApplicationRuntimeException;
import com.consol.citrus.endpoint.Endpoint;
import com.consol.citrus.endpoint.EndpointConfiguration;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Christoph Deppisch
 */
public abstract class AbstractEndpointModelConverter<T, S extends Endpoint, C extends EndpointConfiguration> implements ModelConverter<T, S> {

    private Pattern invalidMethodNamePattern = Pattern.compile("[\\s\\.-]");

    private final Class<S> sourceModelType;

    private List<AbstractModelConverter.MethodCallDecorator> decorators = new ArrayList<>();

    /**
     * Delegate model converter operates on endpoint configuration.
     */
    private final ModelConverter<T, C> delegate;

    /**
     * Default constructor using source and target model types.
     *
     * @param targetModelType
     * @param sourceModelType
     * @param configurationType
     */
    protected AbstractEndpointModelConverter(Class<T> targetModelType, Class<S> sourceModelType, Class<C> configurationType) {
        this.sourceModelType = sourceModelType;
        delegate = new AbstractModelConverter<T, C>(targetModelType, configurationType) {
            @Override
            public String getJavaConfig(T model) {
                return AbstractEndpointModelConverter.this.getJavaConfig(model);
            }
        };

        addDecorator(new AbstractModelConverter.MethodCallDecorator("timeout") {
            @Override
            public Object decorateArgument(Object arg) {
                return Long.valueOf(arg.toString());
            }
        });
    }

    /**
     * Default constructor using source and target model types.
     *
     * @param targetModelType
     * @param sourceModelType
     */
    protected AbstractEndpointModelConverter(Class<T> targetModelType, Class<S> sourceModelType, ModelConverter<T, C> delegate) {
        this.sourceModelType = sourceModelType;
        this.delegate = delegate;

        addDecorator(new AbstractModelConverter.MethodCallDecorator("timeout") {
            @Override
            public Object decorateArgument(Object arg) {
                return Long.valueOf(arg.toString());
            }
        });
    }

    @Override
    public T convert(String id, S model) {
        return convert(model);
    }

    @Override
    public T convert(S model) {
        return delegate.convert((C) model.getEndpointConfiguration());
    }

    /**
     * Build Java configuration snippet for endpoints.
     * @param model
     * @param id
     * @param endpointType
     * @return
     */
    public String getJavaConfig(T model, String id, String endpointType) {
        StringBuilder builder = new StringBuilder();

        String methodName;
        if (StringUtils.hasText(id)) {
            Matcher matcher = invalidMethodNamePattern.matcher(id);
            if (matcher.find()) {
                methodName = StringUtils.trimAllWhitespace(id.replaceAll("\\.", "").replaceAll("-", ""));
                builder.append(String.format("%n\t@Bean(\"%s\")%n", id));
            } else {
                methodName = id;
                builder.append(String.format("%n\t@Bean%n"));
            }
        } else {
            methodName = StringUtils.uncapitalize(model.getClass().getSimpleName());
            builder.append(String.format("%n\t@Bean%n"));
        }

        builder.append(String.format("\tpublic %s %s() {%n", getSourceModelClass().getSimpleName(), methodName));

        builder.append(String.format("\t\treturn CitrusEndpoints.%s%n", endpointType));

        ReflectionUtils.doWithMethods(model.getClass(), method -> {
            try {
                Object object = method.invoke(model);
                if (object != null) {
                    String methodCall = getMethodCall(method.getName());
                    Optional<AbstractModelConverter.MethodCallDecorator> decorator = decorators.stream().filter(d -> d.supports(methodCall)).findAny();

                    if (decorator.isPresent()) {
                        if (decorator.get().allowMethodCall(object)) {
                            builder.append(decorator.get().decorate(String.format("\t\t\t.%s(%s)%n", decorator.get().decorateMethodName(), decorator.get().decorateArgument(object)), object));
                        }
                    } else if (object instanceof String) {
                        builder.append(String.format("\t\t\t.%s(\"%s\")%n", methodCall, object));
                    } else {
                        builder.append(String.format("\t\t\t.%s(%s)%n", methodCall, object));
                    }
                }
            } catch (InvocationTargetException e) {
                throw new ApplicationRuntimeException("Failed to access target model property", e);
            }
        }, method -> (method.getName().startsWith("get") || method.getName().startsWith("is"))
                && !method.getName().equals("getClass")
                && !method.getName().equals("getId")
                && method.getParameterCount() == 0);

        builder.append(String.format("\t\t\t.build();%n"));
        builder.append(String.format("\t}%n"));

        return builder.toString();
    }

    /**
     * Map setter method call based on getter method names.
     * @param methodName
     * @return
     */
    private String getMethodCall(String methodName) {
        if (methodName.startsWith("get")) {
            return StringUtils.uncapitalize(methodName.substring(3));
        } else if (methodName.startsWith("is")) {
            return StringUtils.uncapitalize(methodName.substring(2));
        }

        return methodName;
    }

    /**
     * Add method call decorator.
     * @param decorator
     */
    protected void addDecorator(AbstractModelConverter.MethodCallDecorator decorator) {
        decorators.add(decorator);
    }

    @Override
    public List<Class<?>> getAdditionalImports() {
        List<Class<?>> types = new ArrayList<>();
        types.add(getSourceModelClass());

        decorators.forEach(decorator -> types.addAll(decorator.getAdditionalImports()));
        return types;
    }

    @Override
    public Class<S> getSourceModelClass() {
        return sourceModelType;
    }

    @Override
    public Class<T> getTargetModelClass() {
        return delegate.getTargetModelClass();
    }
}
