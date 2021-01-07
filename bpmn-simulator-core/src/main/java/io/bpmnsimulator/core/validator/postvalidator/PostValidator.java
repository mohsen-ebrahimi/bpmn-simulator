package io.bpmnsimulator.core.validator.postvalidator;

import io.bpmnsimulator.core.model.ProcessSimulationError;
import io.bpmnsimulator.core.model.Step;
import org.camunda.bpm.engine.delegate.DelegateExecution;

import javax.annotation.Nonnull;
import java.util.List;

public interface PostValidator {

    List<ProcessSimulationError> validate(@Nonnull final Step step, @Nonnull final DelegateExecution delegateExecution);

}
