package io.bpmnsimulator.core.simulator;

import io.bpmnsimulator.core.BpmnTest;
import io.bpmnsimulator.core.model.Field;
import io.bpmnsimulator.core.model.ProcessSimulationRequest;
import io.bpmnsimulator.core.model.ProcessSimulationResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import java.util.Map;

import static io.bpmnsimulator.core.util.JsonUtil.readJsonWithRelativePath;
import static io.bpmnsimulator.core.util.StepUtil.getPreConditionValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@BpmnTest
class ProcessVariablesValidationTest {

    private static final String PAYMENT_STEP_NAME = "paymentTask";

    private static final String PAYMENT_SIMULATION_REQUEST_URL = "request/request-without-error.json";

    @Autowired
    private CamundaProcessSimulator processSimulator;

    @Test
    void shouldFailWhenProcessVariableNotFound() {
        //given
        final ProcessSimulationRequest processSimulationRequest = readJsonWithRelativePath(PAYMENT_SIMULATION_REQUEST_URL, ProcessSimulationRequest.class);
        final Map<String, Object> expectedProcessVariables = getExpectedProcessVariables(processSimulationRequest);
        expectedProcessVariables.put("new-key", "new-value");
        expectedProcessVariables.put("amount", 999);
        expectedProcessVariables.put("description", "new dummy description");

        //when
        final ProcessSimulationResult simulationResult = processSimulator.simulate(processSimulationRequest);

        //then
        assertThat(simulationResult.getErrors(), containsInAnyOrder(
                allOf(
                        hasProperty("stepId", is(PAYMENT_STEP_NAME)),
                        hasProperty("field", is(Field.PROCESS_VARIABLES)),
                        hasProperty("expectedFieldValue", is("{new-key=new-value}")),
                        hasProperty("actualFieldValue", nullValue())
                ),
                allOf(
                        hasProperty("stepId", is(PAYMENT_STEP_NAME)),
                        hasProperty("field", is(Field.PROCESS_VARIABLES)),
                        hasProperty("expectedFieldValue", is("{amount=999}")),
                        hasProperty("actualFieldValue", nullValue())
                ),
                allOf(
                        hasProperty("stepId", is(PAYMENT_STEP_NAME)),
                        hasProperty("field", is(Field.PROCESS_VARIABLES)),
                        hasProperty("expectedFieldValue", is("{description=new dummy description}")),
                        hasProperty("actualFieldValue", nullValue())
                )
        ));
    }

    @Test
    void shouldFailWhenProcessVariableValueNotMatch() {
        //given
        final ProcessSimulationRequest processSimulationRequest = readJsonWithRelativePath(PAYMENT_SIMULATION_REQUEST_URL, ProcessSimulationRequest.class);
        final Map<String, Object> expectedProcessVariables = getExpectedProcessVariables(processSimulationRequest);
        expectedProcessVariables.put("name", "Baz Foo");
        expectedProcessVariables.put("age", null);

        //when
        final ProcessSimulationResult simulationResult = processSimulator.simulate(processSimulationRequest);

        //then
        assertThat(simulationResult.getErrors(), containsInAnyOrder(
                allOf(
                        hasProperty("stepId", is(PAYMENT_STEP_NAME)),
                        hasProperty("field", is(Field.PROCESS_VARIABLES)),
                        hasProperty("expectedFieldValue", is("{name=Baz Foo}")),
                        hasProperty("actualFieldValue", is("{name=null}"))
                ),
                allOf(
                        hasProperty("stepId", is(PAYMENT_STEP_NAME)),
                        hasProperty("field", is(Field.PROCESS_VARIABLES)),
                        hasProperty("expectedFieldValue", is("{age=null}")),
                        hasProperty("actualFieldValue", is("{age=30}"))
                )
        ));
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    private Map<String, Object> getExpectedProcessVariables(@Nonnull final ProcessSimulationRequest processSimulationRequest) {
        return getPreConditionValue(processSimulationRequest, PAYMENT_STEP_NAME, Field.PROCESS_VARIABLES, Map.class);
    }

}
