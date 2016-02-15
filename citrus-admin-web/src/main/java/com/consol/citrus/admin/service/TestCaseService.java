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

package com.consol.citrus.admin.service;

import com.consol.citrus.admin.model.*;
import com.consol.citrus.util.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;

/**
 * @author Christoph Deppisch
 */
@Service
public class TestCaseService {

    /**
     * Lists all available Citrus test cases grouped in test packages.
     * @param project
     * @return
     */
    public List<TestPackage> getTestPackages(Project project) {
        Map<String, TestPackage> testPackages = new HashMap<>();
        List<Test> tests = new ArrayList<>();

        List<File> testFiles = FileUtils.getTestFiles(getTestDirectory(project));
        for (File file : testFiles) {
            String testName = FilenameUtils.getBaseName(file.getName());
            String testPackageName = file.getPath().substring(getTestDirectory(project).length(), file.getPath().length() - file.getName().length())
                    .replace(File.separatorChar, '.');

            if (testPackageName.endsWith(".")) {
                testPackageName = testPackageName.substring(0, testPackageName.length() - 1);
            }

            Test testCase = new Test();
            testCase.setType(TestType.XML);
            testCase.setName(testName);
            testCase.setPackageName(testPackageName);
            testCase.setFile(file.getParentFile().getAbsolutePath() + File.separator + FilenameUtils.getBaseName(file.getName()));
            testCase.setLastModified(file.lastModified());

            tests.add(testCase);
        }

        for (Test test : tests) {
            if (!testPackages.containsKey(test.getPackageName())) {
                TestPackage testPackage = new TestPackage();
                testPackage.setName(test.getPackageName());
                testPackages.put(test.getPackageName(), testPackage);
            }

            testPackages.get(test.getPackageName()).getTests().add(test);
        }

        return Arrays.asList(testPackages.values().toArray(new TestPackage[testPackages.size()]));
    }

    /**
     * Gets the current test directory based on project home and default test directory.
     * @return
     */
    private String getTestDirectory(Project project) {
        return new File(project.getProjectHome()).getAbsolutePath() + File.separator + project.getSrcDirectory() + "resources" + File.separator;
    }
}
