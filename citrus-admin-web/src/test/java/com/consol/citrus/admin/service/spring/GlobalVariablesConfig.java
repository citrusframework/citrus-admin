package com.consol.citrus.admin.service.spring;

import com.consol.citrus.variable.GlobalVariables;
import org.springframework.context.annotation.Bean;

/**
 * @author Christoph Deppisch
 */
public class GlobalVariablesConfig {

    @Bean
    public GlobalVariables globalVariables() {
        GlobalVariables globalVariables = new GlobalVariables();

        globalVariables.getVariables().put("foo", "globalFoo");
        globalVariables.getVariables().put("bar", "globalBar");

        return globalVariables;
    }
}
