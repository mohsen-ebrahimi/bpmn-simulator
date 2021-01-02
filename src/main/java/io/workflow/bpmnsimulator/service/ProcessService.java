package io.workflow.bpmnsimulator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.ProcessEngineServices;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.rest.dto.runtime.ProcessInstanceDto;
import org.camunda.bpm.engine.runtime.ActivityInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessService {

    private final RuntimeService runtimeService;

    @Nonnull
    public ProcessInstance startProcessInstance(@Nonnull final String processDefinitionKey) {
        final ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(processDefinitionKey);
        log.debug("A new process instance: [{}] started with process definition key: [{}]", processInstance, processDefinitionKey);

        return processInstance;
    }

    public Optional<ProcessInstance> getProcessInstance(@Nonnull final String processInstanceId) {
        try {
            return Optional.of(runtimeService.createProcessInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .singleResult());
        } catch (Exception e) {
            log.warn("No process instance found with id: [{}]", processInstanceId);
            return Optional.empty();
        }
    }

}
