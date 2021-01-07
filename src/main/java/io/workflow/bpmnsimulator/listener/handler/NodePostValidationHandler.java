package io.workflow.bpmnsimulator.listener.handler;

import io.workflow.bpmnsimulator.model.ProcessSimulationError;
import io.workflow.bpmnsimulator.model.ProcessSimulationRequest;
import io.workflow.bpmnsimulator.model.ProcessSimulationResult;
import io.workflow.bpmnsimulator.model.Step;
import io.workflow.bpmnsimulator.simulator.ProcessSimulationContextHolder;
import io.workflow.bpmnsimulator.validator.postvalidator.PostValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
class NodePostValidationHandler implements NodeTakenHandler {

    private final List<PostValidator> postValidators;

    @Override
    public void onNodeTaken(@Nonnull final ProcessSimulationRequest processSimulationRequest,
                            @Nonnull final DelegateExecution delegateExecution) {
        final List<ProcessSimulationError> simulationErrors = findStep(processSimulationRequest, delegateExecution)
                .map(step -> validateStep(step, delegateExecution))
                .orElse(List.of());

        final ProcessSimulationResult simulationResult = ProcessSimulationContextHolder.getProcessSimulationResult();
        simulationResult.getErrors().addAll(simulationErrors);
        log.info("Post-validation for node: [{}] with request: [{}] finished with result: [{}]",
                delegateExecution.getCurrentActivityId(), processSimulationRequest, simulationResult);
    }

    private Optional<Step> findStep(@Nonnull final ProcessSimulationRequest processSimulationRequest,
                                    @Nonnull final DelegateExecution delegateExecution) {
        return processSimulationRequest.getSteps()
                .stream()
                .filter(step -> step.getId().equals(delegateExecution.getCurrentActivityId()))
                .filter(step -> step.getPostCondition() != null)
                .findAny();
    }

    private List<ProcessSimulationError> validateStep(@Nonnull final Step step,
                                                      @Nonnull final DelegateExecution delegateExecution) {
        return postValidators.stream()
                .flatMap(postValidator -> postValidator.validate(step, delegateExecution).stream())
                .collect(Collectors.toList());
    }

}
