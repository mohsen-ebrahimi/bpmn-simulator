package io.workflow.bpmnsimulator.listener.handler;

import io.workflow.bpmnsimulator.model.ProcessSimulationRequest;
import org.camunda.bpm.spring.boot.starter.event.ExecutionEvent;

import javax.annotation.Nonnull;

public interface NodeTakenHandler {

    void onNodeTaken(@Nonnull final ProcessSimulationRequest processSimulationRequest,
                     @Nonnull final ExecutionEvent executionEvent);
}
