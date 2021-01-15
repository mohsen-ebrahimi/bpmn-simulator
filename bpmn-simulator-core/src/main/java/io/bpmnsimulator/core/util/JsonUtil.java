package io.bpmnsimulator.core.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.AutoCloseInputStream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.FileInputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class JsonUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @SneakyThrows
    public static <T> T readJsonWithRelativePath(@Nonnull final String url,
                                                 @Nonnull final Class<T> clazz,
                                                 final Object... args) {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final URL resource = requireNonNull(classLoader.getResource(url), String.format("Resource: [%s] does not exist", url));
        return readJsonWithAbsolutePath(Paths.get(resource.getPath()), clazz, args);
    }

    @SneakyThrows
    public static <T> T readJsonWithAbsolutePath(@Nonnull final Path path,
                                                 @Nonnull final Class<T> clazz,
                                                 final Object... args) {
        final String jsonContent = JsonUtil.readFile(path.toAbsolutePath().toString(), args);
        return OBJECT_MAPPER.readValue(jsonContent, clazz);
    }

    @SneakyThrows
    private static String readFile(@Nonnull final String url, final Object... args) {
        final String jsonContent = IOUtils.toString(new AutoCloseInputStream(new FileInputStream(url)), Charset.defaultCharset());
        return String.format(jsonContent, Arrays.stream(args).map(JsonUtil::toStringJson).toArray());
    }

    @SneakyThrows
    private static String toStringJson(@Nullable final Object obj) {
        return OBJECT_MAPPER.writeValueAsString(obj);

    }

}
