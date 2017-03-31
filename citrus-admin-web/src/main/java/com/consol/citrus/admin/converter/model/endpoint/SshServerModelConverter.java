package com.consol.citrus.admin.converter.model.endpoint;

import com.consol.citrus.model.config.ssh.SshServerModel;
import com.consol.citrus.ssh.server.SshServer;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component
public class SshServerModelConverter extends AbstractServerModelConverter<SshServerModel, SshServer> {

    /**
     * Default constructor.
     */
    public SshServerModelConverter() {
        super(SshServerModel.class, SshServer.class);
    }

    @Override
    public SshServerModel convert(String id, SshServer model) {
        SshServerModel converted = convert(model);
        converted.setId(id);
        return converted;
    }

    @Override
    protected String getEndpointType() {
        return "ssh().server()";
    }

    @Override
    protected String getId(SshServerModel model) {
        return model.getId();
    }
}
