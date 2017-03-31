package com.consol.citrus.admin.converter.model.config;

import com.consol.citrus.admin.converter.model.AbstractModelConverter;
import com.consol.citrus.model.config.core.NamespaceContextModel;
import com.consol.citrus.xml.namespace.NamespaceContextBuilder;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component
public class NamespaceContextModelConverter extends AbstractModelConverter<NamespaceContextModel, NamespaceContextBuilder> {
    /**
     * Default constructor.
     */
    public NamespaceContextModelConverter() {
        super(NamespaceContextModel.class, NamespaceContextBuilder.class);
    }

    @Override
    public NamespaceContextModel convert(NamespaceContextBuilder model) {
        NamespaceContextModel converted = super.convert(model);

        model.getNamespaceMappings().forEach((key, value) -> {
            NamespaceContextModel.Namespace namespace = new NamespaceContextModel.Namespace();
            namespace.setPrefix(key);
            namespace.setUri(value);
            converted.getNamespaces().add(namespace);
        });

        return converted;
    }

    @Override
    public String getJavaConfig(NamespaceContextModel model) {
        return getJavaConfig(model, "namespaceContext");
    }
}
