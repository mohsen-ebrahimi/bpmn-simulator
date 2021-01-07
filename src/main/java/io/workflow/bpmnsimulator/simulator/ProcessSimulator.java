package io.workflow.bpmnsimulator.simulator;

import io.workflow.bpmnsimulator.model.ProcessSimulationRequest;

import javax.annotation.Nonnull;

public interface ProcessSimulator {

    void startSimulation(@Nonnull ProcessSimulationRequest processSimulationRequest);

}
