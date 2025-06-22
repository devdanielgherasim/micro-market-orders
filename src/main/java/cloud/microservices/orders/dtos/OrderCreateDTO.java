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
    public OrderCreateDTO(String customerId, List<OrderItemCreateDTO> items) {
        this.customerId = customerId;
        this.items = items;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
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
               Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, items);
    }

    @Override
    public String toString() {
        return "OrderCreateDTO{" +
                "customerId='" + customerId + '\'' +
                ", items=" + items +
                '}';
    }
}