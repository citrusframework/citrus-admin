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

package com.consol.citrus.admin.converter.action.ws;

import com.consol.citrus.admin.converter.action.AbstractTestContainerConverter;
import com.consol.citrus.admin.model.TestAction;
import com.consol.citrus.model.testcase.core.*;
import com.consol.citrus.model.testcase.ws.AssertFaultModel;
import com.consol.citrus.ws.actions.AssertSoapFault;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author Christoph Deppisch
 * @since 2.6
 */
@Component
public class AssertSoapFaultContainerConverter extends AbstractTestContainerConverter<AssertFaultModel, AssertSoapFault> {

    public AssertSoapFaultContainerConverter() {
        super("assert-fault");
    }

    @Override
    public TestAction convert(AssertFaultModel model) {
        TestAction action = new TestAction(getActionType(), getSourceModelClass());
        addActionProperties(action, model);

        if (model.getWhen() != null) {
            action.setActions(getNestedActions(Collections.singletonList(getNestedAction(model))));
        }

        return action;
    }

    @Override
    public AssertFaultModel convertModel(AssertSoapFault model) {
        AssertFaultModel action = new AssertFaultModel();
        action.setDescription(model.getDescription());

        List<Object> actions = new ArrayList<>();
        convertActions(model, actions);

        if (actions.size() > 0) {
            setNestedAction(action, actions.get(0));
        }

        return action;
    }

    private void setNestedAction(AssertFaultModel action, Object object) {
        action.setWhen(new AssertFaultModel.When());

        if (object instanceof ActionModel) {
            action.getWhen().setAction((ActionModel) object);
        } else if (object instanceof AntModel) {
            action.getWhen().setAnt((AntModel) object);
        } else if (object instanceof com.consol.citrus.model.testcase.core.AssertModel) {
            action.getWhen().setAssert((com.consol.citrus.model.testcase.core.AssertModel) object);
        } else if (object instanceof CallTemplateModel) {
            action.getWhen().setCallTemplate((CallTemplateModel) object);
        } else if (object instanceof CatchModel) {
            action.getWhen().setCatch((CatchModel) object);
        } else if (object instanceof ConditionalModel) {
            action.getWhen().setConditional((ConditionalModel) object);
        } else if (object instanceof CreateVariablesModel) {
            action.getWhen().setCreateVariables((CreateVariablesModel) object);
        } else if (object instanceof EchoModel) {
            action.getWhen().setEcho((EchoModel) object);
        } else if (object instanceof ExpectTimeoutModel) {
            action.getWhen().setExpectTimeout((ExpectTimeoutModel) object);
        } else if (object instanceof FailModel) {
            action.getWhen().setFail((FailModel) object);
        } else if (object instanceof GroovyModel) {
            action.getWhen().setGroovy((GroovyModel) object);
        } else if (object instanceof InputModel) {
            action.getWhen().setInput((InputModel) object);
        } else if (object instanceof IterateModel) {
            action.getWhen().setIterate((IterateModel) object);
        } else if (object instanceof JavaModel) {
            action.getWhen().setJava((JavaModel) object);
        } else if (object instanceof LoadModel) {
            action.getWhen().setLoad((LoadModel) object);
        } else if (object instanceof ParallelModel) {
            action.getWhen().setParallel((ParallelModel) object);
        } else if (object instanceof PlsqlModel) {
            action.getWhen().setPlsql((PlsqlModel) object);
        } else if (object instanceof PurgeChannelModel) {
            action.getWhen().setPurgeChannel((PurgeChannelModel) object);
        } else if (object instanceof PurgeEndpointModel) {
            action.getWhen().setPurgeEndpoint((PurgeEndpointModel) object);
        } else if (object instanceof ReceiveActionType) {
            action.getWhen().setReceive((ReceiveActionType) object);
        } else if (object instanceof RepeatOnerrorUntilTrueModel) {
            action.getWhen().setRepeatOnerrorUntilTrue((RepeatOnerrorUntilTrueModel) object);
        } else if (object instanceof RepeatUntilTrueModel) {
            action.getWhen().setRepeatUntilTrue((RepeatUntilTrueModel) object);
        } else if (object instanceof SendActionType) {
            action.getWhen().setSend((SendActionType) object);
        } else if (object instanceof SequentialModel) {
            action.getWhen().setSequential((SequentialModel) object);
        } else if (object instanceof SleepModel) {
            action.getWhen().setSleep((SleepModel) object);
        } else if (object instanceof SqlModel) {
            action.getWhen().setSql((SqlModel) object);
        } else if (object instanceof StartModel) {
            action.getWhen().setStart((StartModel) object);
        } else if (object instanceof StopModel) {
            action.getWhen().setStop((StopModel) object);
        } else if (object instanceof StopTimerModel) {
            action.getWhen().setStopTimer((StopTimerModel) object);
        } else if (object instanceof TimerModel) {
            action.getWhen().setTimer((TimerModel) object);
        } else if (object instanceof TraceTimeModel) {
            action.getWhen().setTraceTime((TraceTimeModel) object);
        } else if (object instanceof TraceVariablesModel) {
            action.getWhen().setTraceVariables((TraceVariablesModel) object);
        } else if (object instanceof TransformModel) {
            action.getWhen().setTransform((TransformModel) object);
        } else if (object instanceof WaitModel) {
            action.getWhen().setWait((WaitModel) object);
        } else {
            action.getWhen().setAny(object);
        }
    }

    private Object getNestedAction(AssertFaultModel model) {
        if (model.getWhen().getAction() != null) {
            return model.getWhen().getAction();
        } else if (model.getWhen().getAnt() != null) {
            return model.getWhen().getAnt();
        } else if (model.getWhen().getAssert() != null) {
            return model.getWhen().getAssert();
        } else if (model.getWhen().getCallTemplate() != null) {
            return model.getWhen().getCallTemplate();
        } else if (model.getWhen().getCatch() != null) {
            return model.getWhen().getCatch();
        } else if (model.getWhen().getConditional() != null) {
            return model.getWhen().getConditional();
        } else if (model.getWhen().getCreateVariables() != null) {
            return model.getWhen().getCreateVariables();
        } else if (model.getWhen().getEcho() != null) {
            return model.getWhen().getEcho();
        } else if (model.getWhen().getExpectTimeout() != null) {
            return model.getWhen().getExpectTimeout();
        } else if (model.getWhen().getFail() != null) {
            return model.getWhen().getFail();
        } else if (model.getWhen().getGroovy() != null) {
            return model.getWhen().getGroovy();
        } else if (model.getWhen().getInput() != null) {
            return model.getWhen().getInput();
        } else if (model.getWhen().getIterate() != null) {
            return model.getWhen().getIterate();
        } else if (model.getWhen().getJava() != null) {
            return model.getWhen().getJava();
        } else if (model.getWhen().getLoad() != null) {
            return model.getWhen().getLoad();
        } else if (model.getWhen().getParallel() != null) {
            return model.getWhen().getParallel();
        } else if (model.getWhen().getPlsql() != null) {
            return model.getWhen().getPlsql();
        } else if (model.getWhen().getPurgeChannel() != null) {
            return model.getWhen().getPurgeChannel();
        } else if (model.getWhen().getPurgeEndpoint() != null) {
            return model.getWhen().getPurgeEndpoint();
        } else if (model.getWhen().getReceive() != null) {
            return model.getWhen().getReceive();
        } else if (model.getWhen().getRepeatOnerrorUntilTrue() != null) {
            return model.getWhen().getRepeatOnerrorUntilTrue();
        } else if (model.getWhen().getRepeatUntilTrue() != null) {
            return model.getWhen().getRepeatUntilTrue();
        } else if (model.getWhen().getSend() != null) {
            return model.getWhen().getSend();
        } else if (model.getWhen().getSequential() != null) {
            return model.getWhen().getSequential();
        } else if (model.getWhen().getSleep() != null) {
            return model.getWhen().getSleep();
        } else if (model.getWhen().getSql() != null) {
            return model.getWhen().getSql();
        } else if (model.getWhen().getStart() != null) {
            return model.getWhen().getStart();
        } else if (model.getWhen().getStop() != null) {
            return model.getWhen().getStop();
        } else if (model.getWhen().getStopTimer() != null) {
            return model.getWhen().getStopTimer();
        } else if (model.getWhen().getTimer() != null) {
            return model.getWhen().getTimer();
        } else if (model.getWhen().getTraceTime() != null) {
            return model.getWhen().getTraceTime();
        } else if (model.getWhen().getTraceVariables() != null) {
            return model.getWhen().getTraceVariables();
        } else if (model.getWhen().getTransform() != null) {
            return model.getWhen().getTransform();
        } else if (model.getWhen().getWait() != null) {
            return model.getWhen().getWait();
        } else {
            return model.getWhen().getAny();
        }
    }

    @Override
    public Class<AssertSoapFault> getActionModelClass() {
        return AssertSoapFault.class;
    }

    @Override
    public Class<AssertFaultModel> getSourceModelClass() {
        return AssertFaultModel.class;
    }
}
