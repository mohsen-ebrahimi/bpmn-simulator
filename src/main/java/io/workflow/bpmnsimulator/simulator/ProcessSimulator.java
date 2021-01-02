package io.workflow.bpmnsimulator.simulator;

import io.workflow.bpmnsimulator.model.ProcessSimulationResult;
import io.workflow.bpmnsimulator.model.ProcessSimulationRequest;

import javax.annotation.Nonnull;

public interface ProcessSimulator {

    @Nonnull
    ProcessSimulationResult simulate(@Nonnull ProcessSimulationRequest processSimulationRequest);

}
