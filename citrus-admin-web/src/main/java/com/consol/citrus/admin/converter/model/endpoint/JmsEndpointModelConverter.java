package com.consol.citrus.admin.converter.model.endpoint;

import com.consol.citrus.admin.converter.model.AbstractModelConverter;
import com.consol.citrus.jms.endpoint.JmsEndpoint;
import com.consol.citrus.jms.endpoint.JmsEndpointConfiguration;
import com.consol.citrus.model.config.jms.JmsEndpointModel;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component
public class JmsEndpointModelConverter extends AbstractEndpointModelConverter<JmsEndpointModel, JmsEndpoint, JmsEndpointConfiguration> {

    /**
     * Default constructor.
     */
    public JmsEndpointModelConverter() {
        super(JmsEndpointModel.class, JmsEndpoint.class, JmsEndpointConfiguration.class);
        addDecorator(new AbstractModelConverter.MethodCallDecorator("destinationName", "destination"));
        addDecorator(new AbstractModelConverter.MethodCallDecorator("isPubSubDomain", "pubSubDomain"));
    }

    @Override
    public JmsEndpointModel convert(String id, JmsEndpoint model) {
        JmsEndpointModel converted = convert(model);
        converted.setId(id);
        return converted;
    }

    @Override
    public String getJavaConfig(JmsEndpointModel model) {
        return getJavaConfig(model, model.getId(), "jms().asynchronous()");
    }
}
