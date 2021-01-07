package io.workflow.bpmnsimulator.validator.prevalidator;

import io.workflow.bpmnsimulator.model.Field;
import io.workflow.bpmnsimulator.model.ProcessSimulationError;
import io.workflow.bpmnsimulator.model.Step;
import io.workflow.bpmnsimulator.service.VariableService;
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

@Slf4j
@Component
@RequiredArgsConstructor
class ProcessVariableValidator implements PreValidator {

    private final VariableService variableService;

    @Override
    public List<ProcessSimulationError> validate(@Nonnull final Step step, @Nonnull final Task task) {
        final Map<String, Object> processVariables = variableService.getProcessVariables(task.getExecutionId());

        if (step.getPrecondition() == null) {
            log.debug("Ignoring variables validation because no precondition found for step: [{}]", step);
            return List.of();
        }

        final List<ProcessSimulationError> simulationErrors = step.getPrecondition()
                .getExpectedProcessVariables()
                .entrySet()
                .stream()
                .map(expectedProcessVariable -> validateProcessVariable(step, processVariables, expectedProcessVariable))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        log.info("The validation result of 'ProcessVariable' field is: [{}]", simulationErrors);

        return simulationErrors;
    }

    @Nonnull
    private Optional<ProcessSimulationError> validateProcessVariable(@Nonnull final Step step,
                                                                     @Nonnull final Map<String, Object> actualProcessVariables,
                                                                     @Nonnull final Entry<String, Object> expectedProcessVariable) {
        final boolean hasProcessVariable = actualProcessVariables.containsKey(expectedProcessVariable.getKey());
        if (!hasProcessVariable) {
            final ProcessSimulationError simulationError = ProcessSimulationError.builder()
                    .stepId(step.getId())
                    .field(Field.PROCESS_VARIABLE)
                    .expectedFieldValue(toVariableString(expectedProcessVariable.getKey(), expectedProcessVariable.getValue()))
                    .actualFieldValue(null)
                    .build();
            return Optional.of(simulationError);
        }

        final Object actualProcessVariableValue = actualProcessVariables.get(expectedProcessVariable.getKey());
        final boolean isVariableValueValid = Objects.equals(actualProcessVariableValue, expectedProcessVariable.getValue());
        if (!isVariableValueValid) {
            final ProcessSimulationError simulationError = ProcessSimulationError.builder()
                    .stepId(step.getId())
                    .field(Field.PROCESS_VARIABLE)
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
