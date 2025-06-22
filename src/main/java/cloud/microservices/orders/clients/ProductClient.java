package cloud.microservices.orders.clients;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.math.BigDecimal;

/**
 * Client interface for interacting with the catalog service to retrieve product information.
 * This interface defines the contract for how the orders microservice should interact with the catalog service.
 * 
 * Note: This is a placeholder interface. In a real implementation, you would need to:
 * 1. Configure the base URL for the catalog service in application.properties
 * 2. Implement proper error handling and retry logic
 * 3. Consider using circuit breakers for resilience
 */
@Path("/api/products")
@RegisterRestClient(configKey = "catalog-api")
@RegisterProvider(ProductServiceTokenRequestFilter.class)
public interface ProductClient {

    /**
     * Get product details by ID.
     *
     * @param id the product ID
     * @return the product details
     */
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    ProductInfo getProductById(@PathParam("id") Long id);

    /**
     * Data class for product information.
     */
    class ProductInfo {
        private Long id;
        private String name;
        private BigDecimal price;
        private String description;
        private boolean available;

        // Getters and setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isAvailable() {
            return available;
        }

        public void setAvailable(boolean available) {
            this.available = available;
        }
    }
}
