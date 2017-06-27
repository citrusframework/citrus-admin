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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(Application.class);

    /** Application version */
    private static String version;

    private static final String CITRUS_ADMIN_PREFIX = "citrus.admin.";
    private static final String CITRUS_ADMIN_ENV_PREFIX = "CITRUS_ADMIN_";

    /** System property names */
    public static final String PROJECT_HOME = CITRUS_ADMIN_PREFIX + "project.home";
    public static final String PROJECT_HOME_ENV = CITRUS_ADMIN_ENV_PREFIX + "PROJECT_HOME";
    public static final String ROOT_DIRECTORY = CITRUS_ADMIN_PREFIX + "root.directory";
    public static final String ROOT_DIRECTORY_ENV = CITRUS_ADMIN_ENV_PREFIX + "ROOT_DIRECTORY";
    public static final String WORKING_DIRECTORY = CITRUS_ADMIN_PREFIX + "working.directory";
    public static final String WORKING_DIRECTORY_ENV = CITRUS_ADMIN_ENV_PREFIX + "WORKING_DIRECTORY";

    /** Git repository to load on startup */
    public static final String PROJECT_REPOSITORY = CITRUS_ADMIN_PREFIX + "project.repository";
    public static final String PROJECT_REPOSITORY_ENV = CITRUS_ADMIN_ENV_PREFIX + "PROJECT_REPOSITORY";
    public static final String PROJECT_REPOSITORY_BRANCH = CITRUS_ADMIN_PREFIX + "project.repository.branch";
    public static final String PROJECT_REPOSITORY_BRANCH_ENV = CITRUS_ADMIN_ENV_PREFIX + "PROJECT_REPOSITORY_BRANCH";
    public static final String PROJECT_REPOSITORY_MODULE = CITRUS_ADMIN_PREFIX + "project.repository.module";
    public static final String PROJECT_REPOSITORY_MODULE_ENV = CITRUS_ADMIN_ENV_PREFIX + "PROJECT_REPOSITORY_MODULE";

    /** Base package for test cases to look for */
    public static final String BASE_PACKAGE = CITRUS_ADMIN_PREFIX + "test.base.package";
    public static final String BASE_PACKAGE_ENV = CITRUS_ADMIN_ENV_PREFIX + "TEST_BASE_PACKAGE";

    /** Spring application context file */
    public static final String SPRING_APPLICATION_CONTEXT = CITRUS_ADMIN_PREFIX + "spring.application.context";
    public static final String SPRING_APPLICATION_CONTEXT_ENV = CITRUS_ADMIN_ENV_PREFIX + "SPRING_APPLICATION_CONTEXT";
    public static final String SPRING_JAVA_CONFIG = CITRUS_ADMIN_PREFIX + "spring.java.config";
    public static final String SPRING_JAVA_CONFIG_ENV = CITRUS_ADMIN_ENV_PREFIX + "SPRING_JAVA_CONFIG";

    /** Source directory */
    public static final String JAVA_SRC_DIRECTORY = CITRUS_ADMIN_PREFIX + "java.source.directory";
    public static final String JAVA_SRC_DIRECTORY_ENV = CITRUS_ADMIN_ENV_PREFIX + "JAVA_SOURCE_DIRECTORY";
    public static final String XML_SRC_DIRECTORY = CITRUS_ADMIN_PREFIX + "xml.source.directory";
    public static final String XML_SRC_DIRECTORY_ENV = CITRUS_ADMIN_ENV_PREFIX + "XML_SOURCE_DIRECTORY";

    public static final String MVN_HOME_DIRECTORY = "maven.home.directory";

    /* Load application version */
    static {
        try (final InputStream in = new ClassPathResource("META-INF/app.version").getInputStream()) {
            Properties versionProperties = new Properties();
            versionProperties.load(in);
            version = versionProperties.get("app.version").toString();
        } catch (IOException e) {
            log.warn("Unable to read application version information", e);
            version = "";
        }
    }

    /**
     * Gets the application version.
     *
     * @return
     */
    public static String getVersion() {
        return version;
    }

    /**
     * Gets the root directory from system property. By default user.home system
     * property setting is used as root.
     * @return
     */
    public static String getRootDirectory() {
        return System.getProperty(ROOT_DIRECTORY, System.getenv(ROOT_DIRECTORY_ENV) != null ? System.getenv(ROOT_DIRECTORY_ENV) : System.getProperty("user.home"));
    }

    /**
     * Gets the working directory from system property. By default this is the root directory.
     * @return
     */
    public static String getWorkingDirectory() {
        return System.getProperty(WORKING_DIRECTORY, System.getenv(WORKING_DIRECTORY_ENV) != null ? System.getenv(WORKING_DIRECTORY_ENV) : getRootDirectory());
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(Application.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}