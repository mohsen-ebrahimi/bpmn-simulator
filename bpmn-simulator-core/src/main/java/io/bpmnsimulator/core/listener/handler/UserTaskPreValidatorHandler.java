package io.bpmnsimulator.core.listener.handler;

import io.bpmnsimulator.core.model.ProcessSimulationError;
import io.bpmnsimulator.core.model.ProcessSimulationRequest;
import io.bpmnsimulator.core.model.ProcessSimulationResult;
import io.bpmnsimulator.core.model.Step;
import io.bpmnsimulator.core.simulator.ProcessSimulationContextHolder;
import io.bpmnsimulator.core.validator.UserTaskValidatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateTask;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
class UserTaskPreValidatorHandler implements TaskAssignedHandler {

    private final UserTaskValidatorService userTaskValidatorService;

    @Override
    public void onTaskAssigned(@Nonnull final ProcessSimulationRequest processSimulationRequest,
                               @Nonnull final DelegateTask delegateTask) {
        final List<ProcessSimulationError> simulationErrors = findStep(processSimulationRequest, delegateTask)
                .map(step -> userTaskValidatorService.preValidateUserTask(step, delegateTask.getId()))
                .orElse(List.of());

        final ProcessSimulationResult simulationResult = ProcessSimulationContextHolder.getProcessSimulationResult();
        simulationResult.getErrors().addAll(simulationErrors);
        log.info("Pre-validation for task: [{}] with request: [{}] finished with result: [{}]",
                delegateTask.getId(), processSimulationRequest, simulationResult);
    }

    private Optional<Step> findStep(@Nonnull final ProcessSimulationRequest processSimulationRequest,
                                    @Nonnull final DelegateTask delegateTask) {
        final String taskDefinitionKey = delegateTask.getTaskDefinitionKey();

        final Optional<Step> step = processSimulationRequest.getSteps()
                .stream()
                .filter(processStep -> processStep.getId().equals(taskDefinitionKey))
                .findAny();
        step.ifPresentOrElse(
                processStep -> log.debug("Step: [{}] found with task name: [{}]", processStep, taskDefinitionKey),
                () -> log.debug("No step found with task name: [{}]", taskDefinitionKey));

        return step;
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

}
