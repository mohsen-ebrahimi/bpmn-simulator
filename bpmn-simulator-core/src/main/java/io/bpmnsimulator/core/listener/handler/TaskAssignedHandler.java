package io.bpmnsimulator.core.listener.handler;

import io.bpmnsimulator.core.model.ProcessSimulationRequest;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.springframework.core.Ordered;

import javax.annotation.Nonnull;

public interface TaskAssignedHandler extends Ordered {

    void onTaskAssigned(@Nonnull final ProcessSimulationRequest processSimulationRequest,
                        @Nonnull final DelegateTask delegateTask);
}
