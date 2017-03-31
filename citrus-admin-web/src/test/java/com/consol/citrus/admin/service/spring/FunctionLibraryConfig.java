package com.consol.citrus.admin.service.spring;

import com.consol.citrus.functions.Function;
import com.consol.citrus.functions.FunctionLibrary;
import com.consol.citrus.functions.core.RandomNumberFunction;
import org.springframework.context.annotation.Bean;

/**
 * @author Christoph Deppisch
 */
public class FunctionLibraryConfig {

    @Bean
    public FunctionLibrary myFunctionLibrary() {
        FunctionLibrary functionLibrary = new FunctionLibrary();

        functionLibrary.setName("myFunctionLibrary");
        functionLibrary.setPrefix("my:");
        functionLibrary.getMembers().put("foo", fooFunction());

        return functionLibrary;
    }

    private Function fooFunction() {
        return new RandomNumberFunction();
    }
}
