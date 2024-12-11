package org.eclipse.rdf4j;

import org.eclipse.rdf4j.http.client.SharedHttpClientSessionManager;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.query.resultio.sparqljson.SPARQLResultsJSONWriter;
import org.eclipse.rdf4j.repository.config.RepositoryConfig;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.eclipse.rdf4j.repository.sail.config.SailRepositoryConfig;
import org.eclipse.rdf4j.sail.memory.config.MemoryStoreConfig;

public class Main {
    private static final String ENDPOINT = "http://localhost:8080/";
    private static final ValueFactory vf = SimpleValueFactory.getInstance();

    public static void main(String[] args) throws Exception {
        final var subj = vf.createIRI("https://www.example.com/subj");
        final var pred = vf.createIRI("https://www.example.com/pred");
        final var obj = vf.createLiteral(25);
        final var ctx = vf.createIRI("https://www.example.com/ctx");

        final var manager = new SharedHttpClientSessionManager();
        try (final var session = manager.createRDF4JProtocolSession(ENDPOINT)) {
            final String repoName = "test-abc";
            try {
                session.deleteRepository(repoName);
            } catch (final Exception e) {
                // ...
            }

            System.out.println("Repos: ");
            session.getRepositoryList(new SPARQLResultsJSONWriter(System.out));

            session.createRepository(
                    new RepositoryConfig(
                            repoName,
                            new SailRepositoryConfig(new MemoryStoreConfig())
                    )
            );

            System.out.println("Repos: ");
            session.getRepositoryList(new SPARQLResultsJSONWriter(System.out));

            HTTPRepository httpRepository = new HTTPRepository(ENDPOINT + "repositories/" + repoName);
            try (final var conn = httpRepository.getConnection()) {
                conn.begin();
                conn.add(subj, pred, obj);
                conn.add(subj, pred, obj, ctx);
                conn.commit();

                System.out.println("Found Contexts: ");
                session.getContextIDs(new SPARQLResultsJSONWriter(System.out));

                System.out.println("Found Contexts: ");
                session.getNamespaces(new SPARQLResultsJSONWriter(System.out));

                System.out.println("Found Data: ");
                var query = "SELECT * WHERE {\n" +
                            "    GRAPH ?g {\n" +
                            "        ?s ?p ?o\n" +
                            "    }\n" +
                            "}\n";
                var preparedQuery = conn.prepareTupleQuery(query);
                preparedQuery.evaluate(new SPARQLResultsJSONWriter(System.out));
                query = "SELECT * WHERE {\n" +
                        "    ?s ?p ?o .\n" +
                        "}\n";
                preparedQuery = conn.prepareTupleQuery(query);
                preparedQuery.evaluate(new SPARQLResultsJSONWriter(System.out));

                conn.setNamespace("bar", "http://www.example.com/foo");
                System.out.println("Found namespace for 'bar:' :" + conn.getNamespace("bar"));

            } finally {
                httpRepository.shutDown();
            }
        } finally {
            manager.shutDown();
        }
    }
}