package io.bpmnsimulator.core.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;
import java.util.Arrays;

import static io.bpmnsimulator.core.util.CollectorsUtil.onlyElement;

@RequiredArgsConstructor
public enum Field {
    ID("id"),
    NAME("name"),
    ASSIGNEE("assignee"),
    CANDIDATE_USERS("candidateUsers"),
    CANDIDATE_GROUPS("candidateGroups"),
    PROCESS_VARIABLES("processVariables"),
    TRANSITION("transition"),
    ;

    @Getter
    @JsonValue
    private final String name;

    @Nonnull
    @JsonCreator
    @SuppressWarnings("unused")
    public static Field forValue(@Nonnull final String fieldName) {
        return Arrays.stream(Field.values())
                .filter(field -> field.getName().equals(fieldName))
                .collect(onlyElement());
    }

}
