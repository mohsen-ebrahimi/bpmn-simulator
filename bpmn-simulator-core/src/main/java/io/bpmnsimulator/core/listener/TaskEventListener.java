package io.bpmnsimulator.core.listener;

import io.bpmnsimulator.core.listener.handler.TaskAssignedHandler;
import io.bpmnsimulator.core.model.ProcessSimulationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;

import static io.bpmnsimulator.core.simulator.ProcessSimulationContextHolder.getProcessSimulationRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class TaskEventListener {

    private static final String CREATE_EVENT_NAME = "create";

    private final List<TaskAssignedHandler> taskAssignedHandlers;

    @EventListener
    public void execute(@Nonnull final DelegateTask delegateTask) {
        if (CREATE_EVENT_NAME.equals(delegateTask.getEventName())) {
            final ProcessSimulationRequest processSimulationRequest = getProcessSimulationRequest();
            taskAssignedHandlers.forEach(taskAssignedHandler ->
                    taskAssignedHandler.onTaskAssigned(processSimulationRequest, delegateTask));
        }
    }
}
