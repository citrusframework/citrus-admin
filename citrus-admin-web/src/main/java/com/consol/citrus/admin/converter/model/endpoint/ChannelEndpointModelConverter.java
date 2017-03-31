package com.consol.citrus.admin.converter.model.endpoint;

import com.consol.citrus.channel.ChannelEndpoint;
import com.consol.citrus.channel.ChannelEndpointConfiguration;
import com.consol.citrus.model.config.core.ChannelEndpointModel;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component
public class ChannelEndpointModelConverter extends AbstractEndpointModelConverter<ChannelEndpointModel, ChannelEndpoint, ChannelEndpointConfiguration> {

    /**
     * Default constructor.
     */
    public ChannelEndpointModelConverter() {
        super(ChannelEndpointModel.class, ChannelEndpoint.class, ChannelEndpointConfiguration.class);
    }

    @Override
    public ChannelEndpointModel convert(String id, ChannelEndpoint model) {
        ChannelEndpointModel converted = convert(model);
        converted.setId(id);
        return converted;
    }

    @Override
    public String getJavaConfig(ChannelEndpointModel model) {
        return getJavaConfig(model, model.getId(), "channel().asynchronous()");
    }
}
