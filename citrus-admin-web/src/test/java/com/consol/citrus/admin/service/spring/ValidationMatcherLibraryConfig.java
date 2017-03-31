package com.consol.citrus.admin.service.spring;

import com.consol.citrus.validation.matcher.ValidationMatcher;
import com.consol.citrus.validation.matcher.ValidationMatcherLibrary;
import com.consol.citrus.validation.matcher.core.StartsWithValidationMatcher;
import org.springframework.context.annotation.Bean;

/**
 * @author Christoph Deppisch
 */
public class ValidationMatcherLibraryConfig {

    @Bean
    public ValidationMatcherLibrary myMatcherLibrary() {
        ValidationMatcherLibrary matcherLibrary = new ValidationMatcherLibrary();

        matcherLibrary.setName("myMatcherLibrary");
        matcherLibrary.setPrefix("my:");
        matcherLibrary.getMembers().put("foo", fooValidationMatcher());

        return matcherLibrary;
    }

    private ValidationMatcher fooValidationMatcher() {
        return new StartsWithValidationMatcher();
    }
}
