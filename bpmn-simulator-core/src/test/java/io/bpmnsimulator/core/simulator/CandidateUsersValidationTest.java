package io.bpmnsimulator.core.simulator;

import io.bpmnsimulator.core.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import javax.annotation.Nonnull;
import java.util.List;

import static io.bpmnsimulator.core.simulator.ProcessSimulationContextHolder.getProcessSimulationResult;
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
class CandidateUsersValidationTest {

    private static final String PAYMENT_STEP_NAME = "paymentTask";

    private static final String PAYMENT_PROCESS_WITH_CANDIDATE_USERS_REQUEST_URL = "/simulator/payment-process-with-candidate-users-simulation-request.json";

    @Autowired
    private CamundaProcessSimulator processSimulator;

    @Test
    void shouldReturnNoErrorWhenCandidateUsersMatch() {
        //given
        final ProcessSimulationRequest processSimulationRequest = readJson(PAYMENT_PROCESS_WITH_CANDIDATE_USERS_REQUEST_URL, ProcessSimulationRequest.class);

        //when
        processSimulator.startSimulation(processSimulationRequest);

        //then
        final ProcessSimulationResult processSimulationResult = getProcessSimulationResult();
        assertTrue(processSimulationResult.getErrors().isEmpty());
    }

    @Test
    void shouldReturnNoErrorWhenOrderNotMatch() {
        //given
        final ProcessSimulationRequest processSimulationRequest = readJson(PAYMENT_PROCESS_WITH_CANDIDATE_USERS_REQUEST_URL, ProcessSimulationRequest.class);
        getStep(processSimulationRequest, PAYMENT_STEP_NAME)
                .setCandidateUsers(List.of("supervisor", "demo", "manager")); //shuffled list of candidate users

        //when
        processSimulator.startSimulation(processSimulationRequest);

        //then
        final ProcessSimulationResult processSimulationResult = getProcessSimulationResult();
        assertTrue(processSimulationResult.getErrors().isEmpty());
    }

    @Test
    void shouldFailWhenExpectedCandidateUsersAreLessThanActualCandidateUsers() {
        //given
        final ProcessSimulationRequest processSimulationRequest = readJson(PAYMENT_PROCESS_WITH_CANDIDATE_USERS_REQUEST_URL, ProcessSimulationRequest.class);
        getStep(processSimulationRequest, PAYMENT_STEP_NAME)
                .setCandidateUsers(List.of("demo", "supervisor")); // 'manager' missed

        //when
        processSimulator.startSimulation(processSimulationRequest);

        //then
        final ProcessSimulationResult simulationResult = getProcessSimulationResult();
        assertThat(simulationResult.getErrors(), contains(
                allOf(
                        hasProperty("stepId", is(PAYMENT_STEP_NAME)),
                        hasProperty("field", is(Field.CANDIDATE_USERS))
                )
        ));

        assertCandidateUsers(simulationResult,
                new String[]{"demo", "supervisor"},
                new String[]{"supervisor", "demo", "manager"});
    }

    @Test
    void shouldFailWhenExpectedCandidateUsersAreMoreThanActualCandidateUsers() {
        //given
        final ProcessSimulationRequest processSimulationRequest = readJson(PAYMENT_PROCESS_WITH_CANDIDATE_USERS_REQUEST_URL, ProcessSimulationRequest.class);
        getStep(processSimulationRequest, PAYMENT_STEP_NAME)
                .setCandidateUsers(List.of("supervisor", "developer", "demo", "manager")); //'developer' is redundant

        //when
        processSimulator.startSimulation(processSimulationRequest);

        //then
        final ProcessSimulationResult simulationResult = getProcessSimulationResult();
        assertThat(simulationResult.getErrors(), contains(
                allOf(
                        hasProperty("stepId", is(PAYMENT_STEP_NAME)),
                        hasProperty("field", is(Field.CANDIDATE_USERS))
                )
        ));

        assertCandidateUsers(simulationResult,
                new String[]{"supervisor", "developer", "demo", "manager"},
                new String[]{"manager", "demo", "supervisor"});
    }

    @Test
    void shouldFailWhenOneCandidateUserNotMatch() {
        //given
        final ProcessSimulationRequest processSimulationRequest = readJson(PAYMENT_PROCESS_WITH_CANDIDATE_USERS_REQUEST_URL, ProcessSimulationRequest.class);
        getStep(processSimulationRequest, PAYMENT_STEP_NAME)
                .setCandidateUsers(List.of("supervisor", "developer", "manager")); // 'developer' not match

        //when
        processSimulator.startSimulation(processSimulationRequest);

        //then
        final ProcessSimulationResult simulationResult = getProcessSimulationResult();
        assertThat(simulationResult.getErrors(), contains(
                allOf(
                        hasProperty("stepId", is(PAYMENT_STEP_NAME)),
                        hasProperty("field", is(Field.CANDIDATE_USERS))
                )
        ));

        assertCandidateUsers(simulationResult,
                new String[]{"supervisor", "developer", "manager"},
                new String[]{"supervisor", "demo", "manager"});
    }

    private void assertCandidateUsers(@Nonnull final ProcessSimulationResult simulationResult,
                                      @Nonnull final String[] expectedCandidateUsers,
                                      @Nonnull final String[] actualCandidateUsers) {
        final ProcessSimulationError simulationError = simulationResult.getErrors().get(0);
        assertThat(simulationError.getExpectedFieldValue(), notNullValue());
        assertThat(simulationError.getActualFieldValue(), notNullValue());

        assertThat(splitCandidateUsers(simulationError.getExpectedFieldValue()),
                arrayContainingInAnyOrder(expectedCandidateUsers));

        assertThat(splitCandidateUsers(simulationError.getActualFieldValue()),
                arrayContainingInAnyOrder(actualCandidateUsers));
    }

    @Nonnull
    private String[] splitCandidateUsers(@Nonnull final String candidateUsers) {
        return candidateUsers.replace("[", "")
                .replace("]", "")
                .replace(" ", "")
                .split(",");
    }
}
