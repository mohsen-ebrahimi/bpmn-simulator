package io.workflow.bpmnsimulator.simulator;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.workflow.bpmnsimulator.model.ProcessSimulationError.Field;
import io.workflow.bpmnsimulator.model.ProcessSimulationRequest;
import io.workflow.bpmnsimulator.model.ProcessSimulationResult;
import io.workflow.bpmnsimulator.util.JsonUtil;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import javax.annotation.Nonnull;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@SpringBootTest
@Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "/sql/cleanup.sql")
@Sql(executionPhase = AFTER_TEST_METHOD, scripts = "/sql/cleanup.sql")
class ProcessSimulatorTest {

    @Autowired
    private CamundaProcessSimulator processSimulator;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnNoError() {
        final ProcessSimulationRequest processSimulationRequest = readJson("/simulator/payment_process.json");
        final ProcessSimulationResult simulationResult = processSimulator.simulate(processSimulationRequest);
        assertTrue(simulationResult.getErrors().isEmpty());
    }

    @Test
    void shouldFailWithWrongStepId() {
        //given
        final String bpmnUrl = "/simulator/payment_process.json";
        final ProcessSimulationRequest processSimulationRequest = readJson(bpmnUrl);
        processSimulationRequest.getSteps()
                .get(0)
                .setId("WRONG_TASK_ID");

        //when
        final ProcessSimulationResult simulationResult = processSimulator.simulate(processSimulationRequest);

        //then
        assertThat(simulationResult.getErrors(), contains(
                allOf(
                        hasProperty("stepId", is("WRONG_TASK_ID")),
                        hasProperty("field", is(Field.ID)),
                        hasProperty("actualFieldValue", nullValue()),
                        hasProperty("expectedFieldValue", is("WRONG_TASK_ID"))
                )
        ));
    }

    @SneakyThrows
    private ProcessSimulationRequest readJson(@Nonnull final String bpmnUrl) {
        final String jsonContent = JsonUtil.readFile(bpmnUrl);
        return objectMapper.readValue(jsonContent, ProcessSimulationRequest.class);
    }
}
