package io.bpmnsimulator.core.validator;

import io.bpmnsimulator.core.model.Condition;
import io.bpmnsimulator.core.model.Field;
import io.bpmnsimulator.core.model.ProcessSimulationError;
import io.bpmnsimulator.core.model.Step;
import io.bpmnsimulator.core.service.TaskInstanceService;
import io.bpmnsimulator.core.validator.usertask.UserTaskValidator;
import io.bpmnsimulator.core.validator.usertask.UserTaskValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;

@Slf4j
@Service
public class UserTaskValidatorService {

    private final TaskInstanceService taskInstanceService;

    private final Map<Field, List<UserTaskValidator>> userTaskValidators;

    public UserTaskValidatorService(@Nonnull final TaskInstanceService taskInstanceService,
                                    @Nonnull final List<UserTaskValidator<?>> userTaskValidators) {
        this.taskInstanceService = taskInstanceService;
        this.userTaskValidators = userTaskValidators.stream()
                .collect(groupingBy(Validator::getSupportedField));
    }

    @Nonnull
    public List<ProcessSimulationError> preValidateUserTask(@Nonnull final Step step, @Nonnull final String taskId) {
        log.debug("Pre-validating taskId: [{}] with step: [{}]", taskId, step);
        final List<ProcessSimulationError> processSimulationErrors = validateUserTask(step, taskId, step.getPreconditions());
        log.info("Pre-validating step: [{}] completed with result: [{}]", step.getId(), processSimulationErrors);

        return processSimulationErrors;
    }

    @Nonnull
    public List<ProcessSimulationError> postValidateUserTask(@Nonnull final Step step, @Nonnull final String taskId) {
        log.debug("Post-validating taskId: [{}] with step: [{}]", taskId, step);
        final List<ProcessSimulationError> processSimulationErrors = validateUserTask(step, taskId, step.getPostConditions());
        log.info("post-validating step: [{}] completed with result: [{}]", step.getId(), processSimulationErrors);

        return processSimulationErrors;
    }

    @Nonnull
    private List<ProcessSimulationError> validateUserTask(@Nonnull final Step step,
                                                          @Nonnull final String taskId,
                                                          @Nonnull final List<? extends Condition<?>> conditions) {
        return taskInstanceService.getTask(taskId)
                .map(task -> validateAllConditions(step, task, conditions))
                .orElseGet(() -> createIdSimulationError(step));
    }

    @Nonnull
    private List<ProcessSimulationError> validateAllConditions(@Nonnull final Step step,
                                                               @Nonnull final Task task,
                                                               @Nonnull final List<? extends Condition<?>> conditions) {
        return conditions.stream()
                .map(condition -> toUserTaskValidationContext(step, task, condition))
                .flatMap(this::validateCondition)
                .collect(Collectors.toList());
    }

    @Nonnull
    private <T> UserTaskValidatorContext<T> toUserTaskValidationContext(@Nonnull final Step step,
                                                                        @Nonnull final Task task,
                                                                        @Nonnull final Condition<T> condition) {
        return UserTaskValidatorContext.<T>builder()
                .condition(condition)
                .stepId(step.getId())
                .task(task)
                .build();
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    private Stream<ProcessSimulationError> validateCondition(@Nonnull final UserTaskValidatorContext<?> context) {
        return getUserTaskValidators(context.getCondition().getField()).stream()
                .flatMap(validator -> validator.validate(context).stream());
    }

    @Nonnull
    private List<ProcessSimulationError> createIdSimulationError(@Nonnull final Step step) {
        log.warn("Creating a simulation error because no task found for step: [{}]", step);

        return List.of(ProcessSimulationError.builder()
                .stepId(step.getId())
                .field(Field.ID)
                .expectedFieldValue(step.getId())
                .actualFieldValue(null)
                .build());
    }

    @Nonnull
    private List<UserTaskValidator> getUserTaskValidators(@Nonnull final Field field) {
        return userTaskValidators.getOrDefault(field, List.of());
    }

}
