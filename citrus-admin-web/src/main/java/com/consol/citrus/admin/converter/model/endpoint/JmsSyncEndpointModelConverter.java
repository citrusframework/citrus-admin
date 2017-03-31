package com.consol.citrus.admin.converter.model.endpoint;

import com.consol.citrus.jms.endpoint.JmsSyncEndpoint;
import com.consol.citrus.jms.endpoint.JmsSyncEndpointConfiguration;
import com.consol.citrus.model.config.jms.JmsSyncEndpointModel;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component
public class JmsSyncEndpointModelConverter extends AbstractEndpointModelConverter<JmsSyncEndpointModel, JmsSyncEndpoint, JmsSyncEndpointConfiguration> {

    /**
     * Default constructor.
     */
    public JmsSyncEndpointModelConverter() {
        super(JmsSyncEndpointModel.class, JmsSyncEndpoint.class, JmsSyncEndpointConfiguration.class);
    }

    @Override
    public JmsSyncEndpointModel convert(String id, JmsSyncEndpoint model) {
        JmsSyncEndpointModel converted = convert(model);
        converted.setId(id);
        return converted;
    }

    @Override
    public String getJavaConfig(JmsSyncEndpointModel model) {
        return getJavaConfig(model, model.getId(), "jms().synchronous()");
    }
}
