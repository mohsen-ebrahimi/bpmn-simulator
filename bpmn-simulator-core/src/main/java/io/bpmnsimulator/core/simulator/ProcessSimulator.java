package io.bpmnsimulator.core.simulator;

import io.bpmnsimulator.core.model.ProcessSimulationRequest;
import io.bpmnsimulator.core.model.ProcessSimulationResult;

import javax.annotation.Nonnull;

public interface ProcessSimulator {

    ProcessSimulationResult simulate(@Nonnull ProcessSimulationRequest processSimulationRequest);

}
