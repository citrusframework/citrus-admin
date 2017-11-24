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

package com.consol.citrus.admin.converter.action;

import com.consol.citrus.admin.model.TestActionModel;
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
    private List<TestActionConverter> actionConverter;

    /**
     * Default constructor using action type reference.
     *
     * @param actionType
     */
    protected AbstractTestContainerConverter(String actionType) {
        super(actionType);
    }

    @Override
    public TestActionModel convert(S model) {
        TestActionModel action = super.convert(model);
        action.setActions(convertNestedActions(getNestedActions(model)));
        return action;
    }

    /**
     * Provide list of nested test actions.
     * @return
     */
    protected abstract List<Object> getNestedActions(S model);

    /**
     * Reads and converts list of nested actions.
     * @param objectList
     * @return
     */
    protected List<TestActionModel> convertNestedActions(List<Object> objectList) {
        List<TestActionModel> actions = new ArrayList<>();
        for (Object actionType : objectList) {
            TestActionModel nested = null;
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

    /**
     * Sets the actionConverter.
     *
     * @param actionConverter
     */
    public AbstractTestContainerConverter setActionConverter(List<TestActionConverter> actionConverter) {
        this.actionConverter = actionConverter;
        return this;
    }
}
