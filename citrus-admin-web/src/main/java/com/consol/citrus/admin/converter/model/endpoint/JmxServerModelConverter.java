package com.consol.citrus.admin.converter.model.endpoint;

import com.consol.citrus.jmx.server.JmxServer;
import com.consol.citrus.model.config.jmx.JmxServerModel;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component
public class JmxServerModelConverter extends AbstractServerModelConverter<JmxServerModel, JmxServer> {

    /**
     * Default constructor.
     */
    public JmxServerModelConverter() {
        super(JmxServerModel.class, JmxServer.class);
    }

    @Override
    public JmxServerModel convert(String id, JmxServer model) {
        JmxServerModel converted = convert(model);
        converted.setId(id);
        return converted;
    }

    @Override
    protected String getEndpointType() {
        return "jmx().server()";
    }

    @Override
    protected String getId(JmxServerModel model) {
        return model.getId();
    }
}
