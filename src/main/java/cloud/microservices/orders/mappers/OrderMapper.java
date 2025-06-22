package cloud.microservices.orders.mappers;

import cloud.microservices.orders.clients.ProductClient;
import cloud.microservices.orders.dtos.*;
import cloud.microservices.orders.entities.Order;
import cloud.microservices.orders.entities.OrderItem;
import cloud.microservices.orders.entities.OrderStatus;
import cloud.microservices.orders.services.ProductService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapper class for converting between Order entity and DTOs.
 */
@ApplicationScoped
public class OrderMapper {

    @Inject
    ProductService productService;

    /**
     * Map Order entity to OrderDTO.
     *
     * @param order the order entity
     * @return the order DTO
     */
    public OrderDTO toDTO(Order order) {
        if (order == null) {
            return null;
        }

        List<OrderItemDTO> itemDTOs = order.getItems().stream()
                .map(this::toOrderItemDTO)
                .toList();

        return new OrderDTO(
                order.id,
                order.getCustomerId(),
                order.getOrderDate(),
                order.getTotalAmount(),
                order.getStatus(),
                itemDTOs
        );
    }

    /**
     * Map OrderItem entity to OrderItemDTO.
     *
     * @param orderItem the order item entity
     * @return the order item DTO
     */
    public OrderItemDTO toOrderItemDTO(OrderItem orderItem) {
        if (orderItem == null) {
            return null;
        }

        String productName = formatProductName(orderItem.getProductName());

        OrderItemDTO dto = new OrderItemDTO(
                orderItem.id,
                orderItem.getProductId(),
                productName,
                orderItem.getPrice(),
                orderItem.getQuantity(),
                null // We'll calculate this below
        );

        // Ensure subtotal is correctly calculated
        dto.calculateSubtotal();

        return dto;
    }

    /**
     * Format product name to handle default or empty values.
     *
     * @param productName the product name to format
     * @return formatted product name or product ID reference if default value
     */
    private String formatProductName(String productName) {
        if (productName == null || productName.isEmpty()) {
            return "Product";
        }

        if ("Unknown Product".equals(productName)) {
            return "Product";
        }

        return productName;
    }

    /**
     * Map OrderCreateDTO to Order entity.
     *
     * @param dto the order create DTO
     * @return the order entity
     */
    public Order fromCreateDTO(OrderCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        Order order = new Order();
        order.setCustomerId(dto.getCustomerId());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.CREATED);

        if (dto.getItems() != null) {
            for (OrderItemCreateDTO itemDTO : dto.getItems()) {
                OrderItem item = fromOrderItemCreateDTO(itemDTO);
                order.addItem(item);
            }
        }

        return order;
    }

    /**
     * Map OrderItemCreateDTO to OrderItem entity.
     * Retrieves product information (name and price) from the catalog service based on the product ID.
     *
     * @param dto the order item create DTO
     * @return the order item entity
     */
    public OrderItem fromOrderItemCreateDTO(OrderItemCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        OrderItem item = new OrderItem();
        item.setProductId(dto.getProductId());

        ProductClient.ProductInfo productInfo = productService.getProductInfo(dto.getProductId());
        String productName = productInfo.getName();
        BigDecimal price = productInfo.getPrice();

        item.setProductName(productName);
        item.setPrice(price);
        item.setQuantity(dto.getQuantity());

        return item;
    }

    /**
     * Map OrderUpdateDTO to Order entity.
     *
     * @param dto   the order update DTO
     * @param order the order entity to update
     */
    public void fromUpdateDTO(OrderUpdateDTO dto, Order order) {
        if (dto == null || order == null) {
            return;
        }

        if (dto.getCustomerId() != null) {
            order.setCustomerId(dto.getCustomerId());
        }

        if (dto.getStatus() != null) {
            order.setStatus(dto.getStatus());
        }

        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            List<OrderItem> existingItems = new ArrayList<>(order.getItems());
            for (OrderItem item : existingItems) {
                order.removeItem(item);
            }

            for (OrderItemCreateDTO itemDTO : dto.getItems()) {
                OrderItem item = fromOrderItemCreateDTO(itemDTO);
                order.addItem(item);
            }
        }
    }
}
