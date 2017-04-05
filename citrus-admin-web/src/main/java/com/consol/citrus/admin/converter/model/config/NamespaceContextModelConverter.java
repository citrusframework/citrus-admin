package com.consol.citrus.admin.converter.model.config;

import com.consol.citrus.admin.converter.model.AbstractModelConverter;
import com.consol.citrus.model.config.core.NamespaceContextModel;
import com.consol.citrus.xml.namespace.NamespaceContextBuilder;
import org.springframework.stereotype.Component;

import java.util.AbstractMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        addDecorator(new MethodCallDecorator("setNamespaces", "setNamespaceMappings") {
            @Override
            public Object decorateArgument(Object arg) {
                getAdditionalImports().add(AbstractMap.class);
                getAdditionalImports().add(Collectors.class);
                getAdditionalImports().add(Stream.class);

                List<NamespaceContextModel.Namespace> namespaces = (List<NamespaceContextModel.Namespace>) arg;
                StringBuilder codeBuilder = new StringBuilder();

                codeBuilder.append("Stream.of(");
                namespaces.forEach(namespace -> codeBuilder.append(String.format("%n\t\t\t\tnew AbstractMap.SimpleEntry<>(\"%s\", \"%s\"),", namespace.getPrefix(), namespace.getUri())));
                codeBuilder.deleteCharAt(codeBuilder.length() - 1);
                codeBuilder.append(String.format(")%n\t\t\t.collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue))"));

                return codeBuilder.toString();
            }

            @Override
            public boolean allowMethodCall(Object arg) {
                return ((List<NamespaceContextModel.Namespace>) arg).size() > 0;
            }
        });
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
