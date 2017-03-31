package com.consol.citrus.admin.converter.model.endpoint;

import com.consol.citrus.ftp.server.FtpServer;
import com.consol.citrus.model.config.ftp.FtpServerModel;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component
public class FtpServerModelConverter extends AbstractServerModelConverter<FtpServerModel, FtpServer> {

    /**
     * Default constructor.
     */
    public FtpServerModelConverter() {
        super(FtpServerModel.class, FtpServer.class);
    }

    @Override
    public FtpServerModel convert(String id, FtpServer model) {
        FtpServerModel converted = convert(model);
        converted.setId(id);
        return converted;
    }

    @Override
    protected String getEndpointType() {
        return "ftp().server()";
    }

    @Override
    protected String getId(FtpServerModel model) {
        return model.getId();
    }
}
