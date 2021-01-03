package io.workflow.bpmnsimulator.simulator;

import io.workflow.bpmnsimulator.model.ProcessSimulationRequest;
import io.workflow.bpmnsimulator.service.DeploymentService;
import io.workflow.bpmnsimulator.service.ProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

@Slf4j
@Component
@RequiredArgsConstructor
class CamundaProcessSimulator implements ProcessSimulator {

    private final DeploymentService deploymentService;

    private final ProcessService processService;

    @Nonnull
    @Override
    public ProcessInstance startSimulation(@Nonnull final ProcessSimulationRequest processSimulationRequest) {
        ProcessSimulationContextHolder.init(processSimulationRequest);
        final ProcessDefinition processDefinition = deploymentService.deploy(
                processSimulationRequest.getBpmnUrl(),
                processSimulationRequest.getDeploymentName());

        return processService.startProcessInstance(processDefinition.getKey());
    }

}
