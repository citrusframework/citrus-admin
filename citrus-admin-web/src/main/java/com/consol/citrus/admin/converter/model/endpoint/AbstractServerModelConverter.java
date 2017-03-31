package com.consol.citrus.admin.converter.model.endpoint;

import com.consol.citrus.admin.converter.model.AbstractModelConverter;
import com.consol.citrus.endpoint.EndpointConfiguration;
import com.consol.citrus.server.Server;

/**
 * @author Christoph Deppisch
 * @since 2.7
 */
public abstract class AbstractServerModelConverter<T, S extends Server> extends AbstractModelConverter<T, S> {

    private final AbstractEndpointModelConverter<T, S, EndpointConfiguration> delegate;

    /**
     * Default constructor using source and target model types.
     *
     * @param targetModelType
     * @param sourceModelType
     */
    protected AbstractServerModelConverter(Class<T> targetModelType, Class<S> sourceModelType) {
        super(targetModelType, sourceModelType);

        this.delegate = new AbstractEndpointModelConverter<T, S, EndpointConfiguration>(targetModelType, sourceModelType, EndpointConfiguration.class) {
            @Override
            public String getJavaConfig(T model) {
                return getJavaConfig(model, getId(model), getEndpointType());
            }
        };
    }

    protected abstract String getEndpointType();

    protected abstract String getId(T model);

    @Override
    public String getJavaConfig(T model) {
        return delegate.getJavaConfig(model);
    }
}
