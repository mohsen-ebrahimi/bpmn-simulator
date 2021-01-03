package io.workflow.bpmnsimulator.simulator;

import io.workflow.bpmnsimulator.model.Field;
import io.workflow.bpmnsimulator.model.ProcessSimulationRequest;
import io.workflow.bpmnsimulator.model.ProcessSimulationResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static io.workflow.bpmnsimulator.simulator.ProcessSimulationContextHolder.getProcessSimulationResult;
import static io.workflow.bpmnsimulator.util.JsonUtil.readJson;
import static io.workflow.bpmnsimulator.util.StepUtil.getStep;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@SpringBootTest
@Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "/sql/cleanup.sql")
@Sql(executionPhase = AFTER_TEST_METHOD, scripts = "/sql/cleanup.sql")
class ProcessGeneralFieldsValidationTest {

    private static final String PAYMENT_STEP_NAME = "paymentTask";

    private static final String PAYMENT_BPMN_URL = "/simulator/payment-process-simulation-request.json";

    @Autowired
    private CamundaProcessSimulator processSimulator;

    @Test
    void shouldReturnNoError() {
        //given
        final ProcessSimulationRequest processSimulationRequest = readJson(PAYMENT_BPMN_URL, ProcessSimulationRequest.class);

        //when
        processSimulator.startSimulation(processSimulationRequest);

        //then
        final ProcessSimulationResult processSimulationResult = getProcessSimulationResult();
        assertTrue(processSimulationResult.getErrors().isEmpty());
    }

    @Test
    void shouldReturnNoErrorWhenPreConditionIsNull() {
        //given
        final ProcessSimulationRequest processSimulationRequest = readJson(PAYMENT_BPMN_URL, ProcessSimulationRequest.class);
        getStep(processSimulationRequest, PAYMENT_STEP_NAME).setPreCondition(null);

        //when
        processSimulator.startSimulation(processSimulationRequest);

        //then
        final ProcessSimulationResult processSimulationResult = getProcessSimulationResult();
        assertTrue(processSimulationResult.getErrors().isEmpty());
    }

    @Test
    void shouldFailWithWrongStepName() {
        //given
        final ProcessSimulationRequest processSimulationRequest = readJson(PAYMENT_BPMN_URL, ProcessSimulationRequest.class);
        getStep(processSimulationRequest, PAYMENT_STEP_NAME).setName("NEW_TASK_NAME");

        //when
        processSimulator.startSimulation(processSimulationRequest);

        //then
        final ProcessSimulationResult simulationResult = getProcessSimulationResult();
        assertThat(simulationResult.getErrors(), contains(
                allOf(
                        hasProperty("stepId", is(PAYMENT_STEP_NAME)),
                        hasProperty("field", is(Field.NAME)),
                        hasProperty("expectedFieldValue", is("NEW_TASK_NAME")),
                        hasProperty("actualFieldValue", is("Payment Task"))
                )
        ));
    }

    @Test
    void shouldFailWithWrongStepAssignee() {
        //given
        final ProcessSimulationRequest processSimulationRequest = readJson(PAYMENT_BPMN_URL, ProcessSimulationRequest.class);
        getStep(processSimulationRequest, PAYMENT_STEP_NAME).setAssignee("NEW_TASK_ASSIGNEE");

        //when
        processSimulator.startSimulation(processSimulationRequest);

        //then
        final ProcessSimulationResult simulationResult = getProcessSimulationResult();
        assertThat(simulationResult.getErrors(), contains(
                allOf(
                        hasProperty("stepId", is(PAYMENT_STEP_NAME)),
                        hasProperty("field", is(Field.ASSIGNEE)),
                        hasProperty("expectedFieldValue", is("NEW_TASK_ASSIGNEE")),
                        hasProperty("actualFieldValue", is("demo"))
                )
        ));
    }
}
