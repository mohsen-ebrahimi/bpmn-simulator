package io.bpmnsimulator.core.listener.handler;

import io.bpmnsimulator.core.model.ProcessSimulationError;
import io.bpmnsimulator.core.model.ProcessSimulationRequest;
import io.bpmnsimulator.core.model.ProcessSimulationResult;
import io.bpmnsimulator.core.model.Step;
import io.bpmnsimulator.core.simulator.ProcessSimulationContextHolder;
import io.bpmnsimulator.core.validator.NodeValidatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
class NodePostValidationHandler implements NodeTakenHandler {

    private final NodeValidatorService nodeValidatorService;

    @Override
    public void onNodeTaken(@Nonnull final ProcessSimulationRequest processSimulationRequest,
                            @Nonnull final DelegateExecution delegateExecution) {
        final List<ProcessSimulationError> simulationErrors = findStep(processSimulationRequest, delegateExecution)
                .map(step -> nodeValidatorService.postValidateNode(step, delegateExecution))
                .orElse(List.of());

        final ProcessSimulationResult simulationResult = ProcessSimulationContextHolder.getProcessSimulationResult();
        simulationResult.getErrors().addAll(simulationErrors);
        log.info("Post-validation for node: [{}] with request: [{}] finished with result: [{}]",
                delegateExecution.getCurrentActivityId(), processSimulationRequest, simulationResult);
    }

    private Optional<Step> findStep(@Nonnull final ProcessSimulationRequest processSimulationRequest,
                                    @Nonnull final DelegateExecution delegateExecution) {
        final String activityId = delegateExecution.getCurrentActivityId();

        final Optional<Step> step = processSimulationRequest.getSteps()
                .stream()
                .filter(processStep -> processStep.getId().equals(activityId))
                .findAny();
        step.ifPresentOrElse(
                processStep -> log.debug("Step: [{}] found with activity id: [{}]", processStep, activityId),
                () -> log.debug("No step found with activity id: [{}]", activityId));

        return step;
    }

}
