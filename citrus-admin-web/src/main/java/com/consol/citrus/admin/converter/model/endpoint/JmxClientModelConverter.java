package com.consol.citrus.admin.converter.model.endpoint;

import com.consol.citrus.jmx.client.JmxClient;
import com.consol.citrus.jmx.endpoint.JmxEndpointConfiguration;
import com.consol.citrus.model.config.jmx.JmxClientModel;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component
public class JmxClientModelConverter extends AbstractEndpointModelConverter<JmxClientModel, JmxClient, JmxEndpointConfiguration> {

    /**
     * Default constructor.
     */
    public JmxClientModelConverter() {
        super(JmxClientModel.class, JmxClient.class, JmxEndpointConfiguration.class);
    }

    @Override
    public JmxClientModel convert(String id, JmxClient model) {
        JmxClientModel converted = convert(model);
        converted.setId(id);
        return converted;
    }

    @Override
    public String getJavaConfig(JmxClientModel model) {
        return getJavaConfig(model, model.getId(), "jmx().client()");
    }
}
