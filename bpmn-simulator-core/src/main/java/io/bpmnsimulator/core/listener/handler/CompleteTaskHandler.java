package io.bpmnsimulator.core.listener.handler;

import io.bpmnsimulator.core.model.ProcessSimulationRequest;
import io.bpmnsimulator.core.service.TaskInstanceService;
import lombok.RequiredArgsConstructor;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

@Component
@RequiredArgsConstructor
class CompleteTaskHandler implements TaskAssignedHandler {

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
