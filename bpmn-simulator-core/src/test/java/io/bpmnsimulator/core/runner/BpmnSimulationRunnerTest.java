package io.bpmnsimulator.core.runner;

import io.bpmnsimulator.core.model.Field;
import io.bpmnsimulator.core.model.ProcessSimulationResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BpmnSimulationRunnerTest {

    @Test
    void runSuccessfulTests() {
        final List<ProcessSimulationResult> simulationResults = BpmnSimulationRunner.run("request/successful-tests");
        assertThat(simulationResults, contains(
                allOf(
                        hasProperty("bpmnUrl", is("bpmn/payment-process-with-candidate-users-and-groups.bpmn")),
                        hasProperty("errors", empty())
                )
        ));
    }

    @Test
    void runFailTests() {
        final List<ProcessSimulationResult> simulationResults = BpmnSimulationRunner.run("request/fail-tests");
        assertThat(simulationResults.size(), is(2));
        assertThat(simulationResults, hasItem(
                allOf(
                        hasProperty("bpmnUrl", is("bpmn/payment-process.bpmn")),
                        hasProperty("errors", contains(
                                allOf(
                                        hasProperty("stepId", is("paymentTask")),
                                        hasProperty("field", is(Field.NAME)),
                                        hasProperty("expectedFieldValue", is("NEW TASK NAME")),
                                        hasProperty("actualFieldValue", is("Payment Task")))))
                )
        ));
        assertThat(simulationResults, hasItem(
                allOf(
                        hasProperty("bpmnUrl", is("bpmn/payment-process.bpmn")),
                        hasProperty("errors", contains(
                                allOf(hasProperty("stepId", is("paymentTask")),
                                        hasProperty("field", is(Field.ASSIGNEE)),
                                        hasProperty("expectedFieldValue", is("NEW TASK ASSIGNEE")),
                                        hasProperty("actualFieldValue", is("demo")))
                        ))
                )
        ));
    }

    @Test
    void runTestsWithInvalidPath() {
        assertThrows(NullPointerException.class, () -> BpmnSimulationRunner.run("request/invalid-path"));
    }
}
