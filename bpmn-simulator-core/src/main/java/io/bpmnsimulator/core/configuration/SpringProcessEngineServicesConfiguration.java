package io.bpmnsimulator.core.configuration;

import org.camunda.bpm.engine.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringProcessEngineServicesConfiguration implements ProcessEngineServices {

    @Autowired
    private ProcessEngine processEngine;

    @Bean
    @Override
    public RuntimeService getRuntimeService() {
        return processEngine.getRuntimeService();
    }

    @Bean
    @Override
    public RepositoryService getRepositoryService() {
        return processEngine.getRepositoryService();
    }

    @Bean
    @Override
    public FormService getFormService() {
        return processEngine.getFormService();
    }

    @Bean
    @Override
    public TaskService getTaskService() {
        return processEngine.getTaskService();
    }

    @Bean
    @Override
    public HistoryService getHistoryService() {
        return processEngine.getHistoryService();
    }

    @Bean
    @Override
    public IdentityService getIdentityService() {
        return processEngine.getIdentityService();
    }

    @Bean
    @Override
    public ManagementService getManagementService() {
        return processEngine.getManagementService();
    }

    @Bean
    @Override
    public AuthorizationService getAuthorizationService() {
        return processEngine.getAuthorizationService();
    }

    @Bean
    @Override
    public CaseService getCaseService() {
        return processEngine.getCaseService();
    }

    @Bean
    @Override
    public FilterService getFilterService() {
        return processEngine.getFilterService();
    }

    @Bean
    @Override
    public ExternalTaskService getExternalTaskService() {
        return processEngine.getExternalTaskService();
    }

    @Bean
    @Override
    public DecisionService getDecisionService() {
        return processEngine.getDecisionService();
    }

}
