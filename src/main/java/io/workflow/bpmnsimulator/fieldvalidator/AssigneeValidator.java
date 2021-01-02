package io.workflow.bpmnsimulator.fieldvalidator;

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
class AssigneeValidator implements Validator {

    @Override
    public List<ProcessSimulationError> validate(@Nonnull final Step step, @Nonnull final Task task) {
        final boolean isValid = Objects.equals(task.getAssignee(), step.getAssignee());
        if (!isValid) {
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
