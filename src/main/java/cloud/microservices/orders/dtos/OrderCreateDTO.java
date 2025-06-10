package cloud.microservices.orders.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.Objects;

/**
 * Data Transfer Object for creating a new Order.
 */
public class OrderCreateDTO {
    @NotBlank(message = "Customer ID is required")
    private String customerId;

    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;

    private String billingAddress;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    private String paymentId;

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<OrderItemCreateDTO> items;

    /**
     * Default constructor.
     */
    public OrderCreateDTO() {
    }

    /**
     * Constructor with all fields.
     */
    public OrderCreateDTO(String customerId, String shippingAddress, String billingAddress, 
                         String paymentMethod, String paymentId, List<OrderItemCreateDTO> items) {
        this.customerId = customerId;
        this.shippingAddress = shippingAddress;
        this.billingAddress = billingAddress;
        this.paymentMethod = paymentMethod;
        this.paymentId = paymentId;
        this.items = items;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public List<OrderItemCreateDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemCreateDTO> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderCreateDTO that = (OrderCreateDTO) o;
        return Objects.equals(customerId, that.customerId) &&
               Objects.equals(shippingAddress, that.shippingAddress) &&
               Objects.equals(billingAddress, that.billingAddress) &&
               Objects.equals(paymentMethod, that.paymentMethod) &&
               Objects.equals(paymentId, that.paymentId) &&
               Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, shippingAddress, billingAddress, paymentMethod, paymentId, items);
    }

    @Override
    public String toString() {
        return "OrderCreateDTO{" +
                "customerId='" + customerId + '\'' +
                ", shippingAddress='" + shippingAddress + '\'' +
                ", billingAddress='" + billingAddress + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", paymentId='" + paymentId + '\'' +
                ", items=" + items +
                '}';
    }
}
