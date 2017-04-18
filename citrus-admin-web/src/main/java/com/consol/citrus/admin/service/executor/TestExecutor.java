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

package com.consol.citrus.admin.service.executor;

import com.consol.citrus.admin.model.*;

/**
 * @author Christoph Deppisch
 */
public interface TestExecutor {

    /**
     * Run all tests and throw exception when failed.
     * @param project
     * @return the processId
     * @throws Exception
     */
    String execute(Project project) throws Exception;

    /**
     * Run test group and throw exception when failed.
     * @param project
     * @param group
     * @return the processId
     * @throws Exception
     */
    String execute(Project project, TestGroup group) throws Exception;

    /**
     * Run test and throw exception when failed.
     * @param project
     * @param test
     * @return the processId
     * @throws Exception
     */
    String execute(Project project, Test test) throws Exception;
}
