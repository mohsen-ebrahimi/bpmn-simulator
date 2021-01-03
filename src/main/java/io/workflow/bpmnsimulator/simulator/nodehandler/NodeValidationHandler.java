package io.workflow.bpmnsimulator.simulator.nodehandler;

import io.workflow.bpmnsimulator.validator.Validator;
import io.workflow.bpmnsimulator.model.*;
import io.workflow.bpmnsimulator.service.TaskInstanceService;
import io.workflow.bpmnsimulator.simulator.ProcessSimulationContextHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.spring.boot.starter.event.TaskEvent;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
class NodeValidationHandler implements TaskAssignedHandler, Ordered {

    private final TaskInstanceService taskInstanceService;

    private final List<Validator> validators;

    @Override
    public void onTaskAssigned(@Nonnull final ProcessSimulationRequest processSimulationRequest,
                               @Nonnull final TaskEvent taskEvent) {
        final List<ProcessSimulationError> simulationErrors = simulateSteps(processSimulationRequest, taskEvent);

        final ProcessSimulationResult simulationResult = ProcessSimulationContextHolder.getProcessSimulationResult();
        simulationResult.getErrors().addAll(simulationErrors);
        log.info("Simulating process instance: [{}] finished with result: [{}]",
                taskEvent.getProcessInstanceId(), simulationResult);
    }

    private List<ProcessSimulationError> simulateSteps(@Nonnull final ProcessSimulationRequest processSimulationRequest,
                                                       @Nonnull final TaskEvent taskEvent) {
        return processSimulationRequest.getSteps()
                .stream()
                .filter(step -> step.getId().equals(taskEvent.getTaskDefinitionKey()))
                .filter(step -> step.getPreCondition() != null)
                .findAny()
                .map(step -> validateStep(taskEvent.getId(), step))
                .orElse(List.of());
    }

    private List<ProcessSimulationError> validateStep(@Nonnull final String taskId, @Nonnull final Step step) {
        return taskInstanceService.loadTask(taskId)
                .map(task -> validateStep(step, task))
                .orElse(createIdSimulationError(step));
    }

    private List<ProcessSimulationError> validateStep(@Nonnull final Step step, @Nonnull final Task task) {
        return validators.stream()
                .flatMap(validator -> validator.validate(step, task).stream())
                .collect(Collectors.toList());
    }

    private List<ProcessSimulationError> createIdSimulationError(@Nonnull final Step step) {
        return List.of(ProcessSimulationError.builder()
                .stepId(step.getId())
                .field(Field.ID)
                .expectedFieldValue(step.getId())
                .actualFieldValue(null)
                .build());
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }
}
