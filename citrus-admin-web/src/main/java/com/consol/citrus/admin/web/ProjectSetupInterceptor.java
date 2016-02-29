/*
 * Copyright 2006-2016 the original author or authors.
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

package com.consol.citrus.admin.web;

import com.consol.citrus.admin.service.ProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @author Christoph Deppisch
 */
public class ProjectSetupInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private ProjectService projectService;

    /** Location to redirect to in case project configuration is not set */
    private String redirect;

    /** Locations that get excluded from interceptor */
    private String[] excludes;

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(ProjectSetupInterceptor.class);

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (projectService.getActiveProject() == null
                && !request.getRequestURI().startsWith(request.getContextPath() + redirect)
                && !request.getRequestURI().startsWith(redirect)
                && !isExcluded(request.getRequestURI(), request.getContextPath())) {
            log.debug("Intercept " + request.getRequestURI() + " as project home is not set properly");
            log.debug("Redirecting to " + request.getContextPath() + redirect);
            response.sendRedirect(request.getContextPath() + redirect);
            return false;
        }

        return true;
    }

    /**
     * Checks whether request uri matches one of the excluded entries.
     * @param requestURI
     * @param contextPath
     * @return
     */
    private boolean isExcluded(String requestURI, String contextPath) {
        for (String exclude : excludes) {
            if (exclude.endsWith("*")) {
                if (requestURI.startsWith(contextPath + exclude.substring(0, exclude.length() - 1).trim())) {
                    return true;
                }
            } else {
                if (requestURI.equals(contextPath + exclude.trim())) {
                    return true;
                }
            }

        }

        return false;
    }

    /**
     * Gets the value of the redirect property.
     *
     * @return the redirect
     */
    public String getRedirect() {
        return redirect;
    }

    /**
     * Sets the redirect.
     * @param redirect the redirect to set
     */
    public void setRedirect(String redirect) {
        this.redirect = redirect;
    }

    /**
     * Gets the excludes.
     * @return the excludes the excludes to get.
     */
    public String[] getExcludes() {
        return Arrays.copyOf(excludes, excludes.length);
    }

    /**
     * Sets the excludes.
     * @param excludes the excludes to set
     */
    public void setExcludes(String[] excludes) {
        this.excludes = Arrays.copyOf(excludes, excludes.length);
    }

    /**
     * Sets the projectService property.
     *
     * @param projectService
     */
    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }
}
