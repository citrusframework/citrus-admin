package com.consol.citrus.admin.converter.model.endpoint;

import com.consol.citrus.model.config.websocket.WebSocketClientModel;
import com.consol.citrus.websocket.client.WebSocketClient;
import com.consol.citrus.websocket.endpoint.WebSocketEndpointConfiguration;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component
public class WebSocketClientModelConverter extends AbstractEndpointModelConverter<WebSocketClientModel, WebSocketClient, WebSocketEndpointConfiguration> {

    /**
     * Default constructor.
     */
    public WebSocketClientModelConverter() {
        super(WebSocketClientModel.class, WebSocketClient.class, WebSocketEndpointConfiguration.class);
    }
    @Override
    public WebSocketClientModel convert(String id, WebSocketClient model) {
        WebSocketClientModel converted = convert(model);
        converted.setId(id);
        return converted;
    }

    @Override
    public String getJavaConfig(WebSocketClientModel model) {
        return getJavaConfig(model, model.getId(), "websocket().client()");
    }
}
