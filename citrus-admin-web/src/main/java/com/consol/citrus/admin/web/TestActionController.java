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

package com.consol.citrus.admin.web;

import com.consol.citrus.admin.converter.action.TestActionConverter;
import com.consol.citrus.admin.exception.ApplicationRuntimeException;
import com.consol.citrus.admin.model.*;
import com.consol.citrus.admin.service.ProjectService;
import com.consol.citrus.admin.service.TestActionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Christoph Deppisch
 */
@Controller
@RequestMapping("api/test/actions")
public class TestActionController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private TestActionService testActionService;

    @Autowired
    private List<TestActionConverter> actionConverter;

    @RequestMapping(method = {RequestMethod.POST})
    @ResponseBody
    public void addTestAction(@RequestParam("pos") Integer position, @RequestBody TestDetail testDetail) {
        if (projectService.hasSpringXmlApplicationContext()) {
            testActionService.addTestAction(getXmlSourceFile(testDetail), projectService.getActiveProject(), position, convertToModel(testDetail.getActions().get(position)));
        } else if (projectService.hasSpringJavaConfig()) {
            throw new UnsupportedOperationException();
        }
    }

    @RequestMapping(method = {RequestMethod.PUT})
    @ResponseBody
    public void updateTestAction(@RequestParam("pos") Integer position, @RequestBody TestDetail testDetail) {
        if (projectService.hasSpringXmlApplicationContext()) {
            testActionService.updateTestAction(getXmlSourceFile(testDetail), projectService.getActiveProject(), position, convertToModel(testDetail.getActions().get(position)));
        } else if (projectService.hasSpringJavaConfig()) {
            throw new UnsupportedOperationException();
        }
    }

    @RequestMapping(value = "/delete", method = {RequestMethod.POST})
    @ResponseBody
    public void deleteTestAction(@RequestParam("pos") Integer position, @RequestBody TestDetail testDetail) {
        if (projectService.hasSpringXmlApplicationContext()) {
            testActionService.removeTestAction(getXmlSourceFile(testDetail), projectService.getActiveProject(), position);
        } else if (projectService.hasSpringJavaConfig()) {
            throw new UnsupportedOperationException();
        }
    }

    @RequestMapping(value = "/types", method = {RequestMethod.GET})
    @ResponseBody
    public List<String> getActionTypes() {
        return actionConverter.stream().map(TestActionConverter::getActionType).collect(Collectors.toList());
    }

    @RequestMapping(value = "/type/{type}", method = {RequestMethod.GET})
    @ResponseBody
    public TestActionModel getActionType(@PathVariable("type") String type) {
        for (TestActionConverter converter : actionConverter) {
            if (converter.getActionType().equals(type)) {
                try {
                    return converter.convert(converter.getSourceModelClass().newInstance());
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new ApplicationRuntimeException("Failed to create new test action model instance", e);
                }
            }
        }

        throw new ApplicationRuntimeException("Unable to find test action definition for type '" + type + "'");
    }

    /**
     * Find endpoint converter for given model type and convert generic endpoint definition back to model class definition.
     * @param testActionModel
     * @return
     */
    private Object convertToModel(TestActionModel testActionModel) {
        if (testActionModel.getModelType() == null) {
            throw new ApplicationRuntimeException("Missing model type in test action definition");
        }

        for (TestActionConverter converter : actionConverter) {
            if (converter.getSourceModelClass().equals(testActionModel.getModelType())) {
                return converter.convertBack(testActionModel);
            }
        }

        throw new ApplicationRuntimeException("Unable to convert test action definition to proper model type");
    }

    /**
     * Find XML source file in test detail.
     * @param detail
     * @return
     */
    private File getXmlSourceFile(TestDetail detail) {
        return new File(detail.getFile() + ".xml");
    }
}
