package io.bpmnsimulator.core.validator.usertask;

import io.bpmnsimulator.core.model.Condition;
import io.bpmnsimulator.core.model.Field;
import io.bpmnsimulator.core.model.ProcessSimulationError;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

import static io.bpmnsimulator.core.model.Field.NAME;

@Slf4j
@Component
class NameValidator implements UserTaskValidator<String> {

    @Nonnull
    @Override
    public List<ProcessSimulationError> validate(@Nonnull final UserTaskValidatorContext<String> context) {
        final Task task = context.getTask();
        final Condition<String> condition = context.getCondition();
        final boolean isNameValid = Objects.equals(task.getName(), condition.getExpectedValue());

        if (!isNameValid) {
            final ProcessSimulationError simulationError = ProcessSimulationError.builder()
                    .stepId(context.getStepId())
                    .field(NAME)
                    .expectedFieldValue(String.valueOf(condition.getExpectedValue()))
                    .actualFieldValue(task.getName())
                    .build();
            log.info("'Name' field validation failed: [{}]", simulationError);

            return List.of(simulationError);
        }

        return List.of();
    }

    @Nonnull
    @Override
    public Field getSupportedField() {
        return NAME;
    }

}
