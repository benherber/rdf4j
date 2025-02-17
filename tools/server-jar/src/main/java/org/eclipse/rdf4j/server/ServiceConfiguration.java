package org.eclipse.rdf4j.server;

import org.eclipse.rdf4j.common.app.AppConfiguration;
import org.eclipse.rdf4j.repository.manager.LocalRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.lang.NonNull;

import java.util.Objects;

@Configuration
public class ServiceConfiguration {
    @Bean(name = "commonAppConfig", initMethod = "init", destroyMethod = "destroy")
    public AppConfiguration commonAppConfig() {
        final AppConfiguration config = new AppConfiguration();
        config.setApplicationId("Server");
        config.setLongName("RDF4J Server");

        return config;
    }

    @Bean(name = "rdf4jRepositoryManager", initMethod = "init", destroyMethod = "shutDown")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    @Autowired
    public RepositoryManager rdf4jRepositoryManager(@NonNull final AppConfiguration appConfig) {
        Objects.requireNonNull(appConfig, "Application config was not properly initialized!");

        return new LocalRepositoryManager(appConfig.getDataDir());
    }
}
