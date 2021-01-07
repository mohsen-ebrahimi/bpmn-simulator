package io.bpmnsimulator.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.AutoCloseInputStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.charset.Charset;
import java.util.Arrays;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class JsonUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @SneakyThrows
    public static String readFile(@Nonnull final String path, final Object... args) {
        final String jsonContent = IOUtils.toString(new AutoCloseInputStream(
                JsonUtil.class.getResourceAsStream(path)), Charset.defaultCharset());
        return String.format(jsonContent, Arrays.stream(args).map(JsonUtil::toStringJson).toArray());
    }

    @SneakyThrows
    public static <T> T readJson(@Nonnull final String bpmnUrl,
                                 @Nonnull final Class<T> clazz,
                                 final Object... args) {
        final String jsonContent = JsonUtil.readFile(bpmnUrl, args);
        return OBJECT_MAPPER.readValue(jsonContent, clazz);
    }

    @SneakyThrows
    private static String toStringJson(@Nullable final Object obj) {
        return OBJECT_MAPPER.writeValueAsString(obj);

    }

}
