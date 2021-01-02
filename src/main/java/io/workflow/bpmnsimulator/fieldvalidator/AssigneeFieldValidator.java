package io.workflow.bpmnsimulator.fieldvalidator;

import io.workflow.bpmnsimulator.model.Field;
import io.workflow.bpmnsimulator.model.Step;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.Objects;

@Slf4j
@Service
class AssigneeFieldValidator implements FieldValidator {

    @Override
    public FieldValidationResult validate(@Nonnull final Step step, @Nonnull final Task task) {
        final boolean isValid = Objects.equals(task.getAssignee(), step.getAssignee());
        final FieldValidationResult validationResult = FieldValidationResult.builder()
                .isValid(isValid)
                .field(Field.ASSIGNEE)
                .stepFieldValue(step.getAssignee())
                .taskFieldValue(task.getAssignee())
                .build();
        log.debug("Result of 'Assignee' validation is: [{}]", validationResult);

        return validationResult;
    }
}
