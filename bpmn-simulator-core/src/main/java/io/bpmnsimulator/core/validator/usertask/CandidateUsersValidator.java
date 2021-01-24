package io.bpmnsimulator.core.validator.usertask;

import io.bpmnsimulator.core.model.Condition;
import io.bpmnsimulator.core.model.Field;
import io.bpmnsimulator.core.model.ProcessSimulationError;
import io.bpmnsimulator.core.service.TaskInstanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.camunda.bpm.engine.task.IdentityLink;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static io.bpmnsimulator.core.model.Field.CANDIDATE_USERS;
import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Slf4j
@Component
@RequiredArgsConstructor
class CandidateUsersValidator implements UserTaskValidator<List<String>> {

    private final TaskInstanceService taskInstanceService;

    @Nonnull
    @Override
    public List<ProcessSimulationError> validate(@Nonnull final UserTaskValidatorContext<List<String>> context) {
        final Task task = context.getTask();
        final Condition<List<String>> condition = context.getCondition();
        final List<String> expectedCandidateUsers = emptyIfNull(condition.getExpectedValue());
        final List<String> actualCandidateUsers = taskInstanceService.getCandidateUsers(task.getId())
                .stream()
                .map(IdentityLink::getUserId)
                .collect(Collectors.toList());

        final Collection<String> missingCandidateUsers = CollectionUtils.removeAll(expectedCandidateUsers, actualCandidateUsers);
        final Collection<String> extraCandidateUsers = CollectionUtils.removeAll(actualCandidateUsers, expectedCandidateUsers);

        if (!missingCandidateUsers.isEmpty() || !extraCandidateUsers.isEmpty()) {
            log.info("CandidateUsers validation failed because either there are missing CandidateUsers: {} or extra CandidateUsers: {}",
                    missingCandidateUsers, extraCandidateUsers);

            final ProcessSimulationError simulationError = ProcessSimulationError.builder()
                    .stepId(context.getStepId())
                    .field(CANDIDATE_USERS)
                    .expectedFieldValue(expectedCandidateUsers.toString())
                    .actualFieldValue(actualCandidateUsers.toString())
                    .build();
            log.info("'CandidateUsers' field validation failed: [{}]", simulationError);

            return List.of(simulationError);
        }

        return List.of();
    }

    @Nonnull
    @Override
    public Field getSupportedField() {
        return CANDIDATE_USERS;
    }

}
