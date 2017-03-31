package com.consol.citrus.admin.converter.model.config;

import com.consol.citrus.admin.converter.model.AbstractModelConverter;
import com.consol.citrus.model.config.core.GlobalVariablesModel;
import com.consol.citrus.variable.GlobalVariables;
import org.springframework.stereotype.Component;

/**
 * @author Christoph Deppisch
 */
@Component
public class GlobalVariablesModelConverter extends AbstractModelConverter<GlobalVariablesModel, GlobalVariables> {
    /**
     * Default constructor.
     */
    public GlobalVariablesModelConverter() {
        super(GlobalVariablesModel.class, GlobalVariables.class);
    }

    @Override
    public GlobalVariablesModel convert(GlobalVariables model) {
        GlobalVariablesModel converted = super.convert(model);

        model.getVariables().forEach((key, value) -> {
            GlobalVariablesModel.Variable variable = new GlobalVariablesModel.Variable();
            variable.setName(key);
            variable.setValue(value.toString());
            converted.getVariables().add(variable);
        });

        return converted;
    }

    @Override
    public String getJavaConfig(GlobalVariablesModel model) {
        return getJavaConfig(model, "globalVariables");
    }
}
