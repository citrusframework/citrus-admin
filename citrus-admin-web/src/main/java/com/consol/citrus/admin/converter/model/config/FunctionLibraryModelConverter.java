package com.consol.citrus.admin.converter.model.config;

import com.consol.citrus.admin.converter.model.AbstractModelConverter;
import com.consol.citrus.functions.Function;
import com.consol.citrus.functions.FunctionLibrary;
import com.consol.citrus.model.config.core.FunctionLibraryModel;
import org.springframework.stereotype.Component;

import java.util.AbstractMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Christoph Deppisch
 */
@Component
public class FunctionLibraryModelConverter extends AbstractModelConverter<FunctionLibraryModel, FunctionLibrary> {
    /**
     * Default constructor.
     */
    public FunctionLibraryModelConverter() {
        super(FunctionLibraryModel.class, FunctionLibrary.class);

        addDecorator(new MethodCallDecorator("setFunctions", "setMembers") {
            @Override
            public Object decorateArgument(Object arg) {
                getAdditionalImports().add(AbstractMap.class);
                getAdditionalImports().add(Collectors.class);
                getAdditionalImports().add(Stream.class);
                getAdditionalImports().add(Function.class);

                List<FunctionLibraryModel.Function> functions = (List<FunctionLibraryModel.Function>) arg;
                StringBuilder codeBuilder = new StringBuilder();

                codeBuilder.append("Stream.of(");
                functions.forEach(function -> codeBuilder.append(String.format("%n\t\t\t\tnew AbstractMap.SimpleEntry<String, Function>(\"%s\", new %s()),", function.getName(), function.getClazz())));
                codeBuilder.deleteCharAt(codeBuilder.length() - 1);
                codeBuilder.append(String.format(")%n\t\t\t.collect(Collectors.toMap(AbstractMap.SimpleEntry::getKey, AbstractMap.SimpleEntry::getValue))"));

                return codeBuilder.toString();
            }

            @Override
            public boolean allowMethodCall(Object arg) {
                return ((List<FunctionLibraryModel.Function>) arg).size() > 0;
            }
        });
    }

    @Override
    public FunctionLibraryModel convert(String id, FunctionLibrary model) {
        FunctionLibraryModel converted = convert(model);
        converted.setId(id);

        model.getMembers().forEach((key, value) -> {
            FunctionLibraryModel.Function function = new FunctionLibraryModel.Function();

            function.setName(key);
            function.setClazz(value.getClass().getName());
            
            converted.getFunctions().add(function);
        });

        return converted;
    }

    @Override
    public String getJavaConfig(FunctionLibraryModel model) {
        return getJavaConfig(model, model.getId());
    }
}
