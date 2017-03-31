package com.consol.citrus.admin.converter.model.endpoint;

import com.consol.citrus.model.config.ssh.SshClientModel;
import com.consol.citrus.ssh.client.SshClient;
import com.consol.citrus.ssh.client.SshEndpointConfiguration;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component
public class SshClientModelConverter extends AbstractEndpointModelConverter<SshClientModel, SshClient, SshEndpointConfiguration> {

    /**
     * Default constructor.
     */
    public SshClientModelConverter() {
        super(SshClientModel.class, SshClient.class, SshEndpointConfiguration.class);
    }

    @Override
    public SshClientModel convert(String id, SshClient model) {
        SshClientModel converted = convert(model);
        converted.setId(id);
        return converted;
    }

    @Override
    public String getJavaConfig(SshClientModel model) {
        return getJavaConfig(model, model.getId(), "ssh().client()");
    }
}
