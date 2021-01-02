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
class NameFieldValidator implements FieldValidator {

    @Override
    public FieldValidationResult validate(@Nonnull final Step step, @Nonnull final Task task) {
        final boolean isValid = Objects.equals(task.getName(), step.getName());
        final FieldValidationResult validationResult = FieldValidationResult.builder()
                .isValid(isValid)
                .field(Field.NAME)
                .stepFieldValue(step.getName())
                .taskFieldValue(task.getName())
                .build();
        log.debug("Result of 'Name' validation is: [{}]", validationResult);

        return validationResult;
    }
}
