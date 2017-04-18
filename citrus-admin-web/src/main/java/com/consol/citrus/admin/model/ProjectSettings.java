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

package com.consol.citrus.admin.model;

import com.consol.citrus.Citrus;
import com.consol.citrus.admin.Application;
import com.consol.citrus.admin.model.build.BuildConfiguration;
import com.consol.citrus.admin.model.build.maven.MavenBuildConfiguration;
import org.springframework.util.StringUtils;

import java.io.File;

/**
 * @author Christoph Deppisch
 */
public class ProjectSettings {

    private String basePackage = System.getProperty(Application.BASE_PACKAGE, "com.consol.citrus");
    private String citrusVersion = Citrus.getVersion();

    private String springApplicationContext = System.getProperty(Application.SPRING_APPLICATION_CONTEXT, Citrus.DEFAULT_APPLICATION_CONTEXT);
    private String springJavaConfig = System.getProperty(Application.SPRING_JAVA_CONFIG, System.getProperty(Citrus.DEFAULT_APPLICATION_CONTEXT_CLASS_PROPERTY, "com.consol.citrus.CitrusEndpointConfig"));
    private String javaSrcDirectory = System.getProperty(Application.JAVA_SRC_DIRECTORY, "src" + File.separator + "test" + File.separator + "java" + File.separator);
    private String xmlSrcDirectory = System.getProperty(Application.XML_SRC_DIRECTORY, "src" + File.separator + "test" + File.separator + "resources" + File.separator);
    private String javaFilePattern = StringUtils.arrayToCommaDelimitedString(Citrus.getJavaTestFileNamePattern().toArray());
    private String xmlFilePattern = StringUtils.arrayToCommaDelimitedString(Citrus.getXmlTestFileNamePattern().toArray());

    private boolean useConnector = true;
    private boolean connectorActive = false;

    private int tabSize = 2;

    private BuildConfiguration build = new MavenBuildConfiguration();

    /**
     * Gets the value of the javaSrcDirectory property.
     *
     * @return the javaSrcDirectory
     */
    public String getJavaSrcDirectory() {
        return javaSrcDirectory;
    }

    /**
     * Sets the javaSrcDirectory property.
     *
     * @param javaSrcDirectory
     */
    public void setJavaSrcDirectory(String javaSrcDirectory) {
        this.javaSrcDirectory = javaSrcDirectory;
    }

    /**
     * Gets the value of the xmlSrcDirectory property.
     *
     * @return the xmlSrcDirectory
     */
    public String getXmlSrcDirectory() {
        return xmlSrcDirectory;
    }

    /**
     * Sets the xmlSrcDirectory property.
     *
     * @param xmlSrcDirectory
     */
    public void setXmlSrcDirectory(String xmlSrcDirectory) {
        this.xmlSrcDirectory = xmlSrcDirectory;
    }

    /**
     * Sets the javaFilePattern property.
     *
     * @param javaFilePattern
     */
    public void setJavaFilePattern(String javaFilePattern) {
        this.javaFilePattern = javaFilePattern;
    }

    /**
     * Gets the value of the javaFilePattern property.
     *
     * @return the javaFilePattern
     */
    public String getJavaFilePattern() {
        return javaFilePattern;
    }

    /**
     * Sets the xmlFilePattern property.
     *
     * @param xmlFilePattern
     */
    public void setXmlFilePattern(String xmlFilePattern) {
        this.xmlFilePattern = xmlFilePattern;
    }

    /**
     * Gets the value of the xmlFilePattern property.
     *
     * @return the xmlFilePattern
     */
    public String getXmlFilePattern() {
        return xmlFilePattern;
    }

    /**
     * Gets the value of the basePackage property.
     *
     * @return the basePackage
     */
    public String getBasePackage() {
        return basePackage;
    }

    /**
     * Sets the basePackage property.
     *
     * @param basePackage
     */
    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    /**
     * Sets the build configuration.
     * @param build
     */
    public void setBuild(BuildConfiguration build) {
        this.build = build;
    }

    /**
     * Gets the build configuration.
     * @return
     */
    public BuildConfiguration getBuild() {
        return build;
    }

    /**
     * Sets the citrus version.
     * @param citrusVersion
     */
    public void setCitrusVersion(String citrusVersion) {
        this.citrusVersion = citrusVersion;
    }

    /**
     * Gets the citrus version.
     * @return
     */
    public String getCitrusVersion() {
        return citrusVersion;
    }

    /**
     * Gets the value of the springApplicationContext property.
     *
     * @return the springApplicationContext
     */
    public String getSpringApplicationContext() {
        return springApplicationContext;
    }

    /**
     * Sets the springApplicationContext property.
     *
     * @param springApplicationContext
     */
    public void setSpringApplicationContext(String springApplicationContext) {
        this.springApplicationContext = springApplicationContext;
    }

    /**
     * Gets the springJavaConfig.
     *
     * @return
     */
    public String getSpringJavaConfig() {
        return springJavaConfig;
    }

    /**
     * Sets the springJavaConfig.
     *
     * @param springJavaConfig
     */
    public void setSpringJavaConfig(String springJavaConfig) {
        this.springJavaConfig = springJavaConfig;
    }

    /**
     * Sets the useConnector property.
     *
     * @param useConnector
     */
    public void setUseConnector(boolean useConnector) {
        this.useConnector = useConnector;
    }

    /**
     * Gets the value of the useConnector property.
     *
     * @return the useConnector
     */
    public boolean isUseConnector() {
        return useConnector;
    }

    /**
     * Sets the connectorActive property.
     *
     * @param connectorActive
     */
    public void setConnectorActive(boolean connectorActive) {
        this.connectorActive = connectorActive;
    }

    /**
     * Gets the value of the connectorActive property.
     *
     * @return the connectorActive
     */
    public boolean isConnectorActive() {
        return connectorActive;
    }

    /**
     * Sets the tabSize property.
     *
     * @param tabSize
     */
    public void setTabSize(int tabSize) {
        this.tabSize = tabSize;
    }

    /**
     * Gets the value of the tabSize property.
     *
     * @return the tabSize
     */
    public int getTabSize() {
        return tabSize;
    }
}
