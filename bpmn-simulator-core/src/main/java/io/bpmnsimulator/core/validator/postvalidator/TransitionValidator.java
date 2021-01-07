package io.bpmnsimulator.core.validator.postvalidator;

import io.bpmnsimulator.core.model.Field;
import io.bpmnsimulator.core.model.PostCondition;
import io.bpmnsimulator.core.model.ProcessSimulationError;
import io.bpmnsimulator.core.model.Step;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

@Slf4j
@Component
class TransitionValidator implements PostValidator {
    @Override
    public List<ProcessSimulationError> validate(@Nonnull final Step step, @Nonnull final DelegateExecution delegateExecution) {
        final PostCondition postCondition = requireNonNull(step.getPostCondition(), String.format("PostCondition of step cannot be null. [%s]", step));
        final String expectedTransition = postCondition.getTransition();
        final String actualTransition = delegateExecution.getCurrentTransitionId();

        if (expectedTransition == null) {
            log.debug("Ignoring TransitionValidator because transition is null. [{}]", step);
            return List.of();
        }

        final boolean isTransitionValid = Objects.equals(expectedTransition, actualTransition);
        if (isTransitionValid) {
            return List.of();
        }

        final ProcessSimulationError simulationError = ProcessSimulationError.builder()
                .stepId(step.getId())
                .field(Field.TRANSITION)
                .expectedFieldValue(expectedTransition)
                .actualFieldValue(actualTransition)
                .build();
        log.debug("'Transition' field validation failed: [{}]", simulationError);
        return List.of(simulationError);
    }
}
