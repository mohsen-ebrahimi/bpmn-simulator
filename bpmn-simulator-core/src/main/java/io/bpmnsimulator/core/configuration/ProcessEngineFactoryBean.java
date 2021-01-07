package io.bpmnsimulator.core.configuration;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineConfiguration;
import org.camunda.bpm.engine.impl.ProcessEngineImpl;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.Nonnull;

public class ProcessEngineFactoryBean implements FactoryBean<ProcessEngine>, DisposableBean, ApplicationContextAware {

    protected ProcessEngineConfiguration processEngineConfiguration;
    protected ApplicationContext applicationContext;
    protected ProcessEngineImpl processEngine;

    @Override
    public void setApplicationContext(@Nonnull final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public ProcessEngine getObject() {
        if (processEngine == null) {
            processEngine = (ProcessEngineImpl) processEngineConfiguration.buildProcessEngine();
        }

        return processEngine;
    }

    @Override
    public Class<ProcessEngine> getObjectType() {
        return ProcessEngine.class;
    }

    @Override
    public void destroy() {
        if (processEngine != null) {
            processEngine.close();
        }
    }

    public void setProcessEngineConfiguration(ProcessEngineConfiguration processEngineConfiguration) {
        this.processEngineConfiguration = processEngineConfiguration;
    }
}
