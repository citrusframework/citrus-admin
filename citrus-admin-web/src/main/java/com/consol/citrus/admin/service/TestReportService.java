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

package com.consol.citrus.admin.service;

import com.consol.citrus.admin.model.*;
import com.consol.citrus.admin.service.report.TestReportLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author Christoph Deppisch
 */
@Service
public class TestReportService {

    @Autowired
    private List<TestReportLoader> testReportLoaders;

    @PostConstruct
    public void init() {
        testReportLoaders.sort(AnnotationAwareOrderComparator.INSTANCE);
    }

    /**
     * Loads latest test results and creates report.
     * @return
     */
    public TestReport getLatest(Project activeProject) {
        if (null != activeProject) {
            return testReportLoaders.stream()
                    .filter(reporter -> reporter.hasTestResults(activeProject))
                    .map(reporter -> reporter.getLatest(activeProject))
                    .findFirst()
                    .orElse(new TestReport());
        }

        return new TestReport();
    }

    /**
     * Loads latest test result for given test.
     * @param activeProject
     * @param test
     * @return
     */
    public TestReport getLatest(Project activeProject, Test test) {
        if (null != activeProject) {
            return testReportLoaders.stream()
                    .filter(reporter -> reporter.hasTestResults(activeProject))
                    .map(reporter -> reporter.getLatest(activeProject, test))
                    .findFirst()
                    .orElse(new TestReport());
        }

        return new TestReport();
    }


}
