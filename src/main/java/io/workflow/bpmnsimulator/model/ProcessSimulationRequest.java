package io.workflow.bpmnsimulator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class ProcessSimulationRequest {

    @Nonnull
    private String bpmnUrl;

    @Nonnull
    private String deploymentName;

    @Nonnull
    private List<Step> steps = new ArrayList<>();
}
