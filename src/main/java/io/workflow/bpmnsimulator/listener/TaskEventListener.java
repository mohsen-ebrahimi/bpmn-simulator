package io.workflow.bpmnsimulator.listener;

import io.workflow.bpmnsimulator.model.ProcessSimulationRequest;
import io.workflow.bpmnsimulator.simulator.nodehandler.TaskAssignedHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.spring.boot.starter.event.TaskEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

import static io.workflow.bpmnsimulator.simulator.ProcessSimulationContextHolder.getProcessSimulationRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskEventListener {

    private static final String ASSIGNMENT_EVENT_NAME = "assignment";

    private final List<TaskAssignedHandler> taskAssignedHandlers;

    @EventListener
    public void execute(TaskEvent taskEvent) {
        if (ASSIGNMENT_EVENT_NAME.equals(taskEvent.getEventName())) {
            final ProcessSimulationRequest processSimulationRequest = getProcessSimulationRequest();
            taskAssignedHandlers.forEach(taskAssignedHandler ->
                    taskAssignedHandler.onTaskAssigned(processSimulationRequest, taskEvent));
        }
    }
}
