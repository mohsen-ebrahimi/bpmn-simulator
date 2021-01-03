package io.workflow.bpmnsimulator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.impl.context.Context;
import org.camunda.bpm.engine.impl.persistence.entity.TaskManager;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskInstanceService {

    private final TaskService taskService;

    public Optional<Task> loadTask(@Nonnull final String taskId) {
        try {
            final TaskManager taskManager = Context.getCommandContext().getTaskManager();
            return Optional.of(taskManager.findTaskById(taskId));
        } catch (Exception e) {
            log.warn("No task found with id: [{}]", taskId);
            return Optional.empty();
        }
    }

    public void complete(@Nonnull final String taskId) {
        log.debug("Completing task with id: [{}]", taskId);
        taskService.complete(taskId);
    }
}
