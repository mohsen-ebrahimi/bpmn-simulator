package io.bpmnsimulator.core.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static lombok.AccessLevel.PROTECTED;

@Data
@NoArgsConstructor(access = PROTECTED)
public class Condition<T> {

    @Nonnull
    private Field field;

    @Nullable
    private T expectedValue;

}
