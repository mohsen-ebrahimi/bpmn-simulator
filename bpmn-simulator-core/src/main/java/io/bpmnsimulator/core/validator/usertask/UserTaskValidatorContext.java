package io.bpmnsimulator.core.validator.usertask;

import io.bpmnsimulator.core.model.Condition;
import lombok.Builder;
import lombok.Data;
import org.camunda.bpm.engine.task.Task;

import javax.annotation.Nonnull;

@Data
@Builder
public class UserTaskValidatorContext<T> {

    @Nonnull
    private final Task task;

    @Nonnull
    private String stepId;

    @Nonnull
    private Condition<T> condition;

}
