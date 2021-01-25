package io.bpmnsimulator.core.simulator;

import io.bpmnsimulator.core.BpmnTest;
import io.bpmnsimulator.core.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static io.bpmnsimulator.core.util.JsonUtil.readJsonWithRelativePath;
import static io.bpmnsimulator.core.util.StepUtil.getStep;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@BpmnTest
class ProcessGeneralFieldsValidationTest {

    private static final String SIMULATION_REQUEST_BASE_URL = "request/";

    private static final String PAYMENT_STEP_ID = "paymentTask";

    private static final String PAYMENT_TASK_NAME = "Payment Task";

    @Autowired
    private CamundaProcessSimulator processSimulator;

    @Test
    void shouldReturnNoError() {
        //given
        final String requestUrl = SIMULATION_REQUEST_BASE_URL + "request-without-error.json";
        final ProcessSimulationRequest processSimulationRequest = readJsonWithRelativePath(requestUrl, ProcessSimulationRequest.class);

        //when
        final ProcessSimulationResult processSimulationResult = processSimulator.simulate(processSimulationRequest);

        //then
        assertTrue(processSimulationResult.getErrors().isEmpty());
    }

    @Test
    void shouldReturnNoErrorWhenPreconditionIsNull() {
        //given
        final String requestUrl = SIMULATION_REQUEST_BASE_URL + "request-with-no-precondition.json";
        final ProcessSimulationRequest processSimulationRequest = readJsonWithRelativePath(requestUrl, ProcessSimulationRequest.class);

        //when
        final ProcessSimulationResult processSimulationResult = processSimulator.simulate(processSimulationRequest);

        //then
        assertTrue(processSimulationResult.getErrors().isEmpty());
    }

    @Test
    void shouldFailWithWrongStepName() {
        //given
        final String requestUrl = SIMULATION_REQUEST_BASE_URL + "request-with-wrong-step-name.json";
        final ProcessSimulationRequest processSimulationRequest = readJsonWithRelativePath(requestUrl, ProcessSimulationRequest.class);

        //when
        final ProcessSimulationResult simulationResult = processSimulator.simulate(processSimulationRequest);

        //then
        assertThat(simulationResult.getErrors(), contains(
                allOf(
                        hasProperty("stepId", is(PAYMENT_STEP_ID)),
                        hasProperty("field", is(Field.NAME)),
                        hasProperty("expectedFieldValue", is("NEW TASK NAME")),
                        hasProperty("actualFieldValue", is(PAYMENT_TASK_NAME))
                )
        ));
    }

    @Test
    void shouldFailWithWrongStepAssignee() {
        //given
        final String requestUrl = SIMULATION_REQUEST_BASE_URL + "request-with-wrong-assignee.json";
        final ProcessSimulationRequest processSimulationRequest = readJsonWithRelativePath(requestUrl, ProcessSimulationRequest.class);

        //when
        final ProcessSimulationResult simulationResult = processSimulator.simulate(processSimulationRequest);

        //then
        assertThat(simulationResult.getErrors(), contains(
                allOf(
                        hasProperty("stepId", is(PAYMENT_STEP_ID)),
                        hasProperty("field", is(Field.ASSIGNEE)),
                        hasProperty("expectedFieldValue", is("NEW TASK ASSIGNEE")),
                        hasProperty("actualFieldValue", is("demo"))
                )
        ));
    }

    @Test
    void shouldReturnNoErrorWhenStepAndTaskAssigneeIsNull() {
        //given
        final String requestUrl = SIMULATION_REQUEST_BASE_URL + "request-with-null-assignee.json";
        final ProcessSimulationRequest processSimulationRequest = readJsonWithRelativePath(requestUrl, ProcessSimulationRequest.class);

        //when
        final ProcessSimulationResult simulationResult = processSimulator.simulate(processSimulationRequest);

        //then
        assertTrue(simulationResult.getErrors().isEmpty());
    }

    @Test
    void shouldFailWhenStepAssigneeIsNullAndTaskAssigneeIsNotNull() {
        //given
        final String requestUrl = SIMULATION_REQUEST_BASE_URL + "request-without-error.json";
        final ProcessSimulationRequest processSimulationRequest = readJsonWithRelativePath(requestUrl, ProcessSimulationRequest.class);
        final Step step = getStep(processSimulationRequest, PAYMENT_STEP_ID);
        step.getPreconditions().clear();
        final Precondition<Object> assigneePrecondition = new Precondition<>();
        assigneePrecondition.setField(Field.ASSIGNEE);
        assigneePrecondition.setExpectedValue(null);
        step.getPreconditions().add(assigneePrecondition);

        //when
        final ProcessSimulationResult simulationResult = processSimulator.simulate(processSimulationRequest);

        //then
        assertThat(simulationResult.getErrors(), contains(
                allOf(
                        hasProperty("stepId", is(PAYMENT_STEP_ID)),
                        hasProperty("field", is(Field.ASSIGNEE)),
                        hasProperty("expectedFieldValue", nullValue()),
                        hasProperty("actualFieldValue", is("demo"))
                )
        ));
    }

}
