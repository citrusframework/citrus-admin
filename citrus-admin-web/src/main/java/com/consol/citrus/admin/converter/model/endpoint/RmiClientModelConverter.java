package com.consol.citrus.admin.converter.model.endpoint;

import com.consol.citrus.model.config.rmi.RmiClientModel;
import com.consol.citrus.rmi.client.RmiClient;
import com.consol.citrus.rmi.endpoint.RmiEndpointConfiguration;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component
public class RmiClientModelConverter extends AbstractEndpointModelConverter<RmiClientModel, RmiClient, RmiEndpointConfiguration> {

    /**
     * Default constructor.
     */
    public RmiClientModelConverter() {
        super(RmiClientModel.class, RmiClient.class, RmiEndpointConfiguration.class);
    }

    @Override
    public RmiClientModel convert(String id, RmiClient model) {
        RmiClientModel converted = convert(model);
        converted.setId(id);
        return converted;
    }

    @Override
    public String getJavaConfig(RmiClientModel model) {
        return getJavaConfig(model, model.getId(), "rmi().client()");
    }
}
