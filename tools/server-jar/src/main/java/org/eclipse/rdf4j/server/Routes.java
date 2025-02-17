package org.eclipse.rdf4j.server;

import org.eclipse.rdf4j.http.server.protocol.ProtocolController;
import org.eclipse.rdf4j.http.server.protocol.ProtocolInterceptor;
import org.eclipse.rdf4j.http.server.repository.RepositoryController;
import org.eclipse.rdf4j.http.server.repository.RepositoryInterceptor;
import org.eclipse.rdf4j.http.server.repository.RepositoryListController;
import org.eclipse.rdf4j.http.server.repository.config.ConfigController;
import org.eclipse.rdf4j.http.server.repository.contexts.ContextsController;
import org.eclipse.rdf4j.http.server.repository.graph.GraphController;
import org.eclipse.rdf4j.http.server.repository.namespaces.NamespaceController;
import org.eclipse.rdf4j.http.server.repository.namespaces.NamespacesController;
import org.eclipse.rdf4j.http.server.repository.size.SizeController;
import org.eclipse.rdf4j.http.server.repository.statements.StatementsController;
import org.eclipse.rdf4j.http.server.repository.transaction.TransactionController;
import org.eclipse.rdf4j.http.server.repository.transaction.TransactionStartController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.eclipse.rdf4j.http.server.repository.RepositoryInterceptor.REPOSITORY_KEY;

@Configuration
public class Routes {
    @NonNull
    @Autowired
    @Bean(name = "rdf4jProtocolUrlMapping")
    public HandlerMapping rdf4jProtocolUrlMapping(@NonNull final ProtocolController protocolController) {
        final Map<String, Object> urlMap = new LinkedHashMap<>();

        urlMap.put("/protocol", protocolController);

        final SimpleUrlHandlerMapping mappings = new SimpleUrlHandlerMapping();
        mappings.setUrlMap(urlMap);
        mappings.setOrder(0);
        mappings.setAlwaysUseFullPath(true);
        mappings.setInterceptors(new ProtocolInterceptor());

        return mappings;
    }

    @NonNull
    @Autowired
    @Bean(name = "rdf4jRepositoryListUrlMapping")
    public HandlerMapping rdf4jRepositoryListUrlMapping(@NonNull final RepositoryListController repositoryListController) {
        final Map<String, Object> urlMap = new LinkedHashMap<>();

        urlMap.put("/repositories", repositoryListController);

        final SimpleUrlHandlerMapping mappings = new SimpleUrlHandlerMapping();
        mappings.setUrlMap(urlMap);
        mappings.setOrder(1);
        mappings.setAlwaysUseFullPath(true);

        return mappings;
    }

    @NonNull
    @Autowired
    @Bean(name = "rdf4jRepositoryUrlMapping")
    public HandlerMapping rdf4jRepositoryUrlMapping(
            @NonNull final RepositoryInterceptor repositoryInterceptor,
            @NonNull final RepositoryController repositoryController,
            @NonNull final ConfigController repositoryConfigController,
            @NonNull final ContextsController contextsController,
            @NonNull final NamespacesController namespacesController,
            @NonNull final NamespaceController namespaceController,
            @NonNull final SizeController sizeController,
            @NonNull final StatementsController statementsController,
            @NonNull final GraphController graphController,
            @NonNull final TransactionController transactionController,
            @NonNull final TransactionStartController transactionStartController
    ) {
        final Map<String, Object> urlMap = new LinkedHashMap<>();

        final String repositoryPrefix = "/repositories/{" + REPOSITORY_KEY + "}";
        urlMap.put(repositoryPrefix + "/namespaces/{nsPrefix}", namespaceController);
        urlMap.put(repositoryPrefix + "/namespaces", namespacesController);
        urlMap.put(repositoryPrefix + "/config", repositoryConfigController);
        urlMap.put(repositoryPrefix + "/contexts", contextsController);
        urlMap.put(repositoryPrefix + "/statements", statementsController);
        urlMap.put(repositoryPrefix + "/rdf-graphs", contextsController);
        urlMap.put(repositoryPrefix + "/rdf-graphs/{graph}", graphController);
        urlMap.put(repositoryPrefix + "/size", sizeController);
        urlMap.put(repositoryPrefix + "/transactions", transactionStartController);
        urlMap.put(repositoryPrefix + "/transactions/{xid}", transactionController);
        urlMap.put(repositoryPrefix, repositoryController);

        final SimpleUrlHandlerMapping mappings = new SimpleUrlHandlerMapping();
        mappings.setUrlMap(urlMap);
        mappings.setOrder(2);
        mappings.setAlwaysUseFullPath(true);
        mappings.setInterceptors(repositoryInterceptor);

        return mappings;
    }
}
