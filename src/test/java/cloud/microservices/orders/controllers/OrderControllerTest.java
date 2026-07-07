package cloud.microservices.orders.controllers;

import cloud.microservices.orders.clients.ProductClient;
import cloud.microservices.orders.services.AuditService;
import cloud.microservices.orders.services.ProductService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@QuarkusTest
class OrderControllerTest {

    @InjectMock
    ProductService productService;

    @InjectMock
    AuditService auditService;

    @BeforeEach
    void setUpProductService() {
        ProductClient.ProductInfo product = new ProductClient.ProductInfo();
        product.setId(101L);
        product.setName("Compiler Pro");
        product.setDescription("Build toolchain");
        product.setPrice(new BigDecimal("25.00"));
        product.setAvailable(true);
        when(productService.getProductInfo(anyLong())).thenReturn(product);
    }

    @Test
    void rejectsAnonymousRequests() {
        given()
                .when()
                .get("/api/orders")
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "alice", roles = "user")
    void createsReadsUpdatesAndDeletesOrder() {
        Integer id = given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "customerId": "customer-1",
                          "items": [
                            {
                              "productId": 101,
                              "quantity": 2
                            }
                          ]
                        }
                        """)
                .when()
                .post("/api/orders")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("customerId", equalTo("customer-1"))
                .body("status", equalTo("CREATED"))
                .body("totalAmount", equalTo(50.00F))
                .extract()
                .path("id");

        given()
                .when()
                .get("/api/orders/{id}", id)
                .then()
                .statusCode(200)
                .body("items[0].productName", equalTo("Compiler Pro"));

        given()
                .when()
                .patch("/api/orders/{id}/status/{status}", id, "PAID")
                .then()
                .statusCode(200)
                .body("status", equalTo("PAID"));

        given()
                .when()
                .delete("/api/orders/{id}", id)
                .then()
                .statusCode(204);

        given()
                .when()
                .get("/api/orders/{id}", id)
                .then()
                .statusCode(404);
    }

    @Test
    @TestSecurity(user = "alice", roles = "user")
    void rejectsInvalidOrderPayload() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {
                          "customerId": "",
                          "items": []
                        }
                        """)
                .when()
                .post("/api/orders")
                .then()
                .statusCode(400);
    }
}
