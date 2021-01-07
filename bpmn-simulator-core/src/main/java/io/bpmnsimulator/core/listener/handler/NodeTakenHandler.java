package io.bpmnsimulator.core.listener.handler;

import io.bpmnsimulator.core.model.ProcessSimulationRequest;
import org.camunda.bpm.engine.delegate.DelegateExecution;

import javax.annotation.Nonnull;

public interface NodeTakenHandler {

    void onNodeTaken(@Nonnull final ProcessSimulationRequest processSimulationRequest,
                     @Nonnull final DelegateExecution delegateExecution);
}
