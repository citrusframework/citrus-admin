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

package com.consol.citrus.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	/** System property names */
	public static final String PROJECT_HOME = "project.home";
	public static final String ROOT_DIRECTORY = "root.directory";

	/** Base package for test cases to look for */
	public static final String BASE_PACKAGE = "test.base.package";

	/** Test source directory */
	public static final String TEST_SRC_DIRECTORY = "test.source.directory";

	/**
	 * Gets the root directory from system property. By default user.home system
	 * property setting is used as root.
	 * @return
	 */
	public static String getRootDirectory() {
		return System.getProperty(ROOT_DIRECTORY, System.getProperty("user.home"));
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}