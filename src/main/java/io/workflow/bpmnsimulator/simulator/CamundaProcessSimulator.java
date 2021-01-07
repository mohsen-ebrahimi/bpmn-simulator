package io.workflow.bpmnsimulator.simulator;

import io.workflow.bpmnsimulator.model.ProcessSimulationRequest;
import io.workflow.bpmnsimulator.service.DeploymentService;
import io.workflow.bpmnsimulator.service.ProcessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.ProcessEngineException;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

@Slf4j
@Component
@RequiredArgsConstructor
class CamundaProcessSimulator implements ProcessSimulator {

    private final DeploymentService deploymentService;

    private final ProcessService processService;

    @Override
    public void startSimulation(@Nonnull final ProcessSimulationRequest processSimulationRequest) {
        ProcessSimulationContextHolder.init(processSimulationRequest);
        final ProcessDefinition processDefinition = deploymentService.deploy(
                processSimulationRequest.getBpmnUrl(),
                processSimulationRequest.getDeploymentName());

        try {
            processService.startProcessInstance(processDefinition.getKey());
        } catch (ProcessEngineException e) {
            log.error("Error in simulating process with request: [{}]", processSimulationRequest, e);
            /*
             * TODO: Add a parser to convert the Camunda exception code.
             * https://github.com/mohsen-ebrahimi/bpmn-simulator/issues/12
             */
        }
    }

}
