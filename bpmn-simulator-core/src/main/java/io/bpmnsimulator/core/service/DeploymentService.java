package io.bpmnsimulator.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.repository.DeploymentWithDefinitions;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeploymentService {

    private final RepositoryService repositoryService;

    @Nonnull
    public ProcessDefinition deploy(@Nonnull final String bpmnUrl, @Nonnull final String deploymentName) {

        final DeploymentWithDefinitions deploymentWithDefinitions = repositoryService.createDeployment()
                .addClasspathResource(bpmnUrl)
                .name(deploymentName)
                .deployWithResult();
        log.debug("BpmnUrl: [{}] deployed. [{}]", bpmnUrl, deploymentWithDefinitions);

        return deploymentWithDefinitions.getDeployedProcessDefinitions()
                .stream()
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(format("No process definition found in BPMN: [%s]", bpmnUrl)));
    }

}
