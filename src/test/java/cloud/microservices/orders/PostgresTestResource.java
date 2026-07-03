package cloud.microservices.orders;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.HashMap;
import java.util.Map;

public class PostgresTestResource implements QuarkusTestResourceLifecycleManager {

    private PostgreSQLContainer<?> postgres;

    @Override
    public Map<String, String> start() {
        postgres = new PostgreSQLContainer<>("postgres:16-alpine");
        postgres.start();

        Map<String, String> config = new HashMap<>();
        config.put("quarkus.datasource.jdbc.url", postgres.getJdbcUrl());
        config.put("quarkus.datasource.username", postgres.getUsername());
        config.put("quarkus.datasource.password", postgres.getPassword());
        config.put("quarkus.hibernate-orm.database.generation", "drop-and-create");
        config.put("quarkus.hibernate-orm.sql-load-script", "no-file");
        config.put("quarkus.otel.enabled", "false");
        config.put("quarkus.rest-client.audit-service.url", "http://localhost:1");
        config.put("quarkus.rest-client.catalog-api.url", "http://localhost:1");
        return config;
    }

    @Override
    public void stop() {
        if (postgres != null) {
            postgres.stop();
        }
    }
}
