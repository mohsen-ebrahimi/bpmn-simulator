package io.workflow.bpmnsimulator.fieldvalidator;

import io.workflow.bpmnsimulator.model.Step;
import org.camunda.bpm.engine.task.Task;

import javax.annotation.Nonnull;

public interface FieldValidator {

    FieldValidationResult validate(@Nonnull final Step step, @Nonnull final Task task);
}
