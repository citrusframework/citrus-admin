package com.consol.citrus.admin.converter.model.endpoint;

import com.consol.citrus.ftp.client.FtpClient;
import com.consol.citrus.ftp.client.FtpEndpointConfiguration;
import com.consol.citrus.model.config.ftp.FtpClientModel;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component
public class FtpClientModelConverter extends AbstractEndpointModelConverter<FtpClientModel, FtpClient, FtpEndpointConfiguration> {

    /**
     * Default constructor.
     */
    public FtpClientModelConverter() {
        super(FtpClientModel.class, FtpClient.class, FtpEndpointConfiguration.class);
    }

    @Override
    public FtpClientModel convert(String id, FtpClient model) {
        FtpClientModel converted = convert(model);
        converted.setId(id);
        return converted;
    }

    @Override
    public String getJavaConfig(FtpClientModel model) {
        return getJavaConfig(model, model.getId(), "ftp().client()");
    }
}
