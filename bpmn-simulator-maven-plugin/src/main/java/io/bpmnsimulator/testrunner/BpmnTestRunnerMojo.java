package io.bpmnsimulator.testrunner;

import io.bpmnsimulator.core.runner.BpmnSimulationRunner;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import static java.lang.String.format;

@Mojo(name = "bpmn-test-runner", defaultPhase = LifecyclePhase.COMPILE)
public class BpmnTestRunnerMojo extends AbstractMojo {

    @Parameter(property = "resource")
    private String resource;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    public void execute() {
        final String outputDirectory = project.getBuild().getOutputDirectory();
        final String path = outputDirectory + resource;
        getLog().info(format("Reading request files from path: [%s] ", path));
        BpmnSimulationRunner.run(path);
    }

}
