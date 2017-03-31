package com.consol.citrus.admin.converter.model.endpoint;

import com.consol.citrus.model.config.ws.WebServiceClientModel;
import com.consol.citrus.ws.client.WebServiceClient;
import com.consol.citrus.ws.client.WebServiceEndpointConfiguration;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component
public class WebServiceClientModelConverter extends AbstractEndpointModelConverter<WebServiceClientModel, WebServiceClient, WebServiceEndpointConfiguration> {

    /**
     * Default constructor.
     */
    public WebServiceClientModelConverter() {
        super(WebServiceClientModel.class, WebServiceClient.class, WebServiceEndpointConfiguration.class);
    }

    @Override
    public WebServiceClientModel convert(String id, WebServiceClient model) {
        WebServiceClientModel converted = convert(model);
        converted.setId(id);
        return converted;
    }

    @Override
    public String getJavaConfig(WebServiceClientModel model) {
        return getJavaConfig(model, model.getId(), "soap().client()");
    }
}
