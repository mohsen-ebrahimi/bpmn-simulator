package io.bpmnsimulator.core.validator.usertask;

import io.bpmnsimulator.core.model.Field;
import io.bpmnsimulator.core.service.TaskInstanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.task.IdentityLink;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Collectors;

import static io.bpmnsimulator.core.model.Field.CANDIDATE_USERS;

@Slf4j
@Component
@RequiredArgsConstructor
class CandidateUsersValidator extends AbstractCandidatesValidator {

    private final TaskInstanceService taskInstanceService;

    @Nonnull
    @Override
    public Field getSupportedField() {
        return CANDIDATE_USERS;
    }

    @Nonnull
    @Override
    protected List<String> getCandidates(@Nonnull final Task task) {
        return taskInstanceService.getCandidateUsers(task.getId())
                .stream()
                .map(IdentityLink::getUserId)
                .collect(Collectors.toList());
    }
}
