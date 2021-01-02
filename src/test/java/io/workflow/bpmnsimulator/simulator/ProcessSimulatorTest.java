package io.workflow.bpmnsimulator.simulator;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.workflow.bpmnsimulator.model.Field;
import io.workflow.bpmnsimulator.model.ProcessSimulationRequest;
import io.workflow.bpmnsimulator.model.ProcessSimulationResult;
import io.workflow.bpmnsimulator.util.JsonUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import javax.annotation.Nonnull;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@SpringBootTest
@Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "/sql/cleanup.sql")
@Sql(executionPhase = AFTER_TEST_METHOD, scripts = "/sql/cleanup.sql")
class ProcessSimulatorTest {

    private static final String PAYMENT_BPMN_URL = "/simulator/payment_process.json";

    @Autowired
    private CamundaProcessSimulator processSimulator;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnNoError() {
        final ProcessSimulationRequest processSimulationRequest = readJson(PAYMENT_BPMN_URL);
        final ProcessSimulationResult simulationResult = processSimulator.simulate(processSimulationRequest);
        assertTrue(simulationResult.getErrors().isEmpty());
    }

    @Test
    void shouldFailWithWrongStepId() {
        //given
        final ProcessSimulationRequest processSimulationRequest = readJson(PAYMENT_BPMN_URL);
        processSimulationRequest.getSteps()
                .get(0)
                .setId("NEW_TASK_ID");

        //when
        final ProcessSimulationResult simulationResult = processSimulator.simulate(processSimulationRequest);

        //then
        assertThat(simulationResult.getErrors(), contains(
                allOf(
                        hasProperty("stepId", is("NEW_TASK_ID")),
                        hasProperty("field", is(Field.ID)),
                        hasProperty("expectedFieldValue", is("NEW_TASK_ID")),
                        hasProperty("actualFieldValue", nullValue())
                )
        ));
    }

    @Test
    void shouldFailWithWrongStepName() {
        //given
        final ProcessSimulationRequest processSimulationRequest = readJson(PAYMENT_BPMN_URL);
        processSimulationRequest.getSteps()
                .get(0)
                .setName("NEW_TASK_NAME");

        //when
        final ProcessSimulationResult simulationResult = processSimulator.simulate(processSimulationRequest);

        //then
        assertThat(simulationResult.getErrors(), contains(
                allOf(
                        hasProperty("stepId", is("paymentTask")),
                        hasProperty("field", is(Field.NAME)),
                        hasProperty("expectedFieldValue", is("NEW_TASK_NAME")),
                        hasProperty("actualFieldValue", is("Payment Task"))
                )
        ));
    }

    @Test
    void shouldFailWithWrongStepAssignee() {
        //given
        final ProcessSimulationRequest processSimulationRequest = readJson(PAYMENT_BPMN_URL);
        processSimulationRequest.getSteps()
                .get(0)
                .setAssignee("NEW_TASK_ASSIGNEE");

        //when
        final ProcessSimulationResult simulationResult = processSimulator.simulate(processSimulationRequest);

        //then
        assertThat(simulationResult.getErrors(), contains(
                allOf(
                        hasProperty("stepId", is("paymentTask")),
                        hasProperty("field", is(Field.ASSIGNEE)),
                        hasProperty("expectedFieldValue", is("NEW_TASK_ASSIGNEE")),
                        hasProperty("actualFieldValue", is("demo"))
                )
        ));
    }

    @Test
    void shouldFailWithInvalidProcessVariable() {
        //given
        final ProcessSimulationRequest processSimulationRequest = readJson(PAYMENT_BPMN_URL);
        final Map<String, Object> processVariables = processSimulationRequest.getSteps()
                .get(0)
                .getProcessVariables();
        processVariables.put("new-key", "new-value");
        processVariables.put("amount", 999);
        processVariables.put("description", "new dummy description");

        //when
        final ProcessSimulationResult simulationResult = processSimulator.simulate(processSimulationRequest);

        //then
        assertThat(simulationResult.getErrors(), containsInAnyOrder(
                allOf(
                        hasProperty("stepId", is("paymentTask")),
                        hasProperty("field", is(Field.PROCESS_VARIABLE)),
                        hasProperty("expectedFieldValue", is("{new-key=new-value}")),
                        hasProperty("actualFieldValue", nullValue())
                ),
                allOf(
                        hasProperty("stepId", is("paymentTask")),
                        hasProperty("field", is(Field.PROCESS_VARIABLE)),
                        hasProperty("expectedFieldValue", is("{amount=999}")),
                        hasProperty("actualFieldValue", is("{amount=100}"))
                ),
                allOf(
                        hasProperty("stepId", is("paymentTask")),
                        hasProperty("field", is(Field.PROCESS_VARIABLE)),
                        hasProperty("expectedFieldValue", is("{description=new dummy description}")),
                        hasProperty("actualFieldValue", is("{description=this is a test description}"))
                )
        ));
    }

    @SneakyThrows
    private ProcessSimulationRequest readJson(@Nonnull final String bpmnUrl) {
        final String jsonContent = JsonUtil.readFile(bpmnUrl);
        return objectMapper.readValue(jsonContent, ProcessSimulationRequest.class);
    }
}
