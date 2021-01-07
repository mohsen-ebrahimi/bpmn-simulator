package io.bpmnsimulator.core.model;

import lombok.Builder;
import lombok.Data;

import javax.annotation.Nonnull;
import java.util.List;

@Data
@Builder
public class ProcessSimulationResult {

    @Nonnull
    private final String bpmnUrl;

    @Nonnull
    private final List<ProcessSimulationError> errors;

}
