package io.bpmnsimulator.core.simulator;

import io.bpmnsimulator.core.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import static io.bpmnsimulator.core.simulator.ProcessSimulationContextHolder.getProcessSimulationResult;
import static io.bpmnsimulator.core.util.JsonUtil.readJson;
import static io.bpmnsimulator.core.util.StepUtil.getStep;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@BpmnTest
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
        final Precondition precondition = getStep(processSimulationRequest, PAYMENT_STEP_NAME)
                .getPrecondition();
        assertThat(precondition, notNullValue());
        precondition.getExpectedProcessVariables().put("new-key", "new-value");
        precondition.getExpectedProcessVariables().put("amount", 999);
        precondition.getExpectedProcessVariables().put("description", "new dummy description");

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
        final Precondition precondition = getStep(processSimulationRequest, PAYMENT_STEP_NAME)
                .getPrecondition();
        assertThat(precondition, notNullValue());
        precondition.getExpectedProcessVariables().put("name", "Baz Foo");
        precondition.getExpectedProcessVariables().put("age", null);

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
