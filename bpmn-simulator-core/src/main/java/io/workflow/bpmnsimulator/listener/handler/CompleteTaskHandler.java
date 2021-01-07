package io.workflow.bpmnsimulator.listener.handler;

import io.workflow.bpmnsimulator.model.ProcessSimulationRequest;
import io.workflow.bpmnsimulator.service.TaskInstanceService;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

@Component
@RequiredArgsConstructor
class CompleteTaskHandler implements TaskAssignedHandler, Ordered {

    private final TaskInstanceService taskInstanceService;

    @Override
    public void onTaskAssigned(@Nonnull final ProcessSimulationRequest processSimulationRequest,
                               @Nonnull final DelegateTask delegateTask) {
        taskInstanceService.complete(delegateTask.getId());
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}
