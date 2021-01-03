package io.workflow.bpmnsimulator.listener;

import io.workflow.bpmnsimulator.model.ProcessSimulationRequest;
import io.workflow.bpmnsimulator.simulator.nodehandler.NodeEndedHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.spring.boot.starter.event.ExecutionEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import java.util.List;

import static io.workflow.bpmnsimulator.simulator.ProcessSimulationContextHolder.getProcessSimulationRequest;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExecutionEventListener {

    private static final String END_EVENT_NAME = "end";

    private final List<NodeEndedHandler> nodeEndedHandlers;

    @EventListener
    public void execute(@Nonnull final ExecutionEvent executionEvent) {
        final String eventName = executionEvent.getEventName();

        if (END_EVENT_NAME.equals(eventName)) {
            final ProcessSimulationRequest processSimulationRequest = getProcessSimulationRequest();
            nodeEndedHandlers.forEach(nodeEndedHandler -> nodeEndedHandler.onNodeEnded(processSimulationRequest, executionEvent));
        }
    }
}
