package com.consol.citrus.admin.converter.model.endpoint;

import com.consol.citrus.model.config.rmi.RmiServerModel;
import com.consol.citrus.rmi.server.RmiServer;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component
public class RmiServerModelConverter extends AbstractServerModelConverter<RmiServerModel, RmiServer> {

    /**
     * Default constructor.
     */
    public RmiServerModelConverter() {
        super(RmiServerModel.class, RmiServer.class);
    }

    @Override
    public RmiServerModel convert(String id, RmiServer model) {
        RmiServerModel converted = convert(model);
        converted.setId(id);
        return converted;
    }

    @Override
    protected String getEndpointType() {
        return "rmi().server()";
    }

    @Override
    protected String getId(RmiServerModel model) {
        return model.getId();
    }
}
