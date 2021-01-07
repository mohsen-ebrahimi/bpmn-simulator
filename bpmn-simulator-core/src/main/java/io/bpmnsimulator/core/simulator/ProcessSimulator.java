package io.bpmnsimulator.core.simulator;

import io.bpmnsimulator.core.model.ProcessSimulationRequest;

import javax.annotation.Nonnull;

public interface ProcessSimulator {

    void startSimulation(@Nonnull ProcessSimulationRequest processSimulationRequest);

}
