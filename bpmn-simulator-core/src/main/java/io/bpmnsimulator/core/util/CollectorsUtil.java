package io.bpmnsimulator.core.util;

import lombok.NoArgsConstructor;

import javax.annotation.Nonnull;
import java.util.NoSuchElementException;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class CollectorsUtil {

    @Nonnull
    public static <T> Collector<T, ?, T> onlyElement() {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    if (list.size() > 1) {
                        throw new IllegalArgumentException(String.format("More than one value exists for key. %s", list));
                    }

                    if (list.isEmpty()) {
                        throw new NoSuchElementException("One value expected for key, but nothing found");
                    }

                    return list.get(0);
                }
        );
    }

}
