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

package com.consol.citrus.admin.process.listener;

/**
 * @author Christoph Deppisch
 */
public abstract class AbstractProcessListener implements ProcessListener {
    @Override
    public void onProcessStart(String processId) {
    }

    @Override
    public void onProcessSuccess(String processId) {
    }

    @Override
    public void onProcessFail(String processId, int exitCode) {
    }

    @Override
    public void onProcessFail(String processId, Throwable e) {
    }

    @Override
    public void onProcessOutput(String processId, String output) {
    }

    @Override
    public void onProcessActivity(String processId, String output) {
    }
}
