package io.workflow.bpmnsimulator.simulator;

import io.workflow.bpmnsimulator.model.ProcessSimulationError;
import io.workflow.bpmnsimulator.model.ProcessSimulationError.Field;
import io.workflow.bpmnsimulator.model.ProcessSimulationRequest;
import io.workflow.bpmnsimulator.model.ProcessSimulationResult;
import io.workflow.bpmnsimulator.model.Step;
import io.workflow.bpmnsimulator.service.DeploymentService;
import io.workflow.bpmnsimulator.service.ProcessService;
import io.workflow.bpmnsimulator.service.TaskInstanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
class CamundaProcessSimulator implements ProcessSimulator {

    private final DeploymentService deploymentService;

    private final ProcessService processService;

    private final TaskInstanceService taskInstanceService;

    @Nonnull
    public ProcessSimulationResult simulate(@Nonnull final ProcessSimulationRequest processSimulationRequest) {
        final ProcessInstance processInstance = startProcess(processSimulationRequest);
        final List<ProcessSimulationError> processSimulationErrors = simulateSteps(processSimulationRequest);

        final ProcessSimulationResult processSimulationResult = ProcessSimulationResult.builder()
                .bpmnUrl(processSimulationRequest.getBpmnUrl())
                .errors(processSimulationErrors)
                .build();
        log.info("Simulating process instance: [{}] finished with result: [{}]", processInstance, processSimulationResult);

        return processSimulationResult;
    }

    @Nonnull
    private ProcessInstance startProcess(@Nonnull final ProcessSimulationRequest processSimulationRequest) {
        final ProcessDefinition processDefinition = deploymentService.deploy(
                processSimulationRequest.getBpmnUrl(),
                processSimulationRequest.getDeploymentName());
        return processService.startProcessInstance(processDefinition.getKey());
    }

    @Nonnull
    private List<ProcessSimulationError> simulateSteps(@Nonnull final ProcessSimulationRequest processSimulationRequest) {
        return processSimulationRequest.getSteps()
                .stream()
                .flatMap(step -> assertAndCompleteStep(step).stream())
                .collect(Collectors.toList());
    }

    private List<ProcessSimulationError> assertAndCompleteStep(@Nonnull final Step step) {
        return taskInstanceService.loadTask(step).map(task -> {
            final List<ProcessSimulationError> errors = assertStep(step, task);
            taskInstanceService.complete(task.getId());
            return errors;
        }).orElse(List.of(ProcessSimulationError.create(step.getId(), Field.ID, null, step.getId())));
    }

    private List<ProcessSimulationError> assertStep(@Nonnull final Step step, @Nonnull final Task task) {
        log.debug("No assertion has implemented yet.");
        return List.of();
    }

}
