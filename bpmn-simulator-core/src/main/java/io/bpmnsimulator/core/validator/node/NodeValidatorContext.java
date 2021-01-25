package io.bpmnsimulator.core.validator.node;

import io.bpmnsimulator.core.model.Condition;
import lombok.Builder;
import lombok.Data;
import org.camunda.bpm.engine.delegate.DelegateExecution;

import javax.annotation.Nonnull;

@Data
@Builder
public class NodeValidatorContext<T> {

    @Nonnull
    private final DelegateExecution delegateExecution;

    @Nonnull
    private String stepId;

    @Nonnull
    private Condition<T> condition;

}
