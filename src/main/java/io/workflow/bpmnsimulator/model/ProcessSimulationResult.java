package io.workflow.bpmnsimulator.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProcessSimulationResult {

    private final String bpmnUrl;

    private final List<ProcessSimulationError> errors;
}
