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

import com.consol.citrus.admin.model.*;
import com.consol.citrus.util.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Finds all tests case declarations in given source files. Provider is loading tests by their annotation presence of @RunWith JUnit annotation and Cucumber runner.
 *
 * @author Christoph Deppisch
 */
@Component
public class CucumberJUnit4TestProvider extends AbstractJavaTestProvider {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(CucumberJUnit4TestProvider.class);

    @Override
    protected List<Test> findTests(Project project, File sourceFile, String packageName, String className) {
        List<Test> tests = new ArrayList<>();

        try {
            String sourceCode = FileUtils.readToString(new FileSystemResource(sourceFile));
            Matcher matcher = Pattern.compile("@RunWith\\(Cucumber\\.class\\)").matcher(sourceCode);
            if (matcher.find()) {
                List<File> featureFiles = FileUtils.findFiles(project.getXmlDirectory() + packageName.replaceAll("\\.", File.separator), StringUtils.commaDelimitedListToSet("/**/*.feature"));

                for (File featureFile : featureFiles) {
                    Test test = new Test();
                    test.setType(TestType.CUCUMBER);
                    test.setClassName(className);
                    test.setPackageName(packageName);
                    test.setMethodName(FilenameUtils.getBaseName(featureFile.getName()));

                    test.setName(className + "." + test.getMethodName());

                    tests.add(test);
                }
            }
        } catch (IOException e) {
            log.error("Failed to read test source file", e);
        }

        return tests;
    }
}
