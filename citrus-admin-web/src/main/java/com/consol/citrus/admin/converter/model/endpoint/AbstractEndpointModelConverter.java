package com.consol.citrus.admin.converter.model.endpoint;

import com.consol.citrus.admin.converter.model.AbstractModelConverter;
import com.consol.citrus.admin.converter.model.ModelConverter;
import com.consol.citrus.admin.exception.ApplicationRuntimeException;
import com.consol.citrus.endpoint.Endpoint;
import com.consol.citrus.endpoint.EndpointConfiguration;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Christoph Deppisch
 */
public abstract class AbstractEndpointModelConverter<T, S extends Endpoint, C extends EndpointConfiguration> implements ModelConverter<T, S> {

    private Pattern invalidMethodNamePattern = Pattern.compile("[\\s\\.-]");

    private final Class<S> sourceModelType;

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
                builder.append(String.format("\t@Bean(\"%s\")%n", id));
            } else {
                methodName = id;
                builder.append(String.format("\t@Bean%n"));
            }
        } else {
            methodName = StringUtils.uncapitalize(model.getClass().getSimpleName());
            builder.append(String.format("\t@Bean%n"));
        }

        builder.append(String.format("\tpublic %s %s() {%n", getTargetModelClass().getSimpleName(), methodName));

        builder.append(String.format("\t\treturn CitrusEndpoints.%s%n", endpointType));

        ReflectionUtils.doWithMethods(model.getClass(), method -> {
            try {
                Object object = method.invoke(model);
                if (object != null) {
                    if (object instanceof String) {
                        builder.append(String.format("\t\t\t.%s(\"%s\")%n", StringUtils.uncapitalize(method.getName().replaceAll("get", "")), object));
                    } else {
                        builder.append(String.format("\t\t\t.%s(%s)%n", StringUtils.uncapitalize(method.getName().replaceAll("get", "")), object));
                    }
                }
            } catch (InvocationTargetException e) {
                throw new ApplicationRuntimeException("Failed to access target model property", e);
            }
        }, method -> method.getName().startsWith("get") && method.getParameterCount() == 0);

        builder.append(String.format("\t\t\t.build();%n"));
        builder.append(String.format("\t}%n"));

        return builder.toString();
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
