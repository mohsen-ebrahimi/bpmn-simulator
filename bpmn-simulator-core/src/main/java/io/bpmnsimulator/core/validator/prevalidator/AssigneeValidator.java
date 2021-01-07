package io.bpmnsimulator.core.validator.prevalidator;

import io.bpmnsimulator.core.model.Field;
import io.bpmnsimulator.core.model.ProcessSimulationError;
import io.bpmnsimulator.core.model.Step;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
class AssigneeValidator implements PreValidator {

    @Override
    public List<ProcessSimulationError> validate(@Nonnull final Step step, @Nonnull final Task task) {
        final boolean isAssigneeValid = Objects.equals(task.getAssignee(), step.getAssignee());
        if (!isAssigneeValid) {
            final ProcessSimulationError simulationError = ProcessSimulationError.builder()
                    .stepId(step.getId())
                    .field(Field.ASSIGNEE)
                    .expectedFieldValue(step.getAssignee())
                    .actualFieldValue(task.getAssignee())
                    .build();
            log.info("'Assignee' field validation failed: [{}]", simulationError);

            return List.of(simulationError);
        }

        return List.of();
    }
}
