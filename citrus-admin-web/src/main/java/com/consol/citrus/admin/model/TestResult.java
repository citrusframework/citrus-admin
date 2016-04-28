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

package com.consol.citrus.admin.model;

/**
 * @author Christoph Deppisch
 */
public class TestResult {

    private Test testCase;
    private boolean success;
    private String stackTrace;
    private String failureStack;

    private String processId;

    /**
     * @return
     */
    public Test getTestCase() {
        return testCase;
    }

    /**
     * @param value
     */
    public void setTestCase(Test value) {
        this.testCase = value;
    }

    /**
     * @return
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @param value
     */
    public void setSuccess(boolean value) {
        this.success = value;
    }

    /**
     * @return
     */
    public String getStackTrace() {
        return stackTrace;
    }

    /**
     * @param value
     */
    public void setStackTrace(String value) {
        this.stackTrace = value;
    }

    /**
     * @return
     */
    public String getFailureStack() {
        return failureStack;
    }

    /**
     * @param value
     */
    public void setFailureStack(String value) {
        this.failureStack = value;
    }

    /**
     * Sets the processId property.
     *
     * @param processId
     */
    public void setProcessId(String processId) {
        this.processId = processId;
    }

    /**
     * Gets the value of the processId property.
     *
     * @return the processId
     */
    public String getProcessId() {
        return processId;
    }
}
