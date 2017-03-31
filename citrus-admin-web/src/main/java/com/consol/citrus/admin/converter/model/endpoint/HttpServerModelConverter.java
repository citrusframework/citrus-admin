package com.consol.citrus.admin.converter.model.endpoint;

import com.consol.citrus.http.server.HttpServer;
import com.consol.citrus.model.config.http.HttpServerModel;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component
public class HttpServerModelConverter extends AbstractServerModelConverter<HttpServerModel, HttpServer> {

    /**
     * Default constructor.
     */
    public HttpServerModelConverter() {
        super(HttpServerModel.class, HttpServer.class);
    }

    @Override
    protected String getEndpointType() {
        return "http().server()";
    }

    @Override
    public HttpServerModel convert(String id, HttpServer model) {
        HttpServerModel converted = convert(model);
        converted.setId(id);
        return converted;
    }

    @Override
    protected String getId(HttpServerModel model) {
        return model.getId();
    }
}
