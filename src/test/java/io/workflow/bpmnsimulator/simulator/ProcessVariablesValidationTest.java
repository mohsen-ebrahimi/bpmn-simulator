package io.workflow.bpmnsimulator.simulator;

import io.workflow.bpmnsimulator.model.Condition;
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
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@SpringBootTest
@Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "/sql/cleanup.sql")
@Sql(executionPhase = AFTER_TEST_METHOD, scripts = "/sql/cleanup.sql")
class ProcessVariablesValidationTest {

    private static final String PAYMENT_STEP_NAME = "paymentTask";

    private static final String PAYMENT_SIMULATION_REQUEST_URL = "/simulator/payment-process-simulation-request.json";

    @Autowired
    private CamundaProcessSimulator processSimulator;

    @Test
    void shouldFailWhenProcessVariableNotFound() {
        //given
        final ProcessSimulationRequest processSimulationRequest = readJson(PAYMENT_SIMULATION_REQUEST_URL, ProcessSimulationRequest.class);
        final Condition preCondition = getStep(processSimulationRequest, PAYMENT_STEP_NAME)
                .getPreCondition();
        assertThat(preCondition, notNullValue());
        preCondition.getExpectedProcessVariables().put("new-key", "new-value");
        preCondition.getExpectedProcessVariables().put("amount", 999);
        preCondition.getExpectedProcessVariables().put("description", "new dummy description");

        //when
        processSimulator.startSimulation(processSimulationRequest);

        //then
        final ProcessSimulationResult simulationResult = getProcessSimulationResult();
        assertThat(simulationResult.getErrors(), containsInAnyOrder(
                allOf(
                        hasProperty("stepId", is(PAYMENT_STEP_NAME)),
                        hasProperty("field", is(Field.PROCESS_VARIABLE)),
                        hasProperty("expectedFieldValue", is("{new-key=new-value}")),
                        hasProperty("actualFieldValue", nullValue())
                ),
                allOf(
                        hasProperty("stepId", is(PAYMENT_STEP_NAME)),
                        hasProperty("field", is(Field.PROCESS_VARIABLE)),
                        hasProperty("expectedFieldValue", is("{amount=999}")),
                        hasProperty("actualFieldValue", nullValue())
                ),
                allOf(
                        hasProperty("stepId", is(PAYMENT_STEP_NAME)),
                        hasProperty("field", is(Field.PROCESS_VARIABLE)),
                        hasProperty("expectedFieldValue", is("{description=new dummy description}")),
                        hasProperty("actualFieldValue", nullValue())
                )
        ));
    }

    @Test
    void shouldFailWhenProcessVariableValueNotMatch() {
        //given
        final ProcessSimulationRequest processSimulationRequest = readJson(PAYMENT_SIMULATION_REQUEST_URL, ProcessSimulationRequest.class);
        final Condition preCondition = getStep(processSimulationRequest, PAYMENT_STEP_NAME)
                .getPreCondition();
        assertThat(preCondition, notNullValue());
        preCondition.getExpectedProcessVariables().put("name", "Baz Foo");
        preCondition.getExpectedProcessVariables().put("age", null);

        //when
        processSimulator.startSimulation(processSimulationRequest);

        //then
        final ProcessSimulationResult simulationResult = getProcessSimulationResult();
        assertThat(simulationResult.getErrors(), containsInAnyOrder(
                allOf(
                        hasProperty("stepId", is(PAYMENT_STEP_NAME)),
                        hasProperty("field", is(Field.PROCESS_VARIABLE)),
                        hasProperty("expectedFieldValue", is("{name=Baz Foo}")),
                        hasProperty("actualFieldValue", is("{name=null}"))
                ),
                allOf(
                        hasProperty("stepId", is(PAYMENT_STEP_NAME)),
                        hasProperty("field", is(Field.PROCESS_VARIABLE)),
                        hasProperty("expectedFieldValue", is("{age=null}")),
                        hasProperty("actualFieldValue", is("{age=30}"))
                )
        ));
    }
}
