package io.workflow.bpmnsimulator.simulator.nodehandler;

import io.workflow.bpmnsimulator.model.ProcessSimulationRequest;
import org.camunda.bpm.spring.boot.starter.event.ExecutionEvent;

import javax.annotation.Nonnull;

public interface NodeEndedHandler {

    void onNodeEnded(@Nonnull final ProcessSimulationRequest processSimulationRequest,
                     @Nonnull final ExecutionEvent executionEvent);
}
