package io.workflow.bpmnsimulator.simulator;

import io.workflow.bpmnsimulator.model.ProcessSimulationRequest;
import org.camunda.bpm.engine.runtime.ProcessInstance;

import javax.annotation.Nonnull;

public interface ProcessSimulator {

    @Nonnull
    ProcessInstance startSimulation(@Nonnull ProcessSimulationRequest processSimulationRequest);

}
