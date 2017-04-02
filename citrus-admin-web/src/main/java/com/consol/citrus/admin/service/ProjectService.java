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

import com.consol.citrus.admin.Application;
import com.consol.citrus.admin.connector.WebSocketPushEventsListener;
import com.consol.citrus.admin.exception.ApplicationRuntimeException;
import com.consol.citrus.admin.marshal.NamespacePrefixMapper;
import com.consol.citrus.admin.model.*;
import com.consol.citrus.admin.model.spring.Property;
import com.consol.citrus.admin.model.spring.SpringBean;
import com.consol.citrus.admin.service.spring.SpringBeanService;
import com.consol.citrus.admin.service.spring.SpringJavaConfigService;
import com.consol.citrus.exceptions.CitrusRuntimeException;
import com.consol.citrus.util.FileUtils;
import com.consol.citrus.util.XMLUtils;
import com.consol.citrus.xml.xpath.XPathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.springframework.util.xml.SimpleNamespaceContext;
import org.w3c.dom.*;

import javax.annotation.PostConstruct;
import javax.xml.xpath.XPathConstants;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Christoph Deppisch
 */
@Service
public class ProjectService {

    @Autowired
    private FileBrowserService fileBrowserService;

    /** Current project actively opened in Citrus admin */
    private Project project;

    /** Holds in memory list of recently opened projects */
    private List<String> recentlyOpened = new ArrayList<>();

    @Autowired
    private SpringBeanService springBeanService;

    @Autowired
    private SpringJavaConfigService springJavaConfigService;

    @Autowired
    private Environment environment;

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
        Project project = new Project(projectHomeDir);
        if (project.getProjectInfoFile().exists()) {
            project.loadSettings();
        } else if (!validateProject(project)) {
            throw new ApplicationRuntimeException("Invalid project home - not a proper Citrus project");
        }

        if (project.isMavenProject()) {
            try {
                String pomXml = FileUtils.readToString(new FileSystemResource(project.getMavenPomFile()));
                SimpleNamespaceContext nsContext = new SimpleNamespaceContext();
                nsContext.bindNamespaceUri("mvn", "http://maven.apache.org/POM/4.0.0");

                Document pomDoc = XMLUtils.parseMessagePayload(pomXml);
                project.setName(evaluate(pomDoc, "/mvn:project/mvn:artifactId", nsContext));

                String version = evaluate(pomDoc, "/mvn:project/mvn:version", nsContext);
                String parentVersion = evaluate(pomDoc, "/mvn:project/mvn:parent/mvn:version", nsContext);
                if (StringUtils.hasText(version)) {
                    project.setVersion(version);
                } else if (StringUtils.hasText(parentVersion)) {
                    project.setVersion(parentVersion);
                }

                project.getSettings().setBasePackage(evaluate(pomDoc, "/mvn:project/mvn:groupId", nsContext));

                String citrusVersion = evaluate(pomDoc, "/mvn:project/mvn:properties/mvn:citrus.version", nsContext);
                if (StringUtils.hasText(citrusVersion)) {
                    project.getSettings().setCitrusVersion(citrusVersion);
                }

                project.setDescription(evaluate(pomDoc, "/mvn:project/mvn:description", nsContext));

                project.getSettings().setConnectorActive(StringUtils.hasText(evaluate(pomDoc,
                        "/mvn:project/mvn:dependencies/mvn:dependency/mvn:artifactId[. = 'citrus-admin-connector']", nsContext)));
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

        saveProject(project);

        this.project = project;
        if (!this.recentlyOpened.contains(projectHomeDir)) {
            if (projectHomeDir.endsWith("/")) {
                this.recentlyOpened.add(projectHomeDir.substring(0, projectHomeDir.length() - 1));
            } else {
                this.recentlyOpened.add(projectHomeDir);
            }
        }
        System.setProperty(Application.PROJECT_HOME, projectHomeDir);
    }

    /**
     * Returns the project's Spring application context config file.
     * @return the config file or null if no config file exists within the selected project.
     */
    public File getSpringXmlApplicationContextFile() {
        String contextFile = project.getSettings().getSpringApplicationContext();

        if (contextFile.startsWith("classpath*:")) {
            contextFile = contextFile.substring("classpath*:".length());
        }

        if (contextFile.startsWith("classpath:")) {
            contextFile = contextFile.substring("classpath:".length());
        }

        return fileBrowserService.findFileInPath(new File(project.getProjectHome()), contextFile, true);
    }

    /**
     * Returns the project's Spring Java config file.
     * @return the config file or null if no config file exists within the selected project.
     */
    public File getSpringJavaConfigFile() {
        String contextFile = project.getSettings().getSpringJavaConfig();

        if (contextFile.contains(".")) {
            contextFile = contextFile.substring(contextFile.lastIndexOf(".") + 1);
        }

        return fileBrowserService.findFileInPath(new File(project.getJavaDirectory()), contextFile + ".java", true);
    }

    /**
     * Returns the project's Spring Java config file.
     * @return the config file or null if no config file exists within the selected project.
     */
    public Class<?> getSpringJavaConfig() {
        try {
            ClassLoader classLoader = project.getClassLoader();
            return classLoader.loadClass(project.getSettings().getSpringJavaConfig());
        }  catch (IOException | ClassNotFoundException | NoClassDefFoundError e) {
            throw new ApplicationRuntimeException("Failed to access Spring Java config class", e);
        }
    }

    /**
     * Checks if Spring application context file is present.
     * @return
     */
    public boolean hasSpringXmlApplicationContext() {
        return getSpringXmlApplicationContextFile() != null;
    }

    /**
     * Checks if Spring Java config class is present.
     * @return
     */
    public boolean hasSpringJavaConfig() {
        return getSpringJavaConfigFile() != null;
    }

    /**
     * Save project settings.
     */
    public void update(Project project) {
        this.project.setDescription(project.getDescription());
        this.project.setSettings(project.getSettings());
        saveProject(this.project);
    }

    /**
     * Save project to file system.
     * @param project
     */
    public void saveProject(Project project) {
        try (FileOutputStream fos = new FileOutputStream(project.getProjectInfoFile())) {
            fos.write(Jackson2ObjectMapperBuilder.json().build().writer().withDefaultPrettyPrinter().writeValueAsBytes(project));
            fos.flush();
        } catch (IOException e) {
            throw new CitrusRuntimeException("Unable to open project info file", e);
        }
    }

    /**
     * Reads default Citrus project property file for active project.
     * @return properties loaded or empty properties if nothing is found
     */
    public Properties getProjectProperties() {
        File projectProperties = fileBrowserService.findFileInPath(new File(project.getProjectHome()), "citrus.properties", true);

        try {
            if (projectProperties != null) {
                return PropertiesLoaderUtils.loadProperties(new FileSystemResource(projectProperties));
            }
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
     * Checks if home directory is valid Citrus project.
     *
     * @param project
     */
    public boolean validateProject(Project project) {
        File homeDir = new File(project.getProjectHome());

        try {
            Assert.isTrue(homeDir.exists(), "Invalid project home directory");
            Assert.isTrue(new File(homeDir, project.getSettings().getJavaSrcDirectory()).exists(), "Missing Java source directory");
            Assert.isTrue(new File(homeDir, project.getSettings().getXmlSrcDirectory()).exists(), "Missing resources directory");
        } catch (IllegalArgumentException e) {
            log.warn("Project home validation failed: " + e.getLocalizedMessage());
            return false;
        }

        return true;
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

    /**
     * Adds the citrus admin connector dependency to the target project Maven POM.
     */
    public void addConnector() {
        if (project.isMavenProject()) {
            try {
                String pomXml = FileUtils.readToString(new FileSystemResource(project.getMavenPomFile()));
                                                                                      
                if (!pomXml.contains("<artifactId>citrus-admin-connector</artifactId>")) {
                    String[] patterns = new String[] {
                            "\\s*<dependency>[\\s\\n\\r]*<groupId>com\\.consol\\.citrus</groupId>[\\s\\n\\r]*<artifactId>citrus-core</artifactId>[\\s\\n\\r]*<version>.*</version>[\\s\\n\\r]*</dependency>",
                            "\\s*<dependency>[\\s\\n\\r]*<groupId>com\\.consol\\.citrus</groupId>[\\s\\n\\r]*<artifactId>citrus-core</artifactId>[\\s\\n\\r]*</dependency>",
                            "\\s*<dependency>[\\s\\n\\r]*<groupId>com\\.consol\\.citrus</groupId>[\\s\\n\\r]*<artifactId>citrus-core</artifactId>[\\s\\n\\r]*<version>.*</version>[\\s\\n\\r]*<scope>.*</scope>[\\s\\n\\r]*</dependency>",
                            "\\s*<dependency>[\\s\\n\\r]*<groupId>com\\.consol\\.citrus</groupId>[\\s\\n\\r]*<artifactId>citrus-core</artifactId>[\\s\\n\\r]*<scope>.*</scope>[\\s\\n\\r]*</dependency>",
                    };

                    for (String pattern : patterns) {
                        Matcher matcher = Pattern.compile(pattern).matcher(pomXml);

                        if (matcher.find()) {
                            pomXml = pomXml.substring(0, matcher.end()) + String.format("%n    <dependency>%n      <groupId>com.consol.citrus</groupId>%n      <artifactId>citrus-admin-connector</artifactId>%n      <version>1.0.0-beta-5</version>%n    </dependency>") + pomXml.substring(matcher.end());
                            break;
                        }
                    }

                    if (!pomXml.contains("<artifactId>citrus-admin-connector</artifactId>")) {
                        throw new ApplicationRuntimeException("Failed to add admin connector dependency to Maven pom.xml file - please add manually");
                    }

                    FileUtils.writeToFile(pomXml, new FileSystemResource(project.getMavenPomFile()).getFile());
                }

                project.getSettings().setUseConnector(true);
                project.getSettings().setConnectorActive(true);
                saveProject(project);

                SpringBean bean = new SpringBean();
                bean.setId(WebSocketPushEventsListener.class.getSimpleName());
                bean.setClazz(WebSocketPushEventsListener.class.getName());

                if (!environment.getProperty("local.server.port", "8080").equals("8080")) {
                    Property portProperty = new Property();
                    portProperty.setName("port");
                    portProperty.setValue(environment.getProperty("local.server.port"));
                    bean.getProperties().add(portProperty);
                }

                if (hasSpringXmlApplicationContext() && springBeanService.getBeanDefinition(getSpringXmlApplicationContextFile(), getActiveProject(), WebSocketPushEventsListener.class.getSimpleName(), SpringBean.class) == null) {
                    springBeanService.addBeanDefinition(getSpringXmlApplicationContextFile(), getActiveProject(), bean);
                } else if (hasSpringJavaConfig() && springJavaConfigService.getBeanDefinition(getSpringJavaConfig(), getActiveProject(), WebSocketPushEventsListener.class.getSimpleName(), WebSocketPushEventsListener.class) == null) {
                    springJavaConfigService.addBeanDefinition(getSpringJavaConfigFile(), getActiveProject(), bean);
                }
            } catch (IOException e) {
                throw new ApplicationRuntimeException("Failed to add admin connector dependency to Maven pom.xml file", e);
            }
        }
    }

    /**
     * Removes the citrus admin connector dependency from the target project Maven POM.
     */
    public void removeConnector() {
        if (project.isMavenProject()) {
            try {
                String pomXml = FileUtils.readToString(new FileSystemResource(project.getMavenPomFile()));

                pomXml = pomXml.replaceAll("\\s*<dependency>[\\s\\n\\r]*<groupId>com\\.consol\\.citrus</groupId>[\\s\\n\\r]*<artifactId>citrus-admin-connector</artifactId>[\\s\\n\\r]*<version>.*</version>[\\s\\n\\r]*</dependency>", "");
                pomXml = pomXml.replaceAll("\\s*<dependency>[\\s\\n\\r]*<groupId>com\\.consol\\.citrus</groupId>[\\s\\n\\r]*<artifactId>citrus-admin-connector</artifactId>[\\s\\n\\r]*</dependency>", "");

                FileUtils.writeToFile(pomXml, new FileSystemResource(project.getMavenPomFile()).getFile());

                project.getSettings().setUseConnector(false);
                project.getSettings().setConnectorActive(false);
                saveProject(project);

                List<String> beans;
                if (hasSpringXmlApplicationContext()) {
                    beans = springBeanService.getBeanNames(getSpringXmlApplicationContextFile(), getActiveProject(), WebSocketPushEventsListener.class.getName());
                    for (String bean : beans) {
                        springBeanService.removeBeanDefinition(getSpringXmlApplicationContextFile(), getActiveProject(), bean);
                    }
                } else if (hasSpringJavaConfig()) {
                    beans = springJavaConfigService.getBeanNames(getSpringJavaConfig(), getActiveProject(), WebSocketPushEventsListener.class);
                    for (String bean : beans) {
                        springJavaConfigService.removeBeanDefinition(getSpringJavaConfigFile(), getActiveProject(), bean);
                    }
                }
            } catch (IOException e) {
                throw new ApplicationRuntimeException("Failed to remove admin connector dependency from Maven pom.xml file", e);
            }
        }
    }

    /**
     * Sets new settings as defaults.
     * @param settings
     */
    public void applySettings(ProjectSettings settings) {
        System.setProperty(Application.JAVA_SRC_DIRECTORY, settings.getJavaSrcDirectory());
        System.setProperty(Application.XML_SRC_DIRECTORY, settings.getXmlSrcDirectory());
        System.setProperty(Application.SPRING_APPLICATION_CONTEXT, settings.getSpringApplicationContext());
        System.setProperty(Application.SPRING_JAVA_CONFIG, settings.getSpringJavaConfig());
    }

    /**
     * Get the Citrus modules for this project based on the build dependencies.
     * @return
     */
    public List<Module> getModules() {
        List<Module> modules = new ArrayList<>();
        Collection<String> allModules = new NamespacePrefixMapper().getNamespaceMappings().values();

        if (project.isMavenProject()) {
            try {
                String pomXml = FileUtils.readToString(new FileSystemResource(project.getMavenPomFile()));
                SimpleNamespaceContext nsContext = new SimpleNamespaceContext();
                nsContext.bindNamespaceUri("mvn", "http://maven.apache.org/POM/4.0.0");

                Document pomDoc = XMLUtils.parseMessagePayload(pomXml);

                NodeList dependencies = XPathUtils.evaluateAsNodeList(pomDoc, "/mvn:project/mvn:dependencies/mvn:dependency/mvn:artifactId[starts-with(., 'citrus-')]", nsContext);

                for (int i = 0; i < dependencies.getLength(); i++) {
                    String moduleName = DomUtils.getTextValue((Element) dependencies.item(i));

                    if (moduleName.equals("citrus-core")) {
                        allModules.remove("citrus");
                    } else {
                        allModules.remove(moduleName);
                    }

                    modules.add(new Module(moduleName.substring("citrus-".length()), getActiveProject().getVersion(), true));
                }
            } catch (IOException e) {
                throw new ApplicationRuntimeException("Unable to open Maven pom.xml file", e);
            }
        }

        allModules.stream()
                .filter(name -> !name.equals("citrus-test"))
                .map(name -> new Module(name.substring("citrus-".length()), getActiveProject().getVersion(), false))
                .forEach(modules::add);

        return modules;
    }

    /**
     * Adds module to project build configuration as dependency.
     * @param module
     */
    public void add(Module module) {
        if (project.isMavenProject()) {
            try {
                String pomXml = FileUtils.readToString(new FileSystemResource(project.getMavenPomFile()));

                if (!pomXml.contains("<artifactId>citrus-" + module.getName() + "</artifactId>")) {
                    String[] patterns = new String[] {
                            "\\s*<dependency>[\\s\\n\\r]*<groupId>com\\.consol\\.citrus</groupId>[\\s\\n\\r]*<artifactId>citrus-core</artifactId>[\\s\\n\\r]*<version>.*</version>[\\s\\n\\r]*</dependency>",
                            "\\s*<dependency>[\\s\\n\\r]*<groupId>com\\.consol\\.citrus</groupId>[\\s\\n\\r]*<artifactId>citrus-core</artifactId>[\\s\\n\\r]*</dependency>",
                            "\\s*<dependency>[\\s\\n\\r]*<groupId>com\\.consol\\.citrus</groupId>[\\s\\n\\r]*<artifactId>citrus-core</artifactId>[\\s\\n\\r]*<version>.*</version>[\\s\\n\\r]*<scope>.*</scope>[\\s\\n\\r]*</dependency>",
                            "\\s*<dependency>[\\s\\n\\r]*<groupId>com\\.consol\\.citrus</groupId>[\\s\\n\\r]*<artifactId>citrus-core</artifactId>[\\s\\n\\r]*<scope>.*</scope>[\\s\\n\\r]*</dependency>",
                    };

                    for (String pattern : patterns) {
                        Matcher matcher = Pattern.compile(pattern).matcher(pomXml);

                        if (matcher.find()) {
                            pomXml = pomXml.substring(0, matcher.end()) + String.format("%n    <dependency>%n      <groupId>com.consol.citrus</groupId>%n      <artifactId>citrus-" + module.getName() + "</artifactId>%n      <version>" + getActiveProject().getVersion() + "</version>%n    </dependency>") + pomXml.substring(matcher.end());
                            break;
                        }
                    }

                    if (!pomXml.contains("<artifactId>citrus-" + module.getName() + "</artifactId>")) {
                        throw new ApplicationRuntimeException("Failed to add Citrus module dependency to Maven pom.xml file - please add manually");
                    }

                    FileUtils.writeToFile(pomXml, new FileSystemResource(project.getMavenPomFile()).getFile());
                }
            } catch (IOException e) {
                throw new ApplicationRuntimeException("Failed to add admin connector dependency to Maven pom.xml file", e);
            }
        }
    }

    /**
     * Removes module from project build configuration.
     * @param module
     */
    public void remove(Module module) {
        if (project.isMavenProject()) {
            try {
                String pomXml = FileUtils.readToString(new FileSystemResource(project.getMavenPomFile()));

                pomXml = pomXml.replaceAll("\\s*<dependency>[\\s\\n\\r]*<groupId>com\\.consol\\.citrus</groupId>[\\s\\n\\r]*<artifactId>citrus-" + module.getName() + "</artifactId>[\\s\\n\\r]*<version>.*</version>[\\s\\n\\r]*</dependency>", "");
                pomXml = pomXml.replaceAll("\\s*<dependency>[\\s\\n\\r]*<groupId>com\\.consol\\.citrus</groupId>[\\s\\n\\r]*<artifactId>citrus-" + module.getName() + "</artifactId>[\\s\\n\\r]*</dependency>", "");

                FileUtils.writeToFile(pomXml, new FileSystemResource(project.getMavenPomFile()).getFile());
            } catch (IOException e) {
                throw new ApplicationRuntimeException("Failed to remove Citrus module dependency from Maven pom.xml file", e);
            }
        }
    }

    /**
     * Sets the fileBrowserService property.
     *
     * @param fileBrowserService
     */
    public void setFileBrowserService(FileBrowserService fileBrowserService) {
        this.fileBrowserService = fileBrowserService;
    }

    /**
     * Sets the springBeanService property.
     *
     * @param springBeanService
     */
    public void setSpringBeanService(SpringBeanService springBeanService) {
        this.springBeanService = springBeanService;
    }

    /**
     * Sets the springJavaConfigService.
     *
     * @param springJavaConfigService
     */
    public void setSpringJavaConfigService(SpringJavaConfigService springJavaConfigService) {
        this.springJavaConfigService = springJavaConfigService;
    }

    /**
     * Sets the environment property.
     *
     * @param environment
     */
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    /**
     * Gets the recently opened projects.
     * @return
     */
    public String[] getRecentProjects() {
        return recentlyOpened.toArray(new String[recentlyOpened.size()]);
    }

    /**
     * Sets the project.
     *
     * @param project
     */
    public void setProject(Project project) {
        this.project = project;
    }
}
