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

package com.consol.citrus.admin.service.test;

import com.consol.citrus.admin.model.Project;
import com.consol.citrus.admin.model.Test;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christoph Deppisch
 */
public abstract class AbstractJavaTestProvider implements TestProvider {

    @Override
    public List<Test> findTests(Project project, List<File> sourceFiles) {
        List<Test> tests = new ArrayList<>();
        for (File sourceFile : sourceFiles) {
            String className = FilenameUtils.getBaseName(sourceFile.getName());
            String testPackageName = sourceFile.getPath().substring(project.getJavaDirectory().length(), sourceFile.getPath().length() - sourceFile.getName().length())
                    .replace(File.separatorChar, '.');

            if (testPackageName.endsWith(".")) {
                testPackageName = testPackageName.substring(0, testPackageName.length() - 1);
            }

            tests.addAll(findTests(project, sourceFile, testPackageName, className));
        }

        return tests;
    }

    /**
     * Find all tests in give source file. Method is finding tests by their annotation presence of @CitrusTest or @CitrusXmlTest.
     * @param sourceFile
     * @param packageName
     * @param className
     * @return
     */
    protected abstract List<Test> findTests(Project project, File sourceFile, String packageName, String className);
}
