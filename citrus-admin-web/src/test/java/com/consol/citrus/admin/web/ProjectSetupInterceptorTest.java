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

import com.consol.citrus.admin.WebConfig;
import com.consol.citrus.admin.model.Project;
import com.consol.citrus.admin.service.ProjectService;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.Assert;
import org.testng.annotations.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.*;

/**
 * @author Christoph Deppisch
 */
public class ProjectSetupInterceptorTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private ProjectService projectService;

    @BeforeClass
    public void setup () {
        MockitoAnnotations.initMocks(this);
    }

    @Test(dataProvider = "requests")
    public void testPreHandle(String requestUri, String contextPath, Project activeProject, boolean shouldRedirect) throws Exception {
        ProjectSetupInterceptor interceptor = new WebConfig().getProjectSetupInterceptor();
        interceptor.setProjectService(projectService);

        reset(request, response);

        when(projectService.getActiveProject()).thenReturn(activeProject);
        when(request.getRequestURI()).thenReturn(requestUri);
        when(request.getContextPath()).thenReturn(contextPath);

        boolean continueHandle = interceptor.preHandle(request, response, null);

        Assert.assertEquals(continueHandle, !shouldRedirect);

        if (shouldRedirect) {
            verify(response).sendRedirect(interceptor.getRedirect());
        }
    }

    @DataProvider
    public Object[][] requests() {
        Project activeProject = new Project("/home");

        return new Object[][] {
            new Object[] {"/setup", "", null, false},
            new Object[] {"/static/icons", "", null, false},
            new Object[] {"/static/images", "", null, false},
            new Object[] {"/templates/setup", "", null, false},
            new Object[] {"/templates/file-browser.html", "", null, false},
            new Object[] {"/file/browse", "", null, false},
            new Object[] {"/project", "", null, false},
            new Object[] {"/foo", "", null, true},
            new Object[] {"/foo/bar", "", null, true},
            new Object[] {"/setup", "", activeProject, false},
            new Object[] {"/static/icons", "", activeProject, false},
            new Object[] {"/static/images", "", activeProject, false},
            new Object[] {"/templates/setup", "", activeProject, false},
            new Object[] {"/templates/file-browser.html", "", activeProject, false},
            new Object[] {"/file/browse", "", activeProject, false},
            new Object[] {"/project", "", activeProject, false},
            new Object[] {"/foo", "", activeProject, false},
            new Object[] {"/foo/bar", "", activeProject, false}
        };
    }
}