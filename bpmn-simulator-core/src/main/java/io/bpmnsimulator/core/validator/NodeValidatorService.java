package io.bpmnsimulator.core.validator;

import io.bpmnsimulator.core.model.Condition;
import io.bpmnsimulator.core.model.Field;
import io.bpmnsimulator.core.model.ProcessSimulationError;
import io.bpmnsimulator.core.model.Step;
import io.bpmnsimulator.core.validator.node.NodeValidator;
import io.bpmnsimulator.core.validator.node.NodeValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;

@Slf4j
@Service
public class NodeValidatorService {

    private final Map<Field, List<NodeValidator>> nodeValidators;

    public NodeValidatorService(@Nonnull final List<NodeValidator<?>> nodeValidators) {
        this.nodeValidators = nodeValidators.stream()
                .collect(groupingBy(Validator::getSupportedField));
    }

    @Nonnull
    public List<ProcessSimulationError> preValidateNode(@Nonnull final Step step,
                                                        @Nonnull final DelegateExecution delegateExecution) {
        log.debug("Pre-validating node with step: [{}]", step);

        return validateAllCondition(step, delegateExecution, step.getPreconditions());
    }

    @Nonnull
    public List<ProcessSimulationError> postValidateNode(@Nonnull final Step step,
                                                         @Nonnull final DelegateExecution delegateExecution) {
        log.debug("Post-validating node with step: [{}]", step);

        return validateAllCondition(step, delegateExecution, step.getPostConditions());
    }

    @Nonnull
    private List<ProcessSimulationError> validateAllCondition(@Nonnull final Step step,
                                                              @Nonnull final DelegateExecution delegateExecution,
                                                              @Nonnull final List<? extends Condition<Object>> conditions) {
        return conditions.stream()
                .map(condition -> toNodeValidationContext(step, delegateExecution, condition))
                .flatMap(this::validateCondition)
                .collect(Collectors.toList());
    }

    @Nonnull
    private NodeValidatorContext<Object> toNodeValidationContext(@Nonnull final Step step,
                                                                 @Nonnull final DelegateExecution delegateExecution,
                                                                 @Nonnull final Condition<Object> condition) {
        return NodeValidatorContext.builder()
                .stepId(step.getId())
                .condition(condition)
                .delegateExecution(delegateExecution)
                .build();
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    private Stream<ProcessSimulationError> validateCondition(@Nonnull final NodeValidatorContext<?> context) {
        return getNodeValidators(context.getCondition().getField())
                .stream()
                .flatMap(validator -> validator.validate(context).stream());
    }

    @Nonnull
    private List<NodeValidator> getNodeValidators(@Nonnull final Field field) {
        return nodeValidators.getOrDefault(field, List.of());
    }

}
