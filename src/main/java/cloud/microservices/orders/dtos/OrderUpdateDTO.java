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

    @Valid
    private List<OrderItemCreateDTO> items;

    /**
     * Default constructor.
     */
    public OrderUpdateDTO() {
        // default constructor
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
                Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, status, items);
    }

    @Override
    public String toString() {
        return "OrderUpdateDTO{" +
                "customerId='" + customerId + '\'' +
                ", status=" + status +
                ", items=" + items +
                '}';
    }
}
