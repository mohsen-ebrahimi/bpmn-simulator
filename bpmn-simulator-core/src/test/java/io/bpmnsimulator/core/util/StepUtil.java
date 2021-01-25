package io.bpmnsimulator.core.util;

import io.bpmnsimulator.core.model.Field;
import io.bpmnsimulator.core.model.ProcessSimulationRequest;
import io.bpmnsimulator.core.model.Step;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;

import static io.bpmnsimulator.core.util.CollectorsUtil.onlyElement;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class StepUtil {

    @Nonnull
    public static Step getStep(@Nonnull final ProcessSimulationRequest processSimulationRequest,
                               @Nonnull final String stepName) {
        return processSimulationRequest.getSteps()
                .stream()
                .filter(step -> stepName.equals(step.getId()))
                .findAny()
                .orElseThrow(AssertionError::new);
    }

    @Nonnull
    public static <T> T getPreConditionValue(@Nonnull final ProcessSimulationRequest processSimulationRequest,
                                             @Nonnull final String stepName,
                                             @Nonnull final Field field,
                                             @Nonnull final Class<T> clazz) {
        return getStep(processSimulationRequest, stepName)
                .getPreconditions()
                .stream()
                .filter(condition -> condition.getField().equals(field))
                .map(condition -> clazz.cast(condition.getExpectedValue()))
                .collect(onlyElement());
    }

}
