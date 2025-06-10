package cloud.microservices.orders.dtos;

import cloud.microservices.orders.entities.OrderStatus;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Objects;

/**
 * Data Transfer Object for updating an existing Order.
 */
public class OrderUpdateDTO {
    private String customerId;

    private OrderStatus status;

    private String shippingAddress;

    private String billingAddress;

    private String paymentMethod;

    private String paymentId;

    @Valid
    private List<OrderItemCreateDTO> items;

    /**
     * Default constructor.
     */
    public OrderUpdateDTO() {
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
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
        OrderUpdateDTO that = (OrderUpdateDTO) o;
        return Objects.equals(customerId, that.customerId) &&
               status == that.status &&
               Objects.equals(shippingAddress, that.shippingAddress) &&
               Objects.equals(billingAddress, that.billingAddress) &&
               Objects.equals(paymentMethod, that.paymentMethod) &&
               Objects.equals(paymentId, that.paymentId) &&
               Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, status, shippingAddress, billingAddress, paymentMethod, paymentId, items);
    }

    @Override
    public String toString() {
        return "OrderUpdateDTO{" +
                "customerId='" + customerId + '\'' +
                ", status=" + status +
                ", shippingAddress='" + shippingAddress + '\'' +
                ", billingAddress='" + billingAddress + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", paymentId='" + paymentId + '\'' +
                ", items=" + items +
                '}';
    }
}
