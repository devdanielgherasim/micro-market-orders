package cloud.microservices.orders.services;

import cloud.microservices.orders.clients.ProductClient;
import cloud.microservices.orders.clients.ProductClient.ProductInfo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * Service for retrieving product information from the catalog service.
 * This service encapsulates the logic for interacting with the catalog service
 * and provides fallback mechanisms for when the service is unavailable.
 */
@ApplicationScoped
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Inject
    @RestClient
    ProductClient productClient;

    /**
     * Get product information by ID.
     * If the catalog service is unavailable, returns default product information.
     *
     * @param productId the product ID
     * @return the product information
     */
    public ProductInfo getProductInfo(Long productId) {
        try {
            logger.debug("Retrieving product information for product ID: {}", productId);
            return productClient.getProductById(productId);
        } catch (Exception e) {
            logger.warn("Error retrieving product information for product ID: {}. Using default values.", productId, e);
            return createDefaultProductInfo(productId);
        }
    }

    /**
     * Get product name by ID.
     * If the catalog service is unavailable, returns a default product name.
     *
     * @param productId the product ID
     * @return the product name
     */
    public String getProductName(Long productId) {
        try {
            ProductInfo productInfo = getProductInfo(productId);
            return productInfo.getName();
        } catch (Exception e) {
            logger.warn("Error retrieving product name for product ID: {}. Using default name.", productId, e);
            return "Product " + productId;
        }
    }

    /**
     * Get product price by ID.
     * If the catalog service is unavailable, returns a default price of 0.00.
     *
     * @param productId the product ID
     * @return the product price
     */
    public BigDecimal getProductPrice(Long productId) {
        try {
            ProductInfo productInfo = getProductInfo(productId);
            return productInfo.getPrice();
        } catch (Exception e) {
            logger.warn("Error retrieving product price for product ID: {}. Using default price.", productId, e);
            return BigDecimal.ZERO;
        }
    }

    /**
     * Create default product information for when the catalog service is unavailable.
     *
     * @param productId the product ID
     * @return default product information
     */
    private ProductInfo createDefaultProductInfo(Long productId) {
        ProductInfo productInfo = new ProductInfo();
        productInfo.setId(productId);
        productInfo.setName("Product " + productId);
        productInfo.setPrice(BigDecimal.ZERO);
        productInfo.setDescription("Default product description");
        productInfo.setAvailable(true);
        return productInfo;
    }
}