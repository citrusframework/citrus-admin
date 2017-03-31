package com.consol.citrus.admin.converter.model.endpoint;

import com.consol.citrus.camel.endpoint.CamelEndpoint;
import com.consol.citrus.camel.endpoint.CamelEndpointConfiguration;
import com.consol.citrus.model.config.camel.CamelEndpointModel;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component
public class CamelEndpointModelConverter extends AbstractEndpointModelConverter<CamelEndpointModel, CamelEndpoint, CamelEndpointConfiguration> {

    /**
     * Default constructor.
     */
    public CamelEndpointModelConverter() {
        super(CamelEndpointModel.class, CamelEndpoint.class, CamelEndpointConfiguration.class);
    }

    @Override
    public CamelEndpointModel convert(String id, CamelEndpoint model) {
        CamelEndpointModel converted = convert(model);
        converted.setId(id);
        return converted;
    }

    @Override
    public String getJavaConfig(CamelEndpointModel model) {
        return getJavaConfig(model, model.getId(), "camel()");
    }
}
