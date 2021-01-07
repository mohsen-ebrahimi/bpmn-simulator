package io.workflow.bpmnsimulator.listener;

import io.workflow.bpmnsimulator.listener.handler.NodeEndedHandler;
import io.workflow.bpmnsimulator.listener.handler.NodeTakenHandler;
import io.workflow.bpmnsimulator.model.ProcessSimulationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
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

    private static final String TAKE_EVENT_NAME = "take";

    private final List<NodeEndedHandler> nodeEndedHandlers;

    private final List<NodeTakenHandler> nodeTakenHandlers;

    @EventListener
    public void execute(@Nonnull final DelegateExecution executionEvent) {
        final String eventName = executionEvent.getEventName();

        if (END_EVENT_NAME.equals(eventName)) {
            final ProcessSimulationRequest processSimulationRequest = getProcessSimulationRequest();
            nodeEndedHandlers.forEach(nodeEndedHandler -> nodeEndedHandler.onNodeEnded(processSimulationRequest, executionEvent));
        } else if (TAKE_EVENT_NAME.equals(eventName)) {
            nodeTakenHandlers.forEach(nodeTakenHandler -> nodeTakenHandler.onNodeTaken(getProcessSimulationRequest(), executionEvent));
        }
    }
}
