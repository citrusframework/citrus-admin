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

package com.consol.citrus.admin.web;

import com.consol.citrus.admin.model.spring.SpringBean;
import com.consol.citrus.admin.service.ProjectService;
import com.consol.citrus.admin.service.spring.SpringBeanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * @author Christoph Deppisch
 */
@Controller
@RequestMapping("/beans")
public class SpringBeanController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private SpringBeanService springBeanService;

    @RequestMapping(method = {RequestMethod.POST})
    @ResponseBody
    public void createBean(@RequestBody SpringBean bean) {
        springBeanService.addBeanDefinition(projectService.getProjectContextConfigFile(), bean);
    }

    @RequestMapping(value = "/{id}", method = {RequestMethod.PUT})
    @ResponseBody
    public void updateBean(@PathVariable("id") String id, @RequestBody SpringBean bean) {
        springBeanService.updateBeanDefinition(projectService.getProjectContextConfigFile(), id, bean);
    }

    @RequestMapping(value = "/{id}", method = {RequestMethod.DELETE})
    @ResponseBody
    public void deleteBean(@PathVariable("id") String id) {
        springBeanService.removeBeanDefinition(projectService.getProjectContextConfigFile(), id);
    }

    @RequestMapping(value = "/{type}", method = {RequestMethod.GET})
    @ResponseBody
    public List<SpringBean> listBeans(@PathVariable("type") String type) {
        return springBeanService.getBeanDefinitions(projectService.getProjectContextConfigFile(), SpringBean.class, Collections.singletonMap("class", type));
    }

    @RequestMapping(value = "/{type}/{id}", method = {RequestMethod.GET})
    @ResponseBody
    public SpringBean getBean(@PathVariable("type") String type, @PathVariable("id") String id) {
        return springBeanService.getBeanDefinition(projectService.getProjectContextConfigFile(), id, SpringBean.class);
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ResponseBody
    public List<String> search(@RequestBody String type) {
        return springBeanService.getBeanNames(projectService.getProjectContextConfigFile(), type);
    }
}
