package com.consol.citrus.admin.converter.model.endpoint;

import com.consol.citrus.model.config.ws.WebServiceServerModel;
import com.consol.citrus.ws.server.WebServiceServer;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component
public class WebServiceServerModelConverter extends AbstractServerModelConverter<WebServiceServerModel, WebServiceServer> {

    /**
     * Default constructor.
     */
    public WebServiceServerModelConverter() {
        super(WebServiceServerModel.class, WebServiceServer.class);
    }

    @Override
    public WebServiceServerModel convert(String id, WebServiceServer model) {
        WebServiceServerModel converted = convert(model);
        converted.setId(id);
        return converted;
    }

    @Override
    protected String getEndpointType() {
        return "soap().server()";
    }

    @Override
    protected String getId(WebServiceServerModel model) {
        return model.getId();
    }
}
