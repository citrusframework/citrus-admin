/*
 * Copyright 2006-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
