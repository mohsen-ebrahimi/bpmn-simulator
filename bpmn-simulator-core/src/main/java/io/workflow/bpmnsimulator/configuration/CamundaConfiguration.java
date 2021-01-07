package io.workflow.bpmnsimulator.configuration;

import io.workflow.bpmnsimulator.listener.DelegateEventParseListener;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.camunda.bpm.engine.impl.cfg.StandaloneInMemProcessEngineConfiguration;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Configuration
public class CamundaConfiguration {

    @Bean
    public ProcessEngineConfiguration processEngineConfiguration(ApplicationEventPublisher publisher) {
        ProcessEngineConfigurationImpl processEngineConfiguration = new StandaloneInMemProcessEngineConfiguration();
        processEngineConfiguration.setProcessEngineName("engine");
        processEngineConfiguration.setDatabaseSchemaUpdate("true");
        processEngineConfiguration.setJobExecutorActivate(false);
        processEngineConfiguration.setCustomPostBPMNParseListeners(new ArrayList<>());
        processEngineConfiguration.getCustomPostBPMNParseListeners().add(new DelegateEventParseListener(publisher));

        return processEngineConfiguration;
    }

    @Bean
    public ProcessEngineFactoryBean processEngineFactoryBean(ProcessEngineConfiguration processEngineConfiguration) {
        ProcessEngineFactoryBean factoryBean = new ProcessEngineFactoryBean();
        factoryBean.setProcessEngineConfiguration(processEngineConfiguration);

        return factoryBean;
    }

}
