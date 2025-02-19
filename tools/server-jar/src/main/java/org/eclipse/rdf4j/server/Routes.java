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
import org.springframework.beans.factory.annotation.Qualifier;
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
    @Bean(name = "rdf4jProtocolUrlMapping")
    public HandlerMapping rdf4jProtocolUrlMapping(@NonNull @Autowired @Qualifier("rdf4jProtocolController") final ProtocolController protocolController) {
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
    @Bean(name = "rdf4jRepositoryListUrlMapping")
    public HandlerMapping rdf4jRepositoryListUrlMapping(@NonNull @Autowired @Qualifier("rdf4jRepositoryListController") final RepositoryListController repositoryListController) {
        final Map<String, Object> urlMap = new LinkedHashMap<>();

        urlMap.put("/repositories", repositoryListController);

        final SimpleUrlHandlerMapping mappings = new SimpleUrlHandlerMapping();
        mappings.setUrlMap(urlMap);
        mappings.setOrder(1);
        mappings.setAlwaysUseFullPath(true);

        return mappings;
    }

    @NonNull
    @Bean(name = "rdf4jRepositoryUrlMapping")
    public HandlerMapping rdf4jRepositoryUrlMapping(
            @NonNull @Autowired @Qualifier("rdf4jRepositoryController") final RepositoryController repositoryController,
            @NonNull @Autowired @Qualifier("rdf4jRepositoryConfigController") final ConfigController repositoryConfigController,
            @NonNull @Autowired @Qualifier("rdf4jRepositoryContextsController") final ContextsController contextsController,
            @NonNull @Autowired @Qualifier("rdf4jRepositoryNamespacesController") final NamespacesController namespacesController,
            @NonNull @Autowired @Qualifier("rdf4jRepositoryNamespaceController") final NamespaceController namespaceController,
            @NonNull @Autowired @Qualifier("rdf4jRepositorySizeController") final SizeController sizeController,
            @NonNull @Autowired @Qualifier("rdf4jRepositoryStatementsController") final StatementsController statementsController,
            @NonNull @Autowired @Qualifier("rdf4jRepositoryGraphController") final GraphController graphController,
            @NonNull @Autowired @Qualifier("rdf4jRepositoryTransactionController") final TransactionController transactionController,
            @NonNull @Autowired @Qualifier("rdf4jRepositoryTransactionStartController") final TransactionStartController transactionStartController,
            @NonNull @Autowired @Qualifier("rdf4jRepositoryInterceptor") final RepositoryInterceptor repositoryInterceptor
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
