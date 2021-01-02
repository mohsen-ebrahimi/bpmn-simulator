package io.workflow.bpmnsimulator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Map;

import static lombok.AccessLevel.PROTECTED;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class Step {

    @Nonnull
    private String id;

    @Nonnull
    private String name;

    @Nullable
    private String assignee;

    @Nonnull
    private Map<String, Object> processVariables;
}
