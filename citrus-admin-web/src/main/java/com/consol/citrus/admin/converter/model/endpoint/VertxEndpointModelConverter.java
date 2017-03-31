package com.consol.citrus.admin.converter.model.endpoint;

import com.consol.citrus.model.config.vertx.VertxEndpointModel;
import com.consol.citrus.vertx.endpoint.VertxEndpoint;
import com.consol.citrus.vertx.endpoint.VertxEndpointConfiguration;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component
public class VertxEndpointModelConverter extends AbstractEndpointModelConverter<VertxEndpointModel, VertxEndpoint, VertxEndpointConfiguration> {

    /**
     * Default constructor.
     */
    public VertxEndpointModelConverter() {
        super(VertxEndpointModel.class, VertxEndpoint.class, VertxEndpointConfiguration.class);
    }

    @Override
    public VertxEndpointModel convert(String id, VertxEndpoint model) {
        VertxEndpointModel converted = convert(model);
        converted.setId(id);
        return converted;
    }

    @Override
    public String getJavaConfig(VertxEndpointModel model) {
        return getJavaConfig(model, model.getId(), "vertx()");
    }
}
