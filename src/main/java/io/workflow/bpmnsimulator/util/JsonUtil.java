package io.workflow.bpmnsimulator.util;

import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nonnull;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class JsonUtil {

    @SneakyThrows
    public static String readFile(@Nonnull final String path) {
        return IOUtils.toString(JsonUtil.class.getResourceAsStream(path));
    }

}
