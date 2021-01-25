package io.bpmnsimulator.core.validator.node;

import io.bpmnsimulator.core.model.Condition;
import io.bpmnsimulator.core.model.Field;
import io.bpmnsimulator.core.model.ProcessSimulationError;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

import static io.bpmnsimulator.core.model.Field.TRANSITION;
import static java.util.Objects.requireNonNull;

@Slf4j
@Component
class TransitionValidator implements NodeValidator<String> {

    @Nonnull
    @Override
    public List<ProcessSimulationError> validate(@Nonnull final NodeValidatorContext<String> context) {
        final DelegateExecution delegateExecution = context.getDelegateExecution();
        final Condition<String> condition = context.getCondition();
        final String expectedTransition = requireNonNull(condition.getExpectedValue(),
                String.format("transition field cannot be null in step: [%s]", context.getStepId()));
        final String actualTransition = delegateExecution.getCurrentTransitionId();

        final boolean isTransitionValid = Objects.equals(expectedTransition, actualTransition);
        if (isTransitionValid) {
            return List.of();
        }

        final ProcessSimulationError simulationError = ProcessSimulationError.builder()
                .stepId(context.getStepId())
                .field(TRANSITION)
                .expectedFieldValue(expectedTransition)
                .actualFieldValue(actualTransition)
                .build();
        log.info("'Transition' field validation failed: [{}]", simulationError);
        return List.of(simulationError);
    }

    @Nonnull
    @Override
    public Field getSupportedField() {
        return TRANSITION;
    }

}
