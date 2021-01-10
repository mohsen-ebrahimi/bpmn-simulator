package io.bpmnsimulator.core.simulator;

import io.bpmnsimulator.core.model.BpmnTest;
import io.bpmnsimulator.core.model.Field;
import io.bpmnsimulator.core.model.ProcessSimulationRequest;
import io.bpmnsimulator.core.model.ProcessSimulationResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import static io.bpmnsimulator.core.util.JsonUtil.readJson;
import static io.bpmnsimulator.core.util.StepUtil.getStep;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@BpmnTest
@Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "/sql/cleanup.sql")
@Sql(executionPhase = AFTER_TEST_METHOD, scripts = "/sql/cleanup.sql")
class ProcessGeneralFieldsValidationTest {

    private static final String PAYMENT_SIMULATION_REQUEST_URL = "/simulator/payment-process-simulation-request.json";

    private static final String PAYMENT_STEP_ID = "paymentTask";

    private static final String PAYMENT_TASK_NAME = "Payment Task";

    @Autowired
    private CamundaProcessSimulator processSimulator;

    @Test
    void shouldReturnNoError() {
        //given
        final ProcessSimulationRequest processSimulationRequest = readJson(PAYMENT_SIMULATION_REQUEST_URL, ProcessSimulationRequest.class);

        //when
        final ProcessSimulationResult processSimulationResult = processSimulator.simulate(processSimulationRequest);

        //then
        assertTrue(processSimulationResult.getErrors().isEmpty());
    }

    @Test
    void shouldReturnNoErrorWhenPreConditionIsNull() {
        //given
        final ProcessSimulationRequest processSimulationRequest = readJson(PAYMENT_SIMULATION_REQUEST_URL, ProcessSimulationRequest.class);
        getStep(processSimulationRequest, PAYMENT_STEP_ID).setPrecondition(null);

        //when
        final ProcessSimulationResult processSimulationResult = processSimulator.simulate(processSimulationRequest);

        //then
        assertTrue(processSimulationResult.getErrors().isEmpty());
    }

    @Test
    void shouldFailWithWrongStepName() {
        //given
        final ProcessSimulationRequest processSimulationRequest = readJson(PAYMENT_SIMULATION_REQUEST_URL, ProcessSimulationRequest.class);
        getStep(processSimulationRequest, PAYMENT_STEP_ID).setName("NEW_TASK_NAME");

        //when
        final ProcessSimulationResult simulationResult = processSimulator.simulate(processSimulationRequest);

        //then
        assertThat(simulationResult.getErrors(), contains(
                allOf(
                        hasProperty("stepId", is(PAYMENT_STEP_ID)),
                        hasProperty("field", is(Field.NAME)),
                        hasProperty("expectedFieldValue", is("NEW_TASK_NAME")),
                        hasProperty("actualFieldValue", is(PAYMENT_TASK_NAME))
                )
        ));
    }

    @Test
    void shouldFailWithWrongStepAssignee() {
        //given
        final ProcessSimulationRequest processSimulationRequest = readJson(PAYMENT_SIMULATION_REQUEST_URL, ProcessSimulationRequest.class);
        getStep(processSimulationRequest, PAYMENT_STEP_ID).setAssignee("NEW_TASK_ASSIGNEE");

        //when
        final ProcessSimulationResult simulationResult = processSimulator.simulate(processSimulationRequest);

        //then
        assertThat(simulationResult.getErrors(), contains(
                allOf(
                        hasProperty("stepId", is(PAYMENT_STEP_ID)),
                        hasProperty("field", is(Field.ASSIGNEE)),
                        hasProperty("expectedFieldValue", is("NEW_TASK_ASSIGNEE")),
                        hasProperty("actualFieldValue", is("demo"))
                )
        ));
    }
}
