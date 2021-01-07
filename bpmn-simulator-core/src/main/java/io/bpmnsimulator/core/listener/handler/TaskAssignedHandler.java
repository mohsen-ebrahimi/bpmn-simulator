package io.bpmnsimulator.core.listener.handler;

import io.bpmnsimulator.core.model.ProcessSimulationRequest;
import org.camunda.bpm.engine.delegate.DelegateTask;

import javax.annotation.Nonnull;

public interface TaskAssignedHandler {

    void onTaskAssigned(@Nonnull final ProcessSimulationRequest processSimulationRequest,
                        @Nonnull final DelegateTask delegateTask);
}
