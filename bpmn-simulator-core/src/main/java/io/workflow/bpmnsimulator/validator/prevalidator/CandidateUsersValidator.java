package io.workflow.bpmnsimulator.validator.prevalidator;

import io.workflow.bpmnsimulator.model.Field;
import io.workflow.bpmnsimulator.model.ProcessSimulationError;
import io.workflow.bpmnsimulator.model.Step;
import io.workflow.bpmnsimulator.service.TaskInstanceService;
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

@Slf4j
@Component
@RequiredArgsConstructor
class CandidateUsersValidator implements PreValidator {

    private final TaskInstanceService taskInstanceService;

    @Override
    public List<ProcessSimulationError> validate(@Nonnull final Step step, @Nonnull final Task task) {
        final List<String> expectedCandidateUsers = step.getCandidateUsers();
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
                    .stepId(step.getId())
                    .field(Field.CANDIDATE_USERS)
                    .expectedFieldValue(step.getCandidateUsers().toString())
                    .actualFieldValue(actualCandidateUsers.toString())
                    .build();
            log.info("'CandidateUsers' field validation failed: [{}]", simulationError);

            return List.of(simulationError);
        }

        return List.of();
    }
}
