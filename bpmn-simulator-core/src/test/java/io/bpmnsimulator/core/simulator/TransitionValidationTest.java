package io.bpmnsimulator.core.simulator;

import io.bpmnsimulator.core.configuration.SimulatorConfiguration;
import io.bpmnsimulator.core.model.Field;
import io.bpmnsimulator.core.model.ProcessSimulationRequest;
import io.bpmnsimulator.core.model.ProcessSimulationResult;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static io.bpmnsimulator.core.simulator.ProcessSimulationContextHolder.getProcessSimulationResult;
import static io.bpmnsimulator.core.util.JsonUtil.readJson;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SimulatorConfiguration.class)
@Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "/sql/cleanup.sql")
@Sql(executionPhase = AFTER_TEST_METHOD, scripts = "/sql/cleanup.sql")
class TransitionValidationTest {

    private static final String TRANSITION_VALIDATION_SIMULATION_REQUEST_JSON = "/simulator/transition-validation-simulation-request.json";

    @Autowired
    private CamundaProcessSimulator processSimulator;

    @Test
    void shouldReturnNoErrorWhenTransitionIsValid() {
        //given
        final ProcessSimulationRequest processSimulationRequest = readJson(TRANSITION_VALIDATION_SIMULATION_REQUEST_JSON,
                ProcessSimulationRequest.class, "one", "flowOne");

        //when
        processSimulator.startSimulation(processSimulationRequest);

        //then
        final ProcessSimulationResult processSimulationResult = getProcessSimulationResult();
        assertTrue(processSimulationResult.getErrors().isEmpty());
    }

    @Test
    void shouldReturnErrorWhenTransitionIsNotCorrect() {
        //given
        final ProcessSimulationRequest processSimulationRequest = readJson(TRANSITION_VALIDATION_SIMULATION_REQUEST_JSON,
                ProcessSimulationRequest.class, "two", "flowFour");

        //when
        processSimulator.startSimulation(processSimulationRequest);

        //then
        final ProcessSimulationResult simulationResult = getProcessSimulationResult();
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
        final ProcessSimulationRequest processSimulationRequest = readJson(TRANSITION_VALIDATION_SIMULATION_REQUEST_JSON,
                ProcessSimulationRequest.class, "four", "flowOne");

        //when
        processSimulator.startSimulation(processSimulationRequest);

        //then
        final ProcessSimulationResult simulationResult = getProcessSimulationResult();
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
