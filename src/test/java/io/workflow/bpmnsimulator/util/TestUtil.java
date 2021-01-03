package io.workflow.bpmnsimulator.util;

import io.workflow.bpmnsimulator.model.ProcessSimulationRequest;
import io.workflow.bpmnsimulator.model.Step;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class TestUtil {

    public static Step getStep(@Nonnull final ProcessSimulationRequest processSimulationRequest,
                               @Nonnull final String stepName) {
        return processSimulationRequest.getSteps()
                .stream()
                .filter(step -> stepName.equals(step.getId()))
                .findAny()
                .orElseThrow(AssertionError::new);
    }

}
