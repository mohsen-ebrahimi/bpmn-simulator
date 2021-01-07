package io.bpmnsimulator.core.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;

import static lombok.AccessLevel.PROTECTED;

@Data
@NoArgsConstructor(access = PROTECTED)
public class PostCondition {

    @Nullable
    private String transition;

}
