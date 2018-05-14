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

package com.consol.citrus.admin.service.spring;

import com.consol.citrus.admin.converter.model.ModelConverter;
import com.consol.citrus.admin.converter.model.spring.SpringBeanModelConverter;
import com.consol.citrus.admin.exception.ApplicationRuntimeException;
import com.consol.citrus.admin.model.Project;
import com.consol.citrus.admin.model.spring.SpringBean;
import com.consol.citrus.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Christoph Deppisch
 */
@Service
public class SpringJavaConfigService {

    @Autowired
    private List<ModelConverter> modelConverter;

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(SpringJavaConfigService.class);

    /**
     * Reads file import locations from Spring bean application context.
     * @param project
     * @return
     */
    public List<Class<?>> getConfigImports(Class<?> configFile, Project project) {
        Import importAnnotation = configFile.getAnnotation(Import.class);
        if (importAnnotation != null) {
            return Arrays.asList(importAnnotation.value());
        }

        return new ArrayList<>();
    }

    /**
     * Finds bean definition element by id and type in Spring application context and
     * performs unmarshalling in order to return JaxB object.
     * @param project
     * @param id
     * @param type
     * @return
     */
    public <T> T getBeanDefinition(Class<?> configFile, Project project, String id, Class<T> type) {
        List<Class<?>> configFiles = new ArrayList<>();
        configFiles.add(configFile);
        configFiles.addAll(getConfigImports(configFile, project));

        final Method[] beanDefinitionMethod = new Method[1];
        for (Class<?> config : configFiles) {
            ReflectionUtils.doWithMethods(config, method -> {
                if (method.getName().equals(id)) {
                    beanDefinitionMethod[0] = method;
                } else {
                    Bean beanAnnotation = method.getAnnotation(Bean.class);
                    if (beanAnnotation.value().length > 0 && beanAnnotation.value()[0].equals(id)) {
                        beanDefinitionMethod[0] = method;
                    }

                    if (beanAnnotation.name().length > 0 && beanAnnotation.name()[0].equals(id)) {
                        beanDefinitionMethod[0] = method;
                    }
                }
            }, method -> method.getAnnotation(Bean.class) != null);

            if (beanDefinitionMethod[0] != null) {
                try {
                    Object bean = beanDefinitionMethod[0].invoke(config.newInstance());
                    if (bean.getClass().equals(type)) {
                        return (T) bean;
                    } else if (type.equals(SpringBean.class)) {
                        SpringBeanModelConverter<Object> springBeanModelConverter = new SpringBeanModelConverter(bean.getClass());
                        return (T) springBeanModelConverter.convert(id, bean);
                    } else {
                        for (ModelConverter converter : modelConverter) {
                            if (converter.getSourceModelClass().equals(bean.getClass()) &&
                                    converter.getTargetModelClass().equals(type)) {
                                return (T) converter.convert(id, bean);
                            }
                        }
                    }
                } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                    throw new ApplicationRuntimeException("Failed to access bean definition method in Java config", e);
                }
            }
        }

        return null;
    }

    /**
     * Finds all bean definition elements by type in Spring application context and
     * performs unmarshalling in order to return a list of JaxB object.
     * @param project
     * @param type
     * @return
     */
    public <T> List<T> getBeanDefinitions(Class<?> configFile, Project project, Class<T> type) {
        log.debug(String.format("Reading Java config: %s", configFile.getName()));
        log.debug(String.format("Reading beans of type: %s", type.getName()));

        List<T> beanDefinitions = new ArrayList<>();

        List<Class<?>> importedFiles = getConfigImports(configFile, project);
        for (Class<?> importLocation : importedFiles) {
            beanDefinitions.addAll(getBeanDefinitions(importLocation, project, type));
        }

        final List<Method> beanDefinitionMethods = new ArrayList<>();
        ReflectionUtils.doWithMethods(configFile, method -> {
            if (type.equals(SpringBean.class)) {
                if (modelConverter.stream().noneMatch(converter -> converter.getSourceModelClass().equals(method.getReturnType()))) {
                    beanDefinitionMethods.add(method);
                }
            } else {
                if (method.getReturnType().equals(type) ||
                        modelConverter.stream().anyMatch(converter -> converter.getTargetModelClass().equals(type) && converter.getSourceModelClass().equals(method.getReturnType()))) {
                    beanDefinitionMethods.add(method);
                }
            }
        }, method -> method.getAnnotation(Bean.class) != null);

        for (Method beanDefinitionMethod : beanDefinitionMethods) {
            try {
                String beanId = beanDefinitionMethod.getName();
                Bean beanAnnotation = beanDefinitionMethod.getAnnotation(Bean.class);
                if (beanAnnotation != null) {
                    if (beanAnnotation.value().length > 0) {
                        beanId = beanAnnotation.value()[0];
                    } else if (beanAnnotation.name().length > 0) {
                        beanId = beanAnnotation.name()[0];
                    }
                }

                log.debug(String.format("Found bean definition '%s' through method: %s()", beanId, beanDefinitionMethod.getName()));

                Object bean = beanDefinitionMethod.invoke(configFile.newInstance());
                if (bean.getClass().equals(type)) {
                    beanDefinitions.add((T) bean);
                } else if (type.equals(SpringBean.class)) {
                    SpringBeanModelConverter<Object> springBeanModelConverter = new SpringBeanModelConverter(bean.getClass());
                    beanDefinitions.add((T) springBeanModelConverter.convert(beanId, bean));
                } else {
                    for (ModelConverter converter : modelConverter) {
                        if (converter.getSourceModelClass().equals(bean.getClass()) &&
                                converter.getTargetModelClass().equals(type)) {
                            beanDefinitions.add((T) converter.convert(beanId, bean));
                            break;
                        }
                    }
                }
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
                log.error("Failed to access bean definition method in Java config", e);
            }
        }

        log.debug(String.format("Found %s Java config beans of type: %s", beanDefinitions.size(), type.getName()));

        return beanDefinitions;
    }

    /**
     * Find all Spring bean definitions in application context for given bean type.
     * @param project
     * @param type
     * @return
     */
    public List<String> getBeanNames(Class<?> configFile, Project project, Class<?> type) {
        List<String> beanNames = new ArrayList<>();

        List<Class<?>> importedFiles = getConfigImports(configFile, project);
        for (Class<?> importLocation : importedFiles) {
            beanNames.addAll(getBeanNames(importLocation, project, type));
        }

        ReflectionUtils.doWithMethods(configFile, method -> {
            Bean beanAnnotation = method.getAnnotation(Bean.class);
            if (beanAnnotation.value().length > 0) {
                Collections.addAll(beanNames, beanAnnotation.value());
            } else if (beanAnnotation.name().length > 0) {
                Collections.addAll(beanNames, beanAnnotation.name());
            } else {
                beanNames.add(method.getName());
            }
        }, method -> method.getAnnotation(Bean.class) != null && method.getReturnType().equals(type));

        return beanNames;
    }

    /**
     * Method adds a new Spring bean definition to the XML application context file.
     * @param project
     * @param model
     */
    public void addBeanDefinition(File configFile, Project project, Object model) {
        ModelConverter converter = modelConverter.stream().filter(c -> c.getTargetModelClass().equals(model.getClass()))
                .findFirst()
                .orElseGet(() -> {
                    if (model instanceof SpringBean) {
                        try {
                            return new SpringBeanModelConverter(project.getClassLoader().loadClass(((SpringBean) model).getClazz()));
                        } catch (ClassNotFoundException | IOException e) {
                            log.warn("Unable to access target Spring bean model type: " + ((SpringBean) model).getClazz(), e);
                        }
                    }

                    return new SpringBeanModelConverter(model.getClass());
        });

        String javaCode;
        try (InputStream fis = new FileInputStream(configFile)) {
            javaCode = FileUtils.readToString(fis);
        } catch (IOException e) {
            throw new ApplicationRuntimeException("Failed to read/write Java config file");
        }

        StringBuilder codeBuilder = new StringBuilder(javaCode);
        String codeSnippet = converter.getJavaConfig(model);
        Matcher classStartMatcher = Pattern.compile("^(public)? class " + configFile.getName().replaceAll("\\.java", "") + "(.*)\\{(\\s)*$", Pattern.MULTILINE).matcher(javaCode);
        if (classStartMatcher.find()) {
            codeBuilder.insert(classStartMatcher.end(), codeSnippet);
        }

        Matcher importMatcher = Pattern.compile("^import(.*);(\\s)*$", Pattern.MULTILINE).matcher(javaCode);
        if (importMatcher.find()) {
            List<Class<?>> imports = converter.getAdditionalImports();
            imports.stream().map(importType -> String.format("%nimport %s;", importType.getName())).forEach(importStmt -> {
                if (!javaCode.contains(importStmt)) {
                    codeBuilder.insert(importMatcher.end(), importStmt);
                }
            });
        }

        FileUtils.writeToFile(codeBuilder.toString(), configFile);

    }

    /**
     * Method removes a Spring bean definition from the XML application context file. Bean definition is
     * identified by its id or bean name.
     * @param project
     * @param id
     */
    public void removeBeanDefinition(File configFile, Project project, String id) {

    }

    /**
     * Method removes all Spring bean definitions of given type from the XML application context file.
     * @param project
     * @param type
     */
    public void removeBeanDefinitions(File configFile, Project project, Class<?> type) {

    }

    /**
     * Method updates an existing Spring bean definition in a XML application context file. Bean definition is
     * identified by its id or bean name.
     * @param project
     * @param id
     * @param model
     */
    public void updateBeanDefinition(File configFile, Project project, String id, Object model) {

    }

    /**
     * Method updates existing Spring bean definitions in a XML application context file. Bean definition is
     * identified by its type defining class.
     *
     * @param project
     * @param type
     * @param model
     */
    public void updateBeanDefinitions(File configFile, Project project, Class<?> type, Object model) {

    }
}
