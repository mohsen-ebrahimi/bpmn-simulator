package io.workflow.bpmnsimulator.fieldvalidator;

import io.workflow.bpmnsimulator.model.Field;
import lombok.Builder;
import lombok.Data;

import javax.annotation.Nonnull;

@Data
@Builder
public class FieldValidationResult {

    private final boolean isValid;

    @Nonnull
    private final Field field;

    @Nonnull
    private final String stepFieldValue;

    @Nonnull
    private final String taskFieldValue;

}
