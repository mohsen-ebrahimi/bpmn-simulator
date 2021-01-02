package io.workflow.bpmnsimulator.model;

import lombok.Data;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Data(staticConstructor = "create")
public class ProcessSimulationError {

    @Nonnull
    private final String stepId;

    @Nonnull
    private final Field field;

    @Nullable
    private final String actualFieldValue;

    @Nullable
    private final String expectedFieldValue;

    public enum Field {
        ID,
        NAME,
        ASSIGNEE,
        PROCESS_VARIABLE,
        ;
    }
}
