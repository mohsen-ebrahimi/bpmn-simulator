package io.workflow.bpmnsimulator.listener.handler;

import io.workflow.bpmnsimulator.model.*;
import io.workflow.bpmnsimulator.service.TaskInstanceService;
import io.workflow.bpmnsimulator.simulator.ProcessSimulationContextHolder;
import io.workflow.bpmnsimulator.validator.prevalidator.PreValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.spring.boot.starter.event.TaskEvent;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
class NodePreValidationHandler implements TaskAssignedHandler, Ordered {

    private final TaskInstanceService taskInstanceService;

    private final List<PreValidator> preValidators;

    @Override
    public void onTaskAssigned(@Nonnull final ProcessSimulationRequest processSimulationRequest,
                               @Nonnull final TaskEvent taskEvent) {
        final List<ProcessSimulationError> simulationErrors = findStep(processSimulationRequest, taskEvent)
                .map(step -> validateStep(step, taskEvent.getId()))
                .orElse(List.of());

        final ProcessSimulationResult simulationResult = ProcessSimulationContextHolder.getProcessSimulationResult();
        simulationResult.getErrors().addAll(simulationErrors);
        log.info("Pre-validation for task: [{}] with request: [{}] finished with result: [{}]",
                taskEvent.getId(), processSimulationRequest, simulationResult);
    }

    private Optional<Step> findStep(@Nonnull final ProcessSimulationRequest processSimulationRequest,
                                    @Nonnull final TaskEvent taskEvent) {
        return processSimulationRequest.getSteps()
                .stream()
                .filter(step -> step.getId().equals(taskEvent.getTaskDefinitionKey()))
                .findAny();
    }

    private List<ProcessSimulationError> validateStep(@Nonnull final Step step, @Nonnull final String taskId) {
        return taskInstanceService.getTask(taskId)
                .map(task -> preValidators.stream()
                        .flatMap(preValidator -> preValidator.validate(step, task).stream())
                        .collect(Collectors.toList()))
                .orElseGet(() -> createIdSimulationError(step));
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
