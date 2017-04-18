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

package com.consol.citrus.admin;

import com.consol.citrus.admin.web.ProjectSetupInterceptor;
import org.springframework.boot.autoconfigure.web.ErrorViewResolver;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;

/**
 * @author Christoph Deppisch
 */
@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {

    @Bean
    public ErrorViewResolver supportPathBasedLocationStrategyWithoutHashes() {
        return (HttpServletRequest req, HttpStatus status, Map<String, Object> model) -> status == HttpStatus.NOT_FOUND
                ? new ModelAndView("index.html", Collections.emptyMap(), HttpStatus.OK)
                : null;
    }

    @Bean
    public ProjectSetupInterceptor getProjectSetupInterceptor() {
        ProjectSetupInterceptor interceptor = new ProjectSetupInterceptor();
        interceptor.setRedirect("/setup");
        interceptor.setExcludes(new String[]{
                "/assets/*",
                "/api/*",
                "/templates/*",
                "/error"
        });
        return interceptor;
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/setup").setViewName("forward:/index.html");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getProjectSetupInterceptor());
    }
}
