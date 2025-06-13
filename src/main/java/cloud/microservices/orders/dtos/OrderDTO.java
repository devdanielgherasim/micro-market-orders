package cloud.microservices.orders.dtos;

import cloud.microservices.orders.entities.OrderStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Data Transfer Object for Order entity.
 */
@JsonSerialize
@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.ANY, setterVisibility = Visibility.ANY)
public class OrderDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    private Long id;
    @JsonProperty("customerId")
    private String customerId;
    @JsonProperty("orderDate")
    private LocalDateTime orderDate;
    @JsonProperty("totalAmount")
    private BigDecimal totalAmount;
    @JsonProperty("status")
    private OrderStatus status;
    @JsonProperty("shippingAddress")
    private String shippingAddress;
    @JsonProperty("billingAddress")
    private String billingAddress;
    @JsonProperty("paymentMethod")
    private String paymentMethod;
    @JsonProperty("paymentId")
    private String paymentId;
    @JsonProperty("items")
    private List<OrderItemDTO> items;

    /**
     * Default constructor.
     */
    public OrderDTO() {
    }

    /**
     * Constructor with all fields.
     */
    public OrderDTO(Long id, String customerId, LocalDateTime orderDate, BigDecimal totalAmount, 
                   OrderStatus status, String shippingAddress, String billingAddress, 
                   String paymentMethod, String paymentId, List<OrderItemDTO> items) {
        this.id = id;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.shippingAddress = shippingAddress;
        this.billingAddress = billingAddress;
        this.paymentMethod = paymentMethod;
        this.paymentId = paymentId;
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
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

    public List<OrderItemDTO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDTO> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderDTO orderDTO = (OrderDTO) o;
        return Objects.equals(id, orderDTO.id) &&
               Objects.equals(customerId, orderDTO.customerId) &&
               Objects.equals(orderDate, orderDTO.orderDate) &&
               Objects.equals(totalAmount, orderDTO.totalAmount) &&
               status == orderDTO.status &&
               Objects.equals(shippingAddress, orderDTO.shippingAddress) &&
               Objects.equals(billingAddress, orderDTO.billingAddress) &&
               Objects.equals(paymentMethod, orderDTO.paymentMethod) &&
               Objects.equals(paymentId, orderDTO.paymentId) &&
               Objects.equals(items, orderDTO.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customerId, orderDate, totalAmount, status, 
                           shippingAddress, billingAddress, paymentMethod, paymentId, items);
    }

    @Override
    public String toString() {
        return "OrderDTO{" +
                "id=" + id +
                ", customerId='" + customerId + '\'' +
                ", orderDate=" + orderDate +
                ", totalAmount=" + totalAmount +
                ", status=" + status +
                ", shippingAddress='" + shippingAddress + '\'' +
                ", billingAddress='" + billingAddress + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", paymentId='" + paymentId + '\'' +
                ", items=" + items +
                '}';
    }
}
