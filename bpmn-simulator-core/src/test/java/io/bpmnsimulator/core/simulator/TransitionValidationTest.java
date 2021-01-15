package io.bpmnsimulator.core.simulator;

import io.bpmnsimulator.core.BpmnTest;
import io.bpmnsimulator.core.model.Field;
import io.bpmnsimulator.core.model.ProcessSimulationRequest;
import io.bpmnsimulator.core.model.ProcessSimulationResult;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.bpmnsimulator.core.util.JsonUtil.readJsonWithRelativePath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@BpmnTest
class TransitionValidationTest {

    private static final String TRANSITION_VALIDATION_REQUEST_URL = "request/transition-validation-request.json";

    @Autowired
    private CamundaProcessSimulator processSimulator;

    @Test
    void shouldReturnNoErrorWhenTransitionIsValid() {
        //given
        final ProcessSimulationRequest processSimulationRequest = readJsonWithRelativePath(TRANSITION_VALIDATION_REQUEST_URL,
                ProcessSimulationRequest.class, "one", "flowOne");

        //when
        final ProcessSimulationResult processSimulationResult = processSimulator.simulate(processSimulationRequest);

        //then
        assertTrue(processSimulationResult.getErrors().isEmpty());
    }

    @Test
    void shouldReturnErrorWhenTransitionIsNotCorrect() {
        //given
        final ProcessSimulationRequest processSimulationRequest = readJsonWithRelativePath(TRANSITION_VALIDATION_REQUEST_URL,
                ProcessSimulationRequest.class, "two", "flowFour");

        //when
        final ProcessSimulationResult simulationResult = processSimulator.simulate(processSimulationRequest);

        //then
        assertThat(simulationResult.getErrors(), contains(
                allOf(
                        hasProperty("stepId", is("gatewayNode")),
                        hasProperty("field", is(Field.TRANSITION)),
                        hasProperty("expectedFieldValue", is("flowFour")),
                        hasProperty("actualFieldValue", is("flowTwo"))
                )
        ));
    }

    @Test
    @Disabled("Test will be passed after resolving https://github.com/mohsen-ebrahimi/bpmn-simulator/issues/12#issue-778493913")
    void shouldReturnErrorWhenTransitionNotExists() {
        //given
        final ProcessSimulationRequest processSimulationRequest = readJsonWithRelativePath(TRANSITION_VALIDATION_REQUEST_URL,
                ProcessSimulationRequest.class, "four", "flowOne");

        //when
        final ProcessSimulationResult simulationResult = processSimulator.simulate(processSimulationRequest);

        //then
        assertThat(simulationResult.getErrors(), contains(
                allOf(
                        hasProperty("stepId", is("gatewayNode")),
                        hasProperty("field", is(Field.TRANSITION)),
                        hasProperty("expectedFieldValue", is("flowOne")),
                        hasProperty("actualFieldValue", nullValue())
                )
        ));
    }

}
