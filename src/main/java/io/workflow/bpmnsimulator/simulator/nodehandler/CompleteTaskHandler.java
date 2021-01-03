package io.workflow.bpmnsimulator.simulator.nodehandler;

import io.workflow.bpmnsimulator.model.ProcessSimulationRequest;
import io.workflow.bpmnsimulator.service.TaskInstanceService;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.spring.boot.starter.event.TaskEvent;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

@Component
@RequiredArgsConstructor
class CompleteTaskHandler implements TaskAssignedHandler, Ordered {

    private final TaskInstanceService taskInstanceService;

    @Override
    public void onTaskAssigned(@Nonnull final ProcessSimulationRequest processSimulationRequest,
                               @Nonnull final TaskEvent taskEvent) {
        taskInstanceService.complete(taskEvent.getId());
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}
