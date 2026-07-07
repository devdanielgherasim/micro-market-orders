package cloud.microservices.orders.controllers;

import cloud.microservices.orders.PostgresTestResource;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusIntegrationTest
@QuarkusTestResource(value = PostgresTestResource.class, restrictToAnnotatedClass = true)
class OrderControllerIT {

    @Test
    void packagedApplicationServesReadinessProbe() {
        given()
                .when()
                .get("/health/ready")
                .then()
                .statusCode(200);
    }
}
