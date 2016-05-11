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

package com.consol.citrus.admin.converter.action;

import com.consol.citrus.TestAction;
import com.consol.citrus.container.TestActionContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Christoph Deppisch
 * @since 2.6
 */
public abstract class AbstractTestContainerConverter<S, R extends com.consol.citrus.TestAction> extends AbstractTestActionConverter<S, R> {

    /** Logger */
    private static Logger log = LoggerFactory.getLogger(SequentialContainerConverter.class);

    @Autowired
    private List<TestActionConverter<?, ? extends TestAction>> actionConverter;

    /**
     * Default constructor using action type reference.
     *
     * @param actionType
     */
    protected AbstractTestContainerConverter(String actionType) {
        super(actionType);
    }

    /**
     * Reads and converts list of nested actions.
     * @param objectList
     * @return
     */
    protected List<com.consol.citrus.admin.model.TestAction> getNestedActions(List<Object> objectList) {
        List<com.consol.citrus.admin.model.TestAction> actions = new ArrayList<>();
        for (Object actionType : objectList) {
            com.consol.citrus.admin.model.TestAction nested = null;
            for (TestActionConverter converter : actionConverter) {
                if (converter.getSourceModelClass().isInstance(actionType)) {
                    nested = converter.convert(actionType);
                    break;
                }
            }

            if (nested == null) {
                if (actionType.getClass().getAnnotation(XmlRootElement.class) == null) {
                    log.info(actionType.getClass().getName());
                } else {
                    nested = new ActionConverter(actionType.getClass().getAnnotation(XmlRootElement.class).name()).convert(actionType);
                }
            }

            if (nested != null) {
                actions.add(nested);
            }
        }

        return actions;
    }

    /**
     * Converts test actions to model.
     * @param model
     * @param objectList
     */
    protected void convertActions(TestActionContainer model, List<Object> objectList) {
        for (com.consol.citrus.TestAction actionType : model.getActions()) {
            Object nested = null;
            for (TestActionConverter converter : actionConverter) {
                if (converter.getActionModelClass().isInstance(actionType)) {
                    nested = converter.convertModel(actionType);
                    break;
                }
            }

            if (nested == null) {
                nested = new ActionConverter(actionType.getName()).convertModel(actionType);
            }

            if (nested != null) {
                objectList.add(nested);
            }
        }
    }
}
