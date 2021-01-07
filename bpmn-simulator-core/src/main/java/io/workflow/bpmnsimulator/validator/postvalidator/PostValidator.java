package io.workflow.bpmnsimulator.validator.postvalidator;

import io.workflow.bpmnsimulator.model.ProcessSimulationError;
import io.workflow.bpmnsimulator.model.Step;
import org.camunda.bpm.engine.delegate.DelegateExecution;

import javax.annotation.Nonnull;
import java.util.List;

public interface PostValidator {

    List<ProcessSimulationError> validate(@Nonnull final Step step, @Nonnull final DelegateExecution delegateExecution);

}
