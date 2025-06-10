package cloud.microservices.orders.mappers;

import cloud.microservices.orders.dtos.*;
import cloud.microservices.orders.entities.Order;
import cloud.microservices.orders.entities.OrderItem;
import cloud.microservices.orders.entities.OrderStatus;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper class for converting between Order entity and DTOs.
 */
@ApplicationScoped
public class OrderMapper {

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
                .collect(Collectors.toList());
        
        return new OrderDTO(
                order.id,
                order.getCustomerId(),
                order.getOrderDate(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getShippingAddress(),
                order.getBillingAddress(),
                order.getPaymentMethod(),
                order.getPaymentId(),
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
        
        return new OrderItemDTO(
                orderItem.id,
                orderItem.getProductId(),
                orderItem.getProductName(),
                orderItem.getPrice(),
                orderItem.getQuantity(),
                orderItem.getSubtotal()
        );
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
        order.setShippingAddress(dto.getShippingAddress());
        order.setBillingAddress(dto.getBillingAddress() != null ? dto.getBillingAddress() : dto.getShippingAddress());
        order.setPaymentMethod(dto.getPaymentMethod());
        order.setPaymentId(dto.getPaymentId());
        
        // Create order items
        List<OrderItem> items = new ArrayList<>();
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
        item.setProductName(dto.getProductName());
        item.setPrice(dto.getPrice());
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
        
        if (dto.getShippingAddress() != null) {
            order.setShippingAddress(dto.getShippingAddress());
        }
        
        if (dto.getBillingAddress() != null) {
            order.setBillingAddress(dto.getBillingAddress());
        }
        
        if (dto.getPaymentMethod() != null) {
            order.setPaymentMethod(dto.getPaymentMethod());
        }
        
        if (dto.getPaymentId() != null) {
            order.setPaymentId(dto.getPaymentId());
        }
        
        // Update items if provided
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            // Remove existing items
            List<OrderItem> existingItems = new ArrayList<>(order.getItems());
            for (OrderItem item : existingItems) {
                order.removeItem(item);
            }
            
            // Add new items
            for (OrderItemCreateDTO itemDTO : dto.getItems()) {
                OrderItem item = fromOrderItemCreateDTO(itemDTO);
                order.addItem(item);
            }
        }
    }
}