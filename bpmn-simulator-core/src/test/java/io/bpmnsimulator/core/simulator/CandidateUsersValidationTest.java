package io.bpmnsimulator.core.simulator;

import io.bpmnsimulator.core.BpmnTest;
import io.bpmnsimulator.core.model.Field;
import io.bpmnsimulator.core.model.ProcessSimulationError;
import io.bpmnsimulator.core.model.ProcessSimulationRequest;
import io.bpmnsimulator.core.model.ProcessSimulationResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import java.util.List;

import static io.bpmnsimulator.core.util.JsonUtil.readJsonWithRelativePath;
import static io.bpmnsimulator.core.util.StepUtil.getPreConditionValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@BpmnTest
class CandidateUsersValidationTest {

    private static final String PAYMENT_STEP_NAME = "paymentTask";

    private static final String PAYMENT_PROCESS_WITH_CANDIDATE_USERS_REQUEST_URL = "request/request-with-candidate-users.json";

    @Autowired
    private CamundaProcessSimulator processSimulator;

    @Test
    void shouldReturnNoErrorWhenCandidateUsersMatch() {
        //given
        final ProcessSimulationRequest processSimulationRequest = readJsonWithRelativePath(PAYMENT_PROCESS_WITH_CANDIDATE_USERS_REQUEST_URL, ProcessSimulationRequest.class);

        //when
        final ProcessSimulationResult processSimulationResult = processSimulator.simulate(processSimulationRequest);

        //then
        assertTrue(processSimulationResult.getErrors().isEmpty());
    }

    @Test
    void shouldReturnNoErrorWhenOrderNotMatch() {
        //given
        final ProcessSimulationRequest processSimulationRequest = readJsonWithRelativePath(PAYMENT_PROCESS_WITH_CANDIDATE_USERS_REQUEST_URL, ProcessSimulationRequest.class);
        final List<String> expectedUserCandidates = getExpectedCandidateUsers(processSimulationRequest);
        expectedUserCandidates.clear();
        expectedUserCandidates.addAll(List.of("supervisor", "demo", "manager")); //shuffled list of candidate users

        //when
        final ProcessSimulationResult processSimulationResult = processSimulator.simulate(processSimulationRequest);

        //then
        assertTrue(processSimulationResult.getErrors().isEmpty());
    }

    @Test
    void shouldFailWhenExpectedCandidateUsersAreLessThanActualCandidateUsers() {
        //given
        final ProcessSimulationRequest processSimulationRequest = readJsonWithRelativePath(PAYMENT_PROCESS_WITH_CANDIDATE_USERS_REQUEST_URL, ProcessSimulationRequest.class);
        final List<String> expectedUserCandidates = getExpectedCandidateUsers(processSimulationRequest);
        expectedUserCandidates.clear();
        expectedUserCandidates.addAll(List.of("demo", "supervisor")); // 'manager' missed

        //when
        final ProcessSimulationResult simulationResult = processSimulator.simulate(processSimulationRequest);

        //then
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
        final ProcessSimulationRequest processSimulationRequest = readJsonWithRelativePath(PAYMENT_PROCESS_WITH_CANDIDATE_USERS_REQUEST_URL, ProcessSimulationRequest.class);
        final List<String> expectedUserCandidates = getExpectedCandidateUsers(processSimulationRequest);
        expectedUserCandidates.clear();
        expectedUserCandidates.addAll(List.of("supervisor", "developer", "demo", "manager")); //'developer' is redundant

        //when
        final ProcessSimulationResult simulationResult = processSimulator.simulate(processSimulationRequest);

        //then
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
        final ProcessSimulationRequest processSimulationRequest = readJsonWithRelativePath(PAYMENT_PROCESS_WITH_CANDIDATE_USERS_REQUEST_URL, ProcessSimulationRequest.class);
        final List<String> expectedUserCandidates = getExpectedCandidateUsers(processSimulationRequest);
        expectedUserCandidates.clear();
        expectedUserCandidates.addAll(List.of("supervisor", "developer", "manager")); // 'developer' not match

        //when
        final ProcessSimulationResult simulationResult = processSimulator.simulate(processSimulationRequest);

        //then
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

    @Nonnull
    @SuppressWarnings("unchecked")
    private List<String> getExpectedCandidateUsers(@Nonnull final ProcessSimulationRequest processSimulationRequest) {
        return getPreConditionValue(processSimulationRequest, PAYMENT_STEP_NAME, Field.CANDIDATE_USERS, List.class);
    }

}
