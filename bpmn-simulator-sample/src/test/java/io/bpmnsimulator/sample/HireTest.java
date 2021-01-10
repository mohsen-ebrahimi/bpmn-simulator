package io.bpmnsimulator.sample;

import io.bpmnsimulator.core.model.*;
import io.bpmnsimulator.core.simulator.ProcessSimulator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;

import static io.bpmnsimulator.core.util.JsonUtil.readJson;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@BpmnTest
class HireTest {

    private static final String HIRING_SIMULATION_REQUEST_URL = "/request/hiring-process-simulation-request.json";

    private static final String INTERVIEW_STEP_ID = "interviewTask";

    private static final String INTERVIEW_TASK_NAME = "Interview Task";

    @Autowired
    private ProcessSimulator processSimulator;

    @Test
    void testSample() {
        //given
        final ProcessSimulationRequest processSimulationRequest = readJson(HIRING_SIMULATION_REQUEST_URL, ProcessSimulationRequest.class);

        //when
        final ProcessSimulationResult processSimulationResult = processSimulator.simulate(processSimulationRequest);

        //then
        assertTrue(processSimulationResult.getErrors().isEmpty());
    }

    @Test
    void shouldReturnNoErrorWhenPreConditionIsNull() {
        //given
        final ProcessSimulationRequest processSimulationRequest = readJson(HIRING_SIMULATION_REQUEST_URL, ProcessSimulationRequest.class);
        getStep(processSimulationRequest, INTERVIEW_STEP_ID).setPrecondition(null);

        //when
        final ProcessSimulationResult processSimulationResult = processSimulator.simulate(processSimulationRequest);

        //then
        assertTrue(processSimulationResult.getErrors().isEmpty());
    }

    @Test
    void shouldFailWithWrongStepName() {
        //given
        final ProcessSimulationRequest processSimulationRequest = readJson(HIRING_SIMULATION_REQUEST_URL, ProcessSimulationRequest.class);
        getStep(processSimulationRequest, INTERVIEW_STEP_ID).setName("NEW_TASK_NAME");

        //when
        final ProcessSimulationResult simulationResult = processSimulator.simulate(processSimulationRequest);

        //then
        assertThat(simulationResult.getErrors(), contains(
                allOf(
                        hasProperty("stepId", is(INTERVIEW_STEP_ID)),
                        hasProperty("field", is(Field.NAME)),
                        hasProperty("expectedFieldValue", is("NEW_TASK_NAME")),
                        hasProperty("actualFieldValue", is(INTERVIEW_TASK_NAME))
                )
        ));
    }

    @Test
    void shouldFailWithWrongStepAssignee() {
        //given
        final ProcessSimulationRequest processSimulationRequest = readJson(HIRING_SIMULATION_REQUEST_URL, ProcessSimulationRequest.class);
        getStep(processSimulationRequest, INTERVIEW_STEP_ID).setAssignee("NEW_TASK_ASSIGNEE");

        //when
        final ProcessSimulationResult simulationResult = processSimulator.simulate(processSimulationRequest);

        //then
        assertThat(simulationResult.getErrors(), contains(
                allOf(
                        hasProperty("stepId", is(INTERVIEW_STEP_ID)),
                        hasProperty("field", is(Field.ASSIGNEE)),
                        hasProperty("expectedFieldValue", is("NEW_TASK_ASSIGNEE")),
                        hasProperty("actualFieldValue", is("hr"))
                )
        ));
    }

    private Step getStep(@Nonnull final ProcessSimulationRequest processSimulationRequest,
                         @Nonnull final String stepName) {
        return processSimulationRequest.getSteps()
                .stream()
                .filter(step -> stepName.equals(step.getId()))
                .findAny()
                .orElseThrow(AssertionError::new);
    }

}
