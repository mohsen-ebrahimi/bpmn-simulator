package io.bpmnsimulator.core.validator.prevalidator;

import io.bpmnsimulator.core.model.ProcessSimulationError;
import io.bpmnsimulator.core.model.Step;
import org.camunda.bpm.engine.task.Task;

import javax.annotation.Nonnull;
import java.util.List;

public interface PreValidator {

    List<ProcessSimulationError> validate(@Nonnull final Step step, @Nonnull final Task task);

}
