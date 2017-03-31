package com.consol.citrus.admin.converter.model.endpoint;

import com.consol.citrus.mail.server.MailServer;
import com.consol.citrus.model.config.mail.MailServerModel;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component
public class MailServerModelConverter extends AbstractServerModelConverter<MailServerModel, MailServer> {

    /**
     * Default constructor.
     */
    public MailServerModelConverter() {
        super(MailServerModel.class, MailServer.class);
    }

    @Override
    public MailServerModel convert(String id, MailServer model) {
        MailServerModel converted = convert(model);
        converted.setId(id);
        return converted;
    }

    @Override
    protected String getEndpointType() {
        return "mail().server()";
    }

    @Override
    protected String getId(MailServerModel model) {
        return model.getId();
    }
}
