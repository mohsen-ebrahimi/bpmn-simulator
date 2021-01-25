package io.bpmnsimulator.core.simulator;

import io.bpmnsimulator.core.BpmnTest;
import io.bpmnsimulator.core.model.ProcessSimulationError;
import io.bpmnsimulator.core.model.ProcessSimulationRequest;
import io.bpmnsimulator.core.model.ProcessSimulationResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import java.util.List;

import static io.bpmnsimulator.core.model.Field.CANDIDATE_GROUPS;
import static io.bpmnsimulator.core.util.JsonUtil.readJsonWithRelativePath;
import static io.bpmnsimulator.core.util.StepUtil.getPreConditionValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@BpmnTest
class CandidateGroupsValidationTest {

    private static final String PAYMENT_STEP_NAME = "paymentTask";

    private static final String REQUEST_URL = "request/request-with-candidate-groups.json";

    @Autowired
    private CamundaProcessSimulator processSimulator;

    @Test
    void shouldReturnNoErrorWhenCandidateGroupsMatch() {
        //given
        final ProcessSimulationRequest processSimulationRequest = readJsonWithRelativePath(REQUEST_URL, ProcessSimulationRequest.class);

        //when
        final ProcessSimulationResult processSimulationResult = processSimulator.simulate(processSimulationRequest);

        //then
        assertTrue(processSimulationResult.getErrors().isEmpty());
    }

    @Test
    void shouldReturnNoErrorWhenOrderNotMatch() {
        //given
        final ProcessSimulationRequest processSimulationRequest = readJsonWithRelativePath(REQUEST_URL, ProcessSimulationRequest.class);
        final List<String> expectedCandidateGroups = getExpectedCandidateGroups(processSimulationRequest);
        expectedCandidateGroups.clear();
        expectedCandidateGroups.addAll(List.of("hr", "accounting")); //shuffled list of candidate groups

        //when
        final ProcessSimulationResult processSimulationResult = processSimulator.simulate(processSimulationRequest);

        //then
        assertTrue(processSimulationResult.getErrors().isEmpty());
    }

    @Test
    void shouldFailWhenExpectedCandidateGroupsAreLessThanActualCandidateGroups() {
        //given
        final ProcessSimulationRequest processSimulationRequest = readJsonWithRelativePath(REQUEST_URL, ProcessSimulationRequest.class);
        final List<String> expectedCandidateGroups = getExpectedCandidateGroups(processSimulationRequest);
        expectedCandidateGroups.clear();
        expectedCandidateGroups.addAll(List.of("hr")); // 'accounting' missed

        //when
        final ProcessSimulationResult simulationResult = processSimulator.simulate(processSimulationRequest);

        //then
        assertThat(simulationResult.getErrors(), contains(
                allOf(
                        hasProperty("stepId", is(PAYMENT_STEP_NAME)),
                        hasProperty("field", is(CANDIDATE_GROUPS))
                )
        ));

        assertCandidateGroups(simulationResult,
                new String[]{"hr"},
                new String[]{"hr", "accounting"});
    }

    @Test
    void shouldFailWhenExpectedCandidateGroupsAreMoreThanActualCandidateGroups() {
        //given
        final ProcessSimulationRequest processSimulationRequest = readJsonWithRelativePath(REQUEST_URL, ProcessSimulationRequest.class);
        final List<String> expectedCandidateGroups = getExpectedCandidateGroups(processSimulationRequest);
        expectedCandidateGroups.clear();
        expectedCandidateGroups.addAll(List.of("hr", "marketing", "accounting")); //'marketing' is redundant

        //when
        final ProcessSimulationResult simulationResult = processSimulator.simulate(processSimulationRequest);

        //then
        assertThat(simulationResult.getErrors(), contains(
                allOf(
                        hasProperty("stepId", is(PAYMENT_STEP_NAME)),
                        hasProperty("field", is(CANDIDATE_GROUPS))
                )
        ));

        assertCandidateGroups(simulationResult,
                new String[]{"hr", "marketing", "accounting"},
                new String[]{"hr", "accounting"});
    }

    @Test
    void shouldFailWhenOneCandidateGroupNotMatch() {
        //given
        final ProcessSimulationRequest processSimulationRequest = readJsonWithRelativePath(REQUEST_URL, ProcessSimulationRequest.class);
        final List<String> expectedCandidateGroups = getExpectedCandidateGroups(processSimulationRequest);
        expectedCandidateGroups.clear();
        expectedCandidateGroups.addAll(List.of("hr", "marketing")); // 'marketing' not match

        //when
        final ProcessSimulationResult simulationResult = processSimulator.simulate(processSimulationRequest);

        //then
        assertThat(simulationResult.getErrors(), contains(
                allOf(
                        hasProperty("stepId", is(PAYMENT_STEP_NAME)),
                        hasProperty("field", is(CANDIDATE_GROUPS))
                )
        ));

        assertCandidateGroups(simulationResult,
                new String[]{"hr", "marketing"},
                new String[]{"hr", "accounting"});
    }

    private void assertCandidateGroups(@Nonnull final ProcessSimulationResult simulationResult,
                                       @Nonnull final String[] expectedCandidateGroups,
                                       @Nonnull final String[] actualCandidateGroups) {
        final ProcessSimulationError simulationError = simulationResult.getErrors().get(0);
        assertThat(simulationError.getExpectedFieldValue(), notNullValue());
        assertThat(simulationError.getActualFieldValue(), notNullValue());

        assertThat(splitCandidateGroups(simulationError.getExpectedFieldValue()),
                arrayContainingInAnyOrder(expectedCandidateGroups));

        assertThat(splitCandidateGroups(simulationError.getActualFieldValue()),
                arrayContainingInAnyOrder(actualCandidateGroups));
    }

    @Nonnull
    private String[] splitCandidateGroups(@Nonnull final String candidateGroups) {
        return candidateGroups.replace("[", "")
                .replace("]", "")
                .replace(" ", "")
                .split(",");
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    private List<String> getExpectedCandidateGroups(@Nonnull final ProcessSimulationRequest processSimulationRequest) {
        return getPreConditionValue(processSimulationRequest, PAYMENT_STEP_NAME, CANDIDATE_GROUPS, List.class);
    }

}
