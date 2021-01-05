package io.workflow.bpmnsimulator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;

import static lombok.AccessLevel.PROTECTED;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class PostCondition {

    @Nullable
    private String transition;

}
