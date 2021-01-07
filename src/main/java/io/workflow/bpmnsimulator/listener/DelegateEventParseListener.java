package io.workflow.bpmnsimulator.listener;

import org.camunda.bpm.engine.delegate.ExecutionListener;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.camunda.bpm.engine.impl.bpmn.behavior.UserTaskActivityBehavior;
import org.camunda.bpm.engine.impl.bpmn.parser.AbstractBpmnParseListener;
import org.camunda.bpm.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.camunda.bpm.engine.impl.pvm.process.ActivityImpl;
import org.camunda.bpm.engine.impl.pvm.process.ScopeImpl;
import org.camunda.bpm.engine.impl.pvm.process.TransitionImpl;
import org.camunda.bpm.engine.impl.task.TaskDefinition;
import org.camunda.bpm.engine.impl.util.xml.Element;
import org.springframework.context.ApplicationEventPublisher;

import java.util.List;

import static org.camunda.bpm.engine.delegate.ExecutionListener.*;
import static org.camunda.bpm.engine.delegate.TaskListener.*;

public class DelegateEventParseListener extends AbstractBpmnParseListener {

    private static final List<String> TASK_EVENTS = List.of(
            EVENTNAME_COMPLETE,
            EVENTNAME_ASSIGNMENT,
            EVENTNAME_CREATE,
            EVENTNAME_DELETE,
            EVENTNAME_UPDATE);
    private static final List<String> EXECUTION_EVENTS = List.of(
            EVENTNAME_START,
            EVENTNAME_END);

    private final TaskListener taskListener;

    private final ExecutionListener executionListener;

    public DelegateEventParseListener(final ApplicationEventPublisher publisher) {
        this.taskListener = publisher::publishEvent;
        this.executionListener = publisher::publishEvent;
    }

    @Override
    public void parseUserTask(Element userTaskElement, ScopeImpl scope, ActivityImpl activity) {
        addTaskListener(taskDefinition(activity));
        addExecutionListener(activity);
    }

    @Override
    public void parseBoundaryErrorEventDefinition(Element errorEventDefinition, boolean interrupting, ActivityImpl activity, ActivityImpl nestedErrorEventActivity) {
        addExecutionListener(activity);
    }

    @Override
    public void parseBoundaryEvent(Element boundaryEventElement, ScopeImpl scopeElement, ActivityImpl activity) {
        addExecutionListener(activity);
    }

    @Override
    public void parseBoundaryMessageEventDefinition(Element element, boolean interrupting, ActivityImpl activity) {
        addExecutionListener(activity);
    }

    @Override
    public void parseBoundarySignalEventDefinition(Element signalEventDefinition, boolean interrupting, ActivityImpl activity) {
        addExecutionListener(activity);
    }

    @Override
    public void parseBoundaryTimerEventDefinition(Element timerEventDefinition, boolean interrupting, ActivityImpl activity) {
        addExecutionListener(activity);
    }

    @Override
    public void parseBusinessRuleTask(Element businessRuleTaskElement, ScopeImpl scope, ActivityImpl activity) {
        addExecutionListener(activity);
    }

    @Override
    public void parseCallActivity(Element callActivityElement, ScopeImpl scope, ActivityImpl activity) {
        addExecutionListener(activity);
    }

    @Override
    public void parseCompensateEventDefinition(Element compensateEventDefinition, ActivityImpl activity) {
        addExecutionListener(activity);
    }

    @Override
    public void parseEndEvent(Element endEventElement, ScopeImpl scope, ActivityImpl activity) {
        addExecutionListener(activity);
    }

    @Override
    public void parseEventBasedGateway(Element eventBasedGwElement, ScopeImpl scope, ActivityImpl activity) {
        addExecutionListener(activity);
    }

    @Override
    public void parseExclusiveGateway(Element exclusiveGwElement, ScopeImpl scope, ActivityImpl activity) {
        addExecutionListener(activity);
    }

    @Override
    public void parseInclusiveGateway(Element inclusiveGwElement, ScopeImpl scope, ActivityImpl activity) {
        addExecutionListener(activity);
    }

    @Override
    public void parseIntermediateCatchEvent(Element intermediateEventElement, ScopeImpl scope, ActivityImpl activity) {
        addExecutionListener(activity);
    }

    @Override
    public void parseIntermediateMessageCatchEventDefinition(Element messageEventDefinition, ActivityImpl activity) {
        addExecutionListener(activity);
    }

    @Override
    public void parseIntermediateSignalCatchEventDefinition(Element signalEventDefinition, ActivityImpl activity) {
        addExecutionListener(activity);
    }

    @Override
    public void parseIntermediateThrowEvent(Element intermediateEventElement, ScopeImpl scope, ActivityImpl activity) {
        addExecutionListener(activity);
    }

    @Override
    public void parseIntermediateTimerEventDefinition(Element timerEventDefinition, ActivityImpl activity) {
        addExecutionListener(activity);
    }

    @Override
    public void parseManualTask(Element manualTaskElement, ScopeImpl scope, ActivityImpl activity) {
        addExecutionListener(activity);
    }

    @Override
    public void parseMultiInstanceLoopCharacteristics(Element activityElement, Element multiInstanceLoopCharacteristicsElement, ActivityImpl activity) {
        // DO NOT IMPLEMENT
        // we do not notify on entering a multi-instance activity, this will be done for every single execution inside that loop.
    }

    @Override
    public void parseParallelGateway(Element parallelGwElement, ScopeImpl scope, ActivityImpl activity) {
        addExecutionListener(activity);
    }

    @Override
    public void parseProcess(Element processElement, ProcessDefinitionEntity processDefinition) {
        if (executionListener != null) {
            for (String event : EXECUTION_EVENTS) {
                processDefinition.addListener(event, executionListener);
            }
        }
    }

    @Override
    public void parseReceiveTask(Element receiveTaskElement, ScopeImpl scope, ActivityImpl activity) {
        addExecutionListener(activity);
    }

    @Override
    public void parseScriptTask(Element scriptTaskElement, ScopeImpl scope, ActivityImpl activity) {
        addExecutionListener(activity);
    }

    @Override
    public void parseSendTask(Element sendTaskElement, ScopeImpl scope, ActivityImpl activity) {
        addExecutionListener(activity);
    }

    @Override
    public void parseSequenceFlow(Element sequenceFlowElement, ScopeImpl scopeElement, TransitionImpl transition) {
        addExecutionListener(transition);
    }

    @Override
    public void parseServiceTask(Element serviceTaskElement, ScopeImpl scope, ActivityImpl activity) {
        addExecutionListener(activity);
    }

    @Override
    public void parseStartEvent(Element startEventElement, ScopeImpl scope, ActivityImpl activity) {
        addExecutionListener(activity);
    }

    @Override
    public void parseSubProcess(Element subProcessElement, ScopeImpl scope, ActivityImpl activity) {
        addExecutionListener(activity);
    }

    @Override
    public void parseTask(Element taskElement, ScopeImpl scope, ActivityImpl activity) {
        addExecutionListener(activity);
    }

    @Override
    public void parseTransaction(Element transactionElement, ScopeImpl scope, ActivityImpl activity) {
        addExecutionListener(activity);
    }

    private void addExecutionListener(final ActivityImpl activity) {
        for (String event : EXECUTION_EVENTS) {
            activity.addListener(event, executionListener);
        }
    }

    private void addExecutionListener(final TransitionImpl transition) {
        transition.addListener(EVENTNAME_TAKE, executionListener);
    }

    private void addTaskListener(TaskDefinition taskDefinition) {
        for (String event : TASK_EVENTS) {
            taskDefinition.addTaskListener(event, taskListener);
        }
    }

    private TaskDefinition taskDefinition(final ActivityImpl activity) {
        final UserTaskActivityBehavior activityBehavior = (UserTaskActivityBehavior) activity.getActivityBehavior();
        return activityBehavior.getTaskDefinition();
    }
}
