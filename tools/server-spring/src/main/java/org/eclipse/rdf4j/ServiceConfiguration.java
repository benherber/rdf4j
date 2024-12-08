package org.eclipse.rdf4j;

import java.util.List;
import java.util.Objects;

import org.eclipse.rdf4j.common.app.AppConfiguration;
import org.eclipse.rdf4j.common.webapp.navigation.NavigationModel;
import org.eclipse.rdf4j.http.server.repository.RepositoryInterceptor;
import org.eclipse.rdf4j.repository.manager.LocalRepositoryManager;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.lang.NonNull;
import org.springframework.web.context.WebApplicationContext;

@Configuration
public class ServiceConfiguration {

//	@Bean(name = "messageSource")
//	public MessageSource messageSource() {
//		final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
//		messageSource.setBasenames(
//				"org.eclipse.rdf4j.http.server.messages",
//				"org.eclipse.rdf4j.common.webapp.system.messages",
//				"org.eclipse.rdf4j.common.webapp.messages"
//		);
//
//		return messageSource;
//	}

	@Bean(name = "commonAppConfig", initMethod = "init", destroyMethod = "destroy")
	public AppConfiguration commonAppConfig() {
		final AppConfiguration config = new AppConfiguration();
		config.setApplicationId("Server");
		config.setLongName("RDF4J Server");

		return config;
	}

//	@Bean(name = "commonWebappNavigation")
//	public NavigationModel commonWebappNavigation() {
//		final NavigationModel navigationModel = new NavigationModel();
//		navigationModel.setNavigationModels(List.of(
//				"/org/eclipse/rdf4j/http/server/navigation.xml",
//				"/org/eclipse/rdf4j/common/webapp/system/navigation.xml",
//				"/org/eclipse/rdf4j/common/webapp/navigation.xml"
//		));
//
//		return navigationModel;
//	}

	@Bean(name = "rdf4jRepositoryManager", initMethod = "init", destroyMethod = "shutDown")
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	@Autowired
	public RepositoryManager rdf4jRepositoryManager(@NonNull final AppConfiguration appConfig) {
		Objects.requireNonNull(appConfig, "Application config was not properly initialized!");

		return new LocalRepositoryManager(appConfig.getDataDir());
	}

	@Bean(name = "rdf4jRepositoryInterceptor")
	@Scope(value = WebApplicationContext.SCOPE_REQUEST)
	@Autowired
	public RepositoryInterceptor rdf4jRepositoryInterceptor(final @NonNull RepositoryManager repositoryManager) {
		Objects.requireNonNull(repositoryManager, "Repository manager was not properly initialized!");

		final RepositoryInterceptor interceptor = new RepositoryInterceptor();
		interceptor.setRepositoryManager(repositoryManager);

		return interceptor;
	}
}
