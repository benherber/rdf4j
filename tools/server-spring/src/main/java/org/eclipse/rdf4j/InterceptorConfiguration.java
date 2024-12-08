package org.eclipse.rdf4j;

import org.eclipse.rdf4j.http.server.ProtocolExceptionResolver;
import org.eclipse.rdf4j.http.server.repository.RepositoryInterceptor;
import org.eclipse.rdf4j.repository.manager.RepositoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer {

    private final RepositoryManager repositoryManager;

    @Autowired
    public InterceptorConfiguration(final RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    @Override
    public void addInterceptors(@NonNull final InterceptorRegistry registry) {
        final RepositoryInterceptor interceptor = new RepositoryInterceptor();
        interceptor.setRepositoryManager(repositoryManager);

        registry.addInterceptor(interceptor);
    }

    @Override
    public void configureHandlerExceptionResolvers(@NonNull final List<HandlerExceptionResolver> resolvers) {
        resolvers.add(new ProtocolExceptionResolver());
    }
}
