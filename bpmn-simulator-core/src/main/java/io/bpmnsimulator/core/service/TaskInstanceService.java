package io.bpmnsimulator.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.persistence.entity.TaskManager;
import org.camunda.bpm.engine.task.IdentityLink;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.camunda.bpm.engine.task.IdentityLinkType.CANDIDATE;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskInstanceService {

    private final TaskService taskService;

    public Optional<Task> getTask(@Nonnull final String taskId) {
        try {
            final TaskManager taskManager = Context.getCommandContext().getTaskManager();
            return Optional.of(taskManager.findTaskById(taskId));
        } catch (Exception e) {
            log.warn("No task found with id: [{}]", taskId);
            return Optional.empty();
        }
    }

    public List<IdentityLink> getCandidateUsers(@Nonnull final String taskId) {
        final List<IdentityLink> candidateUsers = taskService.getIdentityLinksForTask(taskId)
                .stream()
                .filter(identityLink -> CANDIDATE.equals(identityLink.getType()))
                .filter(identityLink -> nonNull(identityLink.getUserId()) && isNull(identityLink.getGroupId()))
                .collect(Collectors.toList());
        log.debug("CandidateUsers of task: [{}] is: {}", taskId, candidateUsers);

        return candidateUsers;
    }

    public void complete(@Nonnull final String taskId) {
        log.debug("Completing task with id: [{}]", taskId);
        taskService.complete(taskId);
    }
}
