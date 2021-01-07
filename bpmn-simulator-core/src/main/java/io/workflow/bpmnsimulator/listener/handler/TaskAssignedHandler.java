package io.workflow.bpmnsimulator.listener.handler;

import io.workflow.bpmnsimulator.model.ProcessSimulationRequest;
import org.camunda.bpm.engine.delegate.DelegateTask;

import javax.annotation.Nonnull;

public interface TaskAssignedHandler {

    void onTaskAssigned(@Nonnull final ProcessSimulationRequest processSimulationRequest,
                        @Nonnull final DelegateTask delegateTask);
}
