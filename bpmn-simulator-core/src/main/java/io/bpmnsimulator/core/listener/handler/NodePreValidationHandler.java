package io.bpmnsimulator.core.listener.handler;

import io.bpmnsimulator.core.model.*;
import io.bpmnsimulator.core.service.TaskInstanceService;
import io.bpmnsimulator.core.simulator.ProcessSimulationContextHolder;
import io.bpmnsimulator.core.validator.prevalidator.PreValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
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
                               @Nonnull final DelegateTask delegateTask) {
        final List<ProcessSimulationError> simulationErrors = findStep(processSimulationRequest, delegateTask)
                .map(step -> validateStep(step, delegateTask.getId()))
                .orElse(List.of());

        final ProcessSimulationResult simulationResult = ProcessSimulationContextHolder.getProcessSimulationResult();
        simulationResult.getErrors().addAll(simulationErrors);
        log.info("Pre-validation for task: [{}] with request: [{}] finished with result: [{}]",
                delegateTask.getId(), processSimulationRequest, simulationResult);
    }

    private Optional<Step> findStep(@Nonnull final ProcessSimulationRequest processSimulationRequest,
                                    @Nonnull final DelegateTask delegateTask) {
        return processSimulationRequest.getSteps()
                .stream()
                .filter(step -> step.getId().equals(delegateTask.getTaskDefinitionKey()))
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
