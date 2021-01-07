package io.workflow.bpmnsimulator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class VariableService {

    private final RuntimeService runtimeService;

    public Map<String, Object> getProcessVariables(@Nonnull final String executionId) {
        final Map<String, Object> variables = runtimeService.getVariables(executionId);
        log.debug("Variables of execution with id: [{}] loaded: [{}]", executionId, variables);

        return variables;
    }

    public void saveProcessVariable(@Nonnull final String executionId,
                                    @Nonnull final String variableName,
                                    @Nullable final Object value) {
        runtimeService.setVariable(executionId, variableName, value);
    }

}
