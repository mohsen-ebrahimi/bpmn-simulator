package io.bpmnsimulator.core.listener.handler;

import io.bpmnsimulator.core.model.ProcessSimulationRequest;
import io.bpmnsimulator.core.service.VariableService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

@Slf4j
@Component
@RequiredArgsConstructor
class VariableCreationHandler implements NodeEndedHandler {

    private final VariableService variableService;

    @Override
    public void onNodeEnded(@Nonnull final ProcessSimulationRequest processSimulationRequest,
                            @Nonnull final DelegateExecution delegateExecution) {
        final String processInstanceId = delegateExecution.getProcessInstanceId();
        final String activityId = delegateExecution.getCurrentActivityId();

        processSimulationRequest
                .getSteps()
                .stream()
                .filter(step -> step.getId().equals(activityId))
                .findAny()
                .ifPresent(step -> step.getProcessVariables().forEach((variableName, value) -> {
                    variableService.saveProcessVariable(processInstanceId, variableName, value);
                    log.debug("A new process variable persisted: [{}={}]", variableName, value);
                }));
    }
}
