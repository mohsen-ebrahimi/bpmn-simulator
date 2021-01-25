package io.bpmnsimulator.core.validator.usertask;

import io.bpmnsimulator.core.model.Condition;
import io.bpmnsimulator.core.model.ProcessSimulationError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;

import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Slf4j
@Component
@RequiredArgsConstructor
abstract class AbstractCandidatesValidator implements UserTaskValidator<List<String>> {

    @Nonnull
    @Override
    public List<ProcessSimulationError> validate(@Nonnull final UserTaskValidatorContext<List<String>> context) {
        final Task task = context.getTask();
        final Condition<List<String>> condition = context.getCondition();
        final List<String> expectedCandidates = emptyIfNull(condition.getExpectedValue());
        final List<String> actualCandidates = getCandidates(task);

        final Collection<String> missingCandidates = CollectionUtils.removeAll(expectedCandidates, actualCandidates);
        final Collection<String> extraCandidates = CollectionUtils.removeAll(actualCandidates, expectedCandidates);

        if (!missingCandidates.isEmpty() || !extraCandidates.isEmpty()) {
            log.info("Candidate validation failed because either there are missing candidates: {} or extra candidates: {}",
                    missingCandidates, extraCandidates);

            final ProcessSimulationError simulationError = ProcessSimulationError.builder()
                    .stepId(context.getStepId())
                    .field(getSupportedField())
                    .expectedFieldValue(expectedCandidates.toString())
                    .actualFieldValue(actualCandidates.toString())
                    .build();
            log.info("'{}' field validation failed: [{}]", getSupportedField().getName(), simulationError);

            return List.of(simulationError);
        }

        return List.of();
    }

    @Nonnull
    protected abstract List<String> getCandidates(@Nonnull Task task);

}
