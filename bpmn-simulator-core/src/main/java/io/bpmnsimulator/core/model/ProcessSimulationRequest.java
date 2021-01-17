package io.bpmnsimulator.core.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Data
@NoArgsConstructor(access = PROTECTED)
public class ProcessSimulationRequest {

    @Nonnull
    private String bpmnUrl;

    @Nonnull
    private String deploymentName;

    @Nonnull
    @ToString.Exclude
    private List<Step> steps = new ArrayList<>();
}
