package com.consol.citrus.admin.converter.model.endpoint;

import com.consol.citrus.mail.client.MailClient;
import com.consol.citrus.mail.client.MailEndpointConfiguration;
import com.consol.citrus.model.config.mail.MailClientModel;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component
public class MailClientModelConverter extends AbstractEndpointModelConverter<MailClientModel, MailClient, MailEndpointConfiguration> {

    /**
     * Default constructor.
     */
    public MailClientModelConverter() {
        super(MailClientModel.class, MailClient.class, MailEndpointConfiguration.class);
    }

    @Override
    public MailClientModel convert(String id, MailClient model) {
        MailClientModel converted = convert(model);
        converted.setId(id);
        return converted;
    }

    @Override
    public String getJavaConfig(MailClientModel model) {
        return getJavaConfig(model, model.getId(), "mail().client()");
    }
}
