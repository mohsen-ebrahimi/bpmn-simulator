package io.bpmnsimulator.core.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static lombok.AccessLevel.PROTECTED;

@Data
@NoArgsConstructor(access = PROTECTED)
public class Step {

    @Nonnull
    private String id;

    @Nonnull
    private List<Precondition<Object>> preconditions = new ArrayList<>();

    @Nonnull
    private List<PostCondition<Object>> postConditions = new ArrayList<>();

    @Nonnull
    private Map<String, Object> processVariables = new HashMap<>();

}
