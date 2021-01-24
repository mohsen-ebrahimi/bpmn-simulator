package io.bpmnsimulator.core.validator.usertask;

import io.bpmnsimulator.core.model.Condition;
import io.bpmnsimulator.core.model.Field;
import io.bpmnsimulator.core.model.ProcessSimulationError;
import io.bpmnsimulator.core.service.VariableService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.bpmnsimulator.core.model.Field.PROCESS_VARIABLES;
import static org.apache.commons.collections4.MapUtils.emptyIfNull;

@Slf4j
@Component
@RequiredArgsConstructor
class ProcessVariableValidator implements UserTaskValidator<Map<String, Object>> {

    private final VariableService variableService;

    @Nonnull
    @Override
    public List<ProcessSimulationError> validate(@Nonnull final UserTaskValidatorContext<Map<String, Object>> context) {
        final Task task = context.getTask();
        final Condition<Map<String, Object>> condition = context.getCondition();
        final Map<String, Object> processVariables = variableService.getProcessVariables(task.getExecutionId());

        final List<ProcessSimulationError> simulationErrors = emptyIfNull(condition.getExpectedValue())
                .entrySet()
                .stream()
                .map(expectedProcessVariable -> validateProcessVariable(context, processVariables, expectedProcessVariable))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        log.info("The validation result of 'ProcessVariable' field is: [{}]", simulationErrors);

        return simulationErrors;
    }

    @Nonnull
    @Override
    public Field getSupportedField() {
        return PROCESS_VARIABLES;
    }

    @Nonnull
    private Optional<ProcessSimulationError> validateProcessVariable(@Nonnull final UserTaskValidatorContext<Map<String, Object>> context,
                                                                     @Nonnull final Map<String, Object> actualProcessVariables,
                                                                     @Nonnull final Entry<String, Object> expectedProcessVariable) {
        final boolean hasProcessVariable = actualProcessVariables.containsKey(expectedProcessVariable.getKey());
        if (!hasProcessVariable) {
            final ProcessSimulationError simulationError = ProcessSimulationError.builder()
                    .stepId(context.getStepId())
                    .field(PROCESS_VARIABLES)
                    .expectedFieldValue(toVariableString(expectedProcessVariable.getKey(), expectedProcessVariable.getValue()))
                    .actualFieldValue(null)
                    .build();
            return Optional.of(simulationError);
        }

        final Object actualProcessVariableValue = actualProcessVariables.get(expectedProcessVariable.getKey());
        final boolean isVariableValueValid = Objects.equals(actualProcessVariableValue, expectedProcessVariable.getValue());
        if (!isVariableValueValid) {
            final ProcessSimulationError simulationError = ProcessSimulationError.builder()
                    .stepId(context.getStepId())
                    .field(PROCESS_VARIABLES)
                    .expectedFieldValue(toVariableString(expectedProcessVariable.getKey(), expectedProcessVariable.getValue()))
                    .actualFieldValue(toVariableString(expectedProcessVariable.getKey(), actualProcessVariableValue))
                    .build();
            return Optional.of(simulationError);
        }

        return Optional.empty();
    }

    @Nonnull
    private String toVariableString(@Nonnull final String key, @Nullable final Object value) {
        return String.format("{%s=%s}", key, value);
    }

}
