package io.bpmnsimulator.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;

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

}
