package io.workflow.bpmnsimulator.validator.prevalidator;

import io.workflow.bpmnsimulator.model.ProcessSimulationError;
import io.workflow.bpmnsimulator.model.Step;
import org.camunda.bpm.engine.task.Task;

import javax.annotation.Nonnull;
import java.util.List;

public interface PreValidator {

    List<ProcessSimulationError> validate(@Nonnull final Step step, @Nonnull final Task task);

}
