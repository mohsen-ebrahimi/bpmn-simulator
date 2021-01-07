package io.workflow.bpmnsimulator.model;

import lombok.Builder;
import lombok.Data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Data
@Builder
public class ProcessSimulationError {

    @Nonnull
    private final String stepId;

    @Nonnull
    private final Field field;

    @Nullable
    private final String expectedFieldValue;

    @Nullable
    private final String actualFieldValue;
}
