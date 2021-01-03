package io.workflow.bpmnsimulator.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;

import javax.annotation.Nonnull;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class JsonUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @SneakyThrows
    public static String readFile(@Nonnull final String path) {
        return IOUtils.toString(JsonUtil.class.getResourceAsStream(path));
    }

    @SneakyThrows
    public static <T> T readJson(@Nonnull final String bpmnUrl,
                                 @Nonnull final Class<T> clazz) {
        final String jsonContent = JsonUtil.readFile(bpmnUrl);
        return OBJECT_MAPPER.readValue(jsonContent, clazz);
    }

}
