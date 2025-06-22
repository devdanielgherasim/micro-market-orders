package cloud.microservices.orders.dtos;

import cloud.microservices.orders.entities.OrderStatus;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.PositiveOrZero;

import java.io.Serial;
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
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    private Long id;

    @NotBlank(message = "Customer ID is required")
    @JsonProperty("customerId")
    private String customerId;

    @NotNull(message = "Order date is required")
    @PastOrPresent(message = "Order date cannot be in the future")
    @JsonProperty("orderDate")
    private LocalDateTime orderDate;

    @NotNull(message = "Total amount is required")
    @PositiveOrZero(message = "Total amount must be zero or positive")
    @JsonProperty("totalAmount")
    private BigDecimal totalAmount;

    @NotNull(message = "Status is required")
    @JsonProperty("status")
    private OrderStatus status;

    @Valid
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
                    OrderStatus status, List<OrderItemDTO> items) {
        this.id = id;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
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
                Objects.equals(items, orderDTO.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customerId, orderDate, totalAmount, status, items);
    }

    @Override
    public String toString() {
        return "OrderDTO{" +
                "id=" + id +
                ", customerId='" + customerId + '\'' +
                ", orderDate=" + orderDate +
                ", totalAmount=" + totalAmount +
                ", status=" + status +
                ", items=" + items +
                '}';
    }
}
