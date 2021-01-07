package io.workflow.bpmnsimulator.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;
import java.util.Map;

import static lombok.AccessLevel.PROTECTED;

@Data
@NoArgsConstructor(access = PROTECTED)
public class Precondition {

    @Nonnull
    private Map<String, Object> expectedProcessVariables;

}
