package io.bpmnsimulator.core.runner;

import io.bpmnsimulator.core.model.ProcessSimulationRequest;
import io.bpmnsimulator.core.model.ProcessSimulationResult;
import io.bpmnsimulator.core.simulator.ProcessSimulator;
import io.bpmnsimulator.core.util.JsonUtil;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class BpmnSimulationRunner {

    @Nonnull
    public static List<ProcessSimulationResult> run(@Nonnull final String baseResource) {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final URL resource = requireNonNull(classLoader.getResource(baseResource), String.format("Path: [%s] does not exist", baseResource));

        try (Stream<Path> paths = Files.walk(Paths.get(resource.getPath()))) {
            return paths.filter(Files::isRegularFile)
                    .map(path -> JsonUtil.readJsonWithAbsolutePath(path, ProcessSimulationRequest.class))
                    .map(Holder.PROCESS_SIMULATOR::simulate)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("Exception in reading files in path: [%s]", baseResource), e);
        }
    }

    private static class Holder {
        
        private static final ProcessSimulator PROCESS_SIMULATOR = createProcessSimulator();

        @Nonnull
        private static ProcessSimulator createProcessSimulator() {
            ApplicationContext context =
                    new AnnotationConfigApplicationContext("io.bpmnsimulator.core.configuration");
            return context.getBean(ProcessSimulator.class);
        }
    }
}
