package cloud.microservices.orders.services;

import cloud.microservices.orders.dtos.OrderCreateDTO;
import cloud.microservices.orders.dtos.OrderDTO;
import cloud.microservices.orders.dtos.OrderUpdateDTO;
import cloud.microservices.orders.entities.Order;
import cloud.microservices.orders.entities.OrderStatus;
import cloud.microservices.orders.mappers.OrderMapper;
import cloud.microservices.orders.repositories.OrderRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for Order entity providing business logic.
 */
@ApplicationScoped
public class OrderService {

    @Inject
    OrderRepository orderRepository;

    @Inject
    OrderMapper orderMapper;

    @Inject
    AuditService auditService;

    /**
     * Get all orders.
     *
     * @return list of all orders
     */
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get order by ID.
     *
     * @param id the order ID
     * @return the order DTO
     * @throws NotFoundException if order not found
     */
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id);
        if (order == null) {
            throw new NotFoundException("Order with ID " + id + " not found");
        }

        // Log the read operation
        auditService.logRead("Order", id.toString(), "Viewed order for customer: " + order.getCustomerId());

        return orderMapper.toDTO(order);
    }

    /**
     * Create a new order.
     *
     * @param orderCreateDTO the order data
     * @return the created order DTO
     */
    @Transactional
    public OrderDTO createOrder(OrderCreateDTO orderCreateDTO) {
        Order order = orderMapper.fromCreateDTO(orderCreateDTO);
        orderRepository.persist(order);

        auditService.logCreate("Order", order.id.toString(),
                "Created new order for customer: " + order.getCustomerId() + ", Total: " + order.getTotalAmount());

        return orderMapper.toDTO(order);
    }

    /**
     * Update an existing order.
     *
     * @param id             the order ID
     * @param orderUpdateDTO the order data to update
     * @return the updated order DTO
     * @throws NotFoundException if order not found
     */
    @Transactional
    public OrderDTO updateOrder(Long id, OrderUpdateDTO orderUpdateDTO) {
        Order order = orderRepository.findById(id);
        if (order == null) {
            throw new NotFoundException("Order with ID " + id + " not found");
        }

        String originalStatus = order.getStatus().toString();
        String originalCustomerId = order.getCustomerId();

        orderMapper.fromUpdateDTO(orderUpdateDTO, order);
        orderRepository.persist(order);

        String details = "Updated order for customer: " + originalCustomerId;
        if (orderUpdateDTO.getStatus() != null && !orderUpdateDTO.getStatus().toString().equals(originalStatus)) {
            details += " - Status changed from: " + originalStatus + " to: " + order.getStatus();
        }
        if (orderUpdateDTO.getCustomerId() != null && !orderUpdateDTO.getCustomerId().equals(originalCustomerId)) {
            details += " - Customer changed from: " + originalCustomerId + " to: " + order.getCustomerId();
        }

        auditService.logUpdate("Order", id.toString(), details);

        return orderMapper.toDTO(order);
    }

    /**
     * Delete an order.
     *
     * @param id the order ID
     * @throws NotFoundException if order not found
     */
    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id);
        if (order == null) {
            throw new NotFoundException("Order with ID " + id + " not found");
        }

        // Store order details for audit log
        String customerId = order.getCustomerId();

        orderRepository.delete(order);

        // Log the delete operation
        auditService.logDelete("Order", id.toString(), "Deleted order for customer: " + customerId);
    }

    /**
     * Find orders by customer ID.
     *
     * @param customerId the customer ID
     * @return list of orders for the customer
     */
    public List<OrderDTO> findByCustomerId(String customerId) {
        return orderRepository.findByCustomerId(customerId).stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Find orders by status.
     *
     * @param status the order status
     * @return list of orders with the specified status
     */
    public List<OrderDTO> findByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status).stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Find orders created between the specified dates.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @return list of orders created between the specified dates
     */
    public List<OrderDTO> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.findByOrderDateBetween(startDate, endDate).stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Find orders by payment method.
     *
     * @param paymentMethod the payment method
     * @return list of orders with the specified payment method
     */
    public List<OrderDTO> findByPaymentMethod(String paymentMethod) {
        return orderRepository.findByPaymentMethod(paymentMethod).stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update order status.
     *
     * @param id     the order ID
     * @param status the new status
     * @return the updated order DTO
     * @throws NotFoundException if order not found
     */
    @Transactional
    public OrderDTO updateOrderStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id);
        if (order == null) {
            throw new NotFoundException("Order with ID " + id + " not found");
        }

        String originalStatus = order.getStatus().toString();
        order.setStatus(status);
        orderRepository.persist(order);

        auditService.logUpdate("Order", id.toString(),
                "Updated order status from: " + originalStatus + " to: " + status);

        return orderMapper.toDTO(order);
    }
}