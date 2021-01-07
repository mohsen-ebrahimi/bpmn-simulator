package io.workflow.bpmnsimulator.validator.prevalidator;

import io.workflow.bpmnsimulator.model.Field;
import io.workflow.bpmnsimulator.model.ProcessSimulationError;
import io.workflow.bpmnsimulator.model.Step;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
class NameValidator implements PreValidator {

    @Override
    public List<ProcessSimulationError> validate(@Nonnull final Step step, @Nonnull final Task task) {
        final boolean isNameValid = Objects.equals(task.getName(), step.getName());

        if (!isNameValid) {
            final ProcessSimulationError simulationError = ProcessSimulationError.builder()
                    .stepId(step.getId())
                    .field(Field.NAME)
                    .expectedFieldValue(step.getName())
                    .actualFieldValue(task.getName())
                    .build();
            log.debug("'Name' field validation failed: [{}]", simulationError);

            return List.of(simulationError);
        }

        return List.of();
    }
}
