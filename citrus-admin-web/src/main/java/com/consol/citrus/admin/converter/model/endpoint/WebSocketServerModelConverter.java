package com.consol.citrus.admin.converter.model.endpoint;

import com.consol.citrus.model.config.websocket.WebSocketServerModel;
import com.consol.citrus.websocket.server.WebSocketServer;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component
public class WebSocketServerModelConverter extends AbstractServerModelConverter<WebSocketServerModel, WebSocketServer> {

    /**
     * Default constructor.
     */
    public WebSocketServerModelConverter() {
        super(WebSocketServerModel.class, WebSocketServer.class);
    }

    @Override
    public WebSocketServerModel convert(String id, WebSocketServer model) {
        WebSocketServerModel converted = convert(model);
        converted.setId(id);
        return converted;
    }

    @Override
    protected String getEndpointType() {
        return "websocket().server()";
    }

    @Override
    protected String getId(WebSocketServerModel model) {
        return model.getId();
    }
}
