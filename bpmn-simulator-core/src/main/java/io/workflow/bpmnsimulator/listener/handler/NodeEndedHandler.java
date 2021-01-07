package io.workflow.bpmnsimulator.listener.handler;

import io.workflow.bpmnsimulator.model.ProcessSimulationRequest;
import org.camunda.bpm.engine.delegate.DelegateExecution;

import javax.annotation.Nonnull;

public interface NodeEndedHandler {

    void onNodeEnded(@Nonnull final ProcessSimulationRequest processSimulationRequest,
                     @Nonnull final DelegateExecution delegateExecution);
}
