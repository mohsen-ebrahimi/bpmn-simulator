package io.bpmnsimulator.core.runner;

import io.bpmnsimulator.core.model.ProcessSimulationRequest;
import io.bpmnsimulator.core.model.ProcessSimulationResult;
import io.bpmnsimulator.core.simulator.ProcessSimulator;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static io.bpmnsimulator.core.util.JsonUtil.readJsonWithAbsolutePath;
import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class BpmnSimulationRunner {

    @Nonnull
    public static List<ProcessSimulationResult> run(@Nonnull final String baseResource) {
        final File[] files = requireNonNull(new File(baseResource).listFiles(), String.format("Path name: [%s] does not denote a directory", baseResource));

        return Arrays.stream(files)
                .map(File::getPath)
                .map(filePath -> readJsonWithAbsolutePath(filePath, ProcessSimulationRequest.class))
                .map(Holder.PROCESS_SIMULATOR::simulate)
                .collect(Collectors.toList());
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
