package io.bpmnsimulator.core.validator;

import io.bpmnsimulator.core.model.Field;
import io.bpmnsimulator.core.model.ProcessSimulationError;

import javax.annotation.Nonnull;
import java.util.List;

public interface Validator<T> {

    @Nonnull
    List<ProcessSimulationError> validate(@Nonnull T context);

    @Nonnull
    Field getSupportedField();

}
