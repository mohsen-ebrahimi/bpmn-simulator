package io.bpmnsimulator.core.util;

import io.bpmnsimulator.core.model.ProcessSimulationRequest;
import io.bpmnsimulator.core.model.Step;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class StepUtil {

    public static Step getStep(@Nonnull final ProcessSimulationRequest processSimulationRequest,
                               @Nonnull final String stepName) {
        return processSimulationRequest.getSteps()
                .stream()
                .filter(step -> stepName.equals(step.getId()))
                .findAny()
                .orElseThrow(AssertionError::new);
    }

}
