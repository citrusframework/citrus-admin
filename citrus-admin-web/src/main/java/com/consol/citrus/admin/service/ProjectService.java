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

import com.consol.citrus.Citrus;
import com.consol.citrus.admin.Application;
import com.consol.citrus.admin.exception.ApplicationRuntimeException;
import com.consol.citrus.admin.model.Project;
import com.consol.citrus.admin.model.ProjectSettings;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.util.FileUtils;
import com.consol.citrus.util.XMLUtils;
import com.consol.citrus.xml.xpath.XPathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.SimpleNamespaceContext;
import org.w3c.dom.Document;

import javax.annotation.PostConstruct;
import javax.xml.xpath.XPathConstants;
import java.io.*;
import java.util.Properties;

/**
 * @author Christoph Deppisch
 */
@Service
public class ProjectService {

    @Autowired
    protected FileBrowserService fileBrowserService;

    /** Current project actively opened in Citrus admin */
    private Project project;

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(ProjectService.class);

    @PostConstruct
    public void loadDefaultProject() throws Exception {
        if (project == null && StringUtils.hasText(System.getProperty(Application.PROJECT_HOME))) {
            load(System.getProperty(Application.PROJECT_HOME));
        }
    }

    /**
     * Loads Citrus project from project home.
     * @param projectHomeDir
     * @return
     */
    public void load(String projectHomeDir) {
        if (!validateProjectHome(projectHomeDir)) {
            throw new ApplicationRuntimeException("Invalid project home - not a proper Citrus project");
        }

        System.setProperty(Application.PROJECT_HOME, projectHomeDir);
        project = new Project(projectHomeDir);

        if (project.isMavenProject()) {
            try {
                String pomXml = FileUtils.readToString(new FileSystemResource(project.getMavenPomFile()));
                SimpleNamespaceContext nsContext = new SimpleNamespaceContext();
                nsContext.bindNamespaceUri("mvn", "http://maven.apache.org/POM/4.0.0");

                Document pomDoc = XMLUtils.parseMessagePayload(pomXml);
                project.setName(evaluate(pomDoc, "/mvn:project/mvn:artifactId", nsContext));

                String version = evaluate(pomDoc, "/mvn:project/mvn:version", nsContext);
                if (StringUtils.hasText(version)) {
                    project.setVersion(version);
                }

                project.getSettings().setBasePackage(evaluate(pomDoc, "/mvn:project/mvn:groupId", nsContext));

                String citrusVersion = evaluate(pomDoc, "/mvn:project/mvn:properties/mvn:citrus.version", nsContext);
                if (StringUtils.hasText(citrusVersion)) {
                    project.getSettings().setCitrusVersion(citrusVersion);
                }

                project.setDescription(evaluate(pomDoc, "/mvn:project/mvn:description", nsContext));
            } catch (IOException e) {
                throw new ApplicationRuntimeException("Unable to open Maven pom.xml file", e);
            }
        } else if (project.isAntProject()) {
            try {
                String buildXml = FileUtils.readToString(new FileSystemResource(project.getAntBuildFile()));
                SimpleNamespaceContext nsContext = new SimpleNamespaceContext();

                Document buildDoc = XMLUtils.parseMessagePayload(buildXml);
                project.setName(evaluate(buildDoc, "/project/@name", nsContext));

                String citrusVersion = evaluate(buildDoc, "/project/property[@name='citrus.version']/@value", nsContext);
                if (StringUtils.hasText(citrusVersion)) {
                    project.getSettings().setCitrusVersion(citrusVersion);
                }

                project.setDescription(evaluate(buildDoc, "/project/@description", nsContext));
            } catch (IOException e) {
                throw new ApplicationRuntimeException("Unable to open Apache Ant build.xml file", e);
            }
        }
    }

    /**
     * Returns the project's Spring application context config file.
     * @return the config file or null if no config file exists within the selected project.
     */
    public File getProjectContextConfigFile() {
        return fileBrowserService.findFileInPath(new File(project.getProjectHome()), getDefaultConfigurationFile(), true);
    }

    /**
     * Save project settings.
     */
    public void saveSettings(ProjectSettings settings) {
        project.setSettings(settings);
        saveProject();
    }

    /**
     * Save project to file system.
     */
    public void saveProject() {
        try (FileOutputStream fos = new FileOutputStream(getProjectInfoFile())) {
            fos.write(Jackson2ObjectMapperBuilder.json().build().writer().writeValueAsBytes(project));
            fos.flush();
        } catch (IOException e) {
            throw new CitrusRuntimeException("Unable to open project info file", e);
        }
    }

    /**
     * Gets file pointer to project info file in project home directory.
     * @return
     */
    public File getProjectInfoFile() {
        return new File(project.getProjectHome() + System.getProperty("file.separator") + "citrus-project.json");
    }

    /**
     * Reads default Citrus project property file for active project.
     * @return properties loaded or empty properties if nothing is found
     */
    public Properties getProjectProperties() {
        File projectProperties = fileBrowserService.findFileInPath(new File(project.getProjectHome()), "citrus.properties", true);

        try {
            return PropertiesLoaderUtils.loadProperties(new FileSystemResource(projectProperties));
        } catch (IOException e) {
            log.warn("Unable to read default Citrus project properties from file resource", e);
        }

        return new Properties();
    }

    /**
     * Evaluates Xpath expression on document and returns null safe result value as String representation.
     * @param document
     * @param expression
     * @param nsContext
     * @return
     */
    private String evaluate(Document document, String expression, SimpleNamespaceContext nsContext) {
        Object result = XPathUtils.evaluateExpression(document, expression, nsContext, XPathConstants.STRING);
        return result != null ? result.toString() : "";
    }

    /**
     * Checks if home directory is valid Citrus project home.
     *
     * @param directory
     */
    private boolean validateProjectHome(String directory) {
        File homeDir = new File(directory);

        try {
            Assert.isTrue(homeDir.exists());
            Assert.isTrue(new File(homeDir, Citrus.DEFAULT_TEST_SRC_DIRECTORY).exists());
            Assert.isTrue(new File(homeDir, Citrus.DEFAULT_TEST_SRC_DIRECTORY + "resources/" + getDefaultConfigurationFile()).exists());
        } catch (IllegalArgumentException e) {
            log.warn("Project home validation failed", e);
            return false;
        }

        return true;
    }

    /**
     * Gets file name from path pattern property.
     * @return
     */
    private String getDefaultConfigurationFile() {
        String configurationFile = Citrus.DEFAULT_APPLICATION_CONTEXT;

        if (configurationFile.startsWith("classpath*:")) {
            configurationFile = configurationFile.substring("classpath*:".length());
        }

        if (configurationFile.startsWith("classpath:")) {
            configurationFile = configurationFile.substring("classpath:".length());
        }

        return configurationFile;
    }

    /**
     * Gets the currently active project.
     * @return
     */
    public Project getActiveProject() {
        return project;
    }

    /**
     * Sets the project property.
     *
     * @param project
     */
    public void setActiveProject(Project project) {
        this.project = project;
    }
}
