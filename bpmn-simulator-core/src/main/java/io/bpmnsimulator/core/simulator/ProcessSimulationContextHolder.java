package io.bpmnsimulator.core.simulator;

import io.bpmnsimulator.core.model.ProcessSimulationRequest;
import io.bpmnsimulator.core.model.ProcessSimulationResult;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;
import java.util.ArrayList;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class ProcessSimulationContextHolder {

    private static final ThreadLocal<ProcessSimulationContext> PROCESS_SIMULATION_REQUEST_HOLDER = new ThreadLocal<>();

    public static void init(@Nonnull final ProcessSimulationRequest simulationRequest) {
        final ProcessSimulationResult processSimulationResult = ProcessSimulationResult.builder()
                .bpmnUrl(simulationRequest.getBpmnUrl())
                .errors(new ArrayList<>())
                .build();

        final ProcessSimulationContext simulationContext = new ProcessSimulationContext(simulationRequest, processSimulationResult);
        PROCESS_SIMULATION_REQUEST_HOLDER.set(simulationContext);
    }

    @Nonnull
    public static ProcessSimulationRequest getProcessSimulationRequest() {
        return PROCESS_SIMULATION_REQUEST_HOLDER.get().getProcessSimulationRequest();
    }

    @Nonnull
    public static ProcessSimulationResult getProcessSimulationResult() {
        return PROCESS_SIMULATION_REQUEST_HOLDER.get().getProcessSimulationResult();
    }

    //TODO: clear context after simulation completed
    public static void remove() {
        PROCESS_SIMULATION_REQUEST_HOLDER.remove();
    }

    @Data
    private static class ProcessSimulationContext {

        private final ProcessSimulationRequest processSimulationRequest;

        private final ProcessSimulationResult processSimulationResult;
    }
}
