package io.bpmnsimulator.core.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

    @Nullable
    private String name;

    @Nullable
    private String assignee;

    @Nonnull
    private List<String> candidateUsers = new ArrayList<>();

    @Nullable
    private Precondition precondition;

    @Nullable
    private PostCondition postCondition;

    @Nonnull
    private Map<String, Object> processVariables = new HashMap<>();
}