# orders

Order management microservice for the microservices dissertation project. Built with Java 21 / Quarkus 3.18.3, it exposes a REST API for creating, updating, querying and deleting customer orders. It calls the `catalog` service (via a MicroProfile REST client, `catalog-api`) to resolve product name/price/description when an order is created, and — like `catalog` — fires audit log entries at the `audit` service for create/update/delete/read operations. Both outbound calls are synchronous REST with Keycloak bearer-token propagation, and both are designed to degrade gracefully rather than fail the primary request. Listens on port **8090**.

## Tech stack

Confirmed from `pom.xml`:

- Java 21, Quarkus 3.18.3 (`quarkus-bom`)
- `quarkus-rest` (JAX-RS, imperative — not reactive)
- `quarkus-hibernate-orm` + `quarkus-hibernate-orm-panache` (Panache active-record) + `quarkus-hibernate-orm-rest-data-panache`
- `quarkus-jdbc-postgresql`
- `quarkus-hibernate-validator` (bean validation on DTOs/entities)
- `quarkus-rest-client` + `quarkus-rest-client-jackson` (MicroProfile REST Client, used for both the catalog and audit outbound clients)
- `quarkus-oidc` (Keycloak authentication)
- `quarkus-jackson` + `jackson-datatype-jsr310`
- `quarkus-smallrye-openapi` (OpenAPI/Swagger UI)
- `quarkus-smallrye-health`
- `quarkus-opentelemetry`
- `quarkus-container-image-docker`
- `quarkus-arc`
- Test scope: `quarkus-junit5`, `quarkus-junit5-mockito`, `quarkus-test-security`, `rest-assured`, `testcontainers` (`postgresql` module)

**Auth drift confirmed**: unlike `catalog` and `audit`, this `pom.xml` does **not** declare `quarkus-keycloak-authorization`, even though it configures `quarkus-oidc` the same way (auth-server-url, client-id, token propagation). This is documented, known drift — not something this README changes.

## Local development

```shell
./mvnw quarkus:dev
```

Starts dev mode with hot reload and the Dev UI at `http://localhost:8090/q/dev/`.

- **Database**: PostgreSQL is expected at `localhost:5433` (see `quarkus.datasource.jdbc.url` in `application.properties`), overridable via `DB_HOST`, `DB_PORT`, `DB_NAME` (defaults to `microservices1691716`), `DB_USERNAME` (default `postgres`) and `DB_PASSWORD` (default `oRncHiOovwJAVOXK` — an accepted lab-scope fallback, not a secret to rotate from here).
- **Schema generation**: `quarkus.hibernate-orm.database.generation` is set to `${HIBERNATE_GENERATION:update}` — the default is `update`, not `drop-and-create`. This differs from the workspace-wide description that assumes `drop-and-create` for all three services; verify the effective value if you rely on schema-reset-per-restart behavior. There is also no `import.sql` in this repo (`quarkus.hibernate-orm.sql-load-script` defaults to `no-file`), so no seed data is loaded automatically.
- **Downstream services**: `catalog` (`CATALOG_SERVICE_URL`, default `http://localhost:8088`) and `audit` (`AUDIT_SERVICE_URL`, default `http://localhost:8089`) should be running for full functionality, but neither is strictly required — see the resilience pattern below.
- **Auth**: `quarkus-oidc` points at the shared `microservices` Keycloak realm (`KEYCLOAK_URL`, `KEYCLOAK_CLIENT_ID` default `orders-service`, `KEYCLOAK_CLIENT_SECRET`). All endpoints under `/*` require an authenticated principal except `/health/*`, which is open to `GET`.

## API docs

- Swagger UI: `/swagger-ui`
- OpenAPI document: `/openapi`
- Liveness: `/health/live`
- Readiness: `/health/ready`

### Endpoints (`OrderController`, base path `/api/orders`)

- `GET /api/orders` — paginated list of all orders
- `GET /api/orders/{id}` — get one order
- `POST /api/orders` — create an order (`OrderCreateDTO`, validated)
- `PUT /api/orders/{id}` — update an order (`OrderUpdateDTO`)
- `DELETE /api/orders/{id}` — delete an order
- `GET /api/orders/customer/{customerId}` — paginated orders for a customer
- `GET /api/orders/status/{status}` — paginated orders by `OrderStatus`
- `GET /api/orders/date-range?startDate&endDate` — paginated orders in a date range
- `PATCH /api/orders/{id}/status/{status}` — update just the status

### `Order` entity

Panache entity (`entities/Order.java`, table `orders`): `customerId` (required), `orderDate` (required), `totalAmount` (recalculated from items), `status` (`OrderStatus` enum: `CREATED`, `PAYMENT_PENDING`, `PAID`, `SHIPPED`, `DELIVERED`, `CANCELLED`, `RETURNED`, `REFUNDED`), and a `List<OrderItem>` (cascade all, orphan removal, eager fetch). `OrderItem` (table `order_items`) holds `productId`, `productName`, `price`, `quantity`, and a `getSubtotal()` helper, plus a `@ManyToOne` back-reference to its `Order`.

## Testing

```shell
./mvnw test
```

Real test suite found under `src/test/java/cloud/microservices/orders/`:

- `PostgresTestResource.java` — a `QuarkusTestResourceLifecycleManager` that spins up a Testcontainers `postgres:16-alpine` container, points the datasource at it, forces `drop-and-create` schema generation for tests, and — notably — points both `catalog-api` and `audit-service` REST client URLs at `http://localhost:1` (an unreachable address), so tests exercise the real resilience/fallback paths rather than depending on live catalog/audit instances.
- `repositories/OrderRepositoryTest.java` — `@QuarkusTest` + `@TestTransaction`, verifies `OrderRepository` query methods (`countByCustomerId`, `countByStatus`, `countByOrderDateBetween`).
- `controllers/OrderControllerTest.java` — `@QuarkusTest`, uses `@InjectMock` to stub `ProductService`/`AuditService` directly (rather than WireMock) and `@TestSecurity` to simulate an authenticated user; covers create/read/update-status/delete flow, a 401 for anonymous requests, and a 400 for invalid payloads.
- `controllers/OrderControllerIT.java` — `@QuarkusIntegrationTest` smoke test against the packaged artifact, checking `/health/ready` returns 200.

Testcontainers requires a working Docker daemon to run these tests locally.

## Build

```shell
./mvnw package                                                          # JVM build -> target/quarkus-app/quarkus-run.jar
./mvnw clean package -Dnative -Dquarkus.native.container-build=true     # native build via container (what CI runs)
```

## CI/CD

CI runs on GitHub Actions (`.github/workflows/ci.yml`; migrated from GitLab
CI, see `Sources/plans/2026-07-08-gitlab-to-github-migration.md`):

1. **test** — this repo's own job runs `./mvnw test` (JUnit report published as a workflow artifact).
2. **security-scan-gate** — calls the reusable workflow in `devdanielgherasim/micro-market-utilities`: CodeQL (HIGH/CRITICAL severity gate), gitleaks, dependency-review.
3. **build-and-push-native** — logs into the cloud registry via the shared `cloud-registry-login` composite action (OIDC), runs `./build.sh` (native image build + push), then resolves the pushed image reference/digest via `resolve-image-ref`.
4. **image-supply-chain** — calls the reusable workflow in `utilities`: Trivy image scan (CRITICAL gate), Syft SBOM, cosign keyless sign + verify, then a `repository_dispatch` trigger into the `deployment` repo's promotion workflow.

`build.sh` is multi-cloud aware (`CLOUD_PROVIDER` = `aws` (default) / `azure` / `gcp`), resolving/logging into the right container registry (ECR, ACR, or Artifact Registry) before running `mvn ... clean package -Dnative -Dquarkus.container-image.push=true ...`. For Azure it needs `ARM_CLIENT_ID`/`ARM_CLIENT_SECRET` (or OIDC federation); for AWS, `AWS_ACCOUNT_ID`/`AWS_REGION`; for GCP, workload-identity federation vars. `CONTAINER_REGISTRY_NAME`, `CI_COMMIT_SHA`, `CI_PROJECT_NAME`, and `PROJECT_NAMESPACE` (default `danielgherasim-microservices`) are used across all providers.

## Auth

`quarkus-oidc` authenticates against the shared `microservices` Keycloak realm (client id `orders-service` by default), with `quarkus.oidc.token-propagation.enabled=true` and per-client `token-propagation=true` for both the `catalog-api` and `audit-service` REST clients so downstream calls carry the caller's token.

**Known, documented inconsistency**: `orders` does not declare the `quarkus-keycloak-authorization` dependency that both `catalog` and `audit` have, despite configuring `quarkus-oidc` identically. This looks like drift rather than an intentional difference — verified still true as of this README (checked `pom.xml`); not something to silently "fix" here.

## Resilience pattern

`ProductService.getProductInfo()` wraps the call to `ProductClient` (the catalog REST client) in a try/catch: if catalog is unreachable or errors, it logs a warning and returns a synthetic placeholder `ProductInfo` (`"Product " + id"`, price `0.00`, `available=true`) instead of failing the order creation. `getProductName`/`getProductPrice` have their own fallback defaults on top of that. This is a deliberate design choice — order creation should not hard-fail just because catalog is down.

Symmetrically, `AuditService.logAction()` wraps the call to `AuditServiceClient` in try/catch, logging (but never throwing) on `ProcessingException` (network/connection failure) or any other error, and logging specific warnings for non-2xx statuses (401/403/other). Audit logging is fire-and-forget and must never block or fail the calling operation.

## Known inconsistencies

- **Auth**: missing `quarkus-keycloak-authorization` vs. `catalog`/`audit` (see above) — documented drift, not fixed here.
- **DB password fallback**: `application.properties` hardcodes `${DB_PASSWORD:oRncHiOovwJAVOXK}` as a default. Treated as an already-known, lab-scope default per the workspace conventions — not something this README recommends rotating, but flag before touching it elsewhere.
- **Schema generation default**: this service's local/default `quarkus.hibernate-orm.database.generation` is `update`, not the `drop-and-create` assumed elsewhere in the workspace docs for "restarting a service in dev mode wipes its schema" — worth double-checking if you rely on that behavior here specifically.
