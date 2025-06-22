package cloud.microservices.orders.repositories;

import cloud.microservices.orders.entities.Order;
import cloud.microservices.orders.entities.OrderStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for Order entity providing database operations.
 */
@ApplicationScoped
public class OrderRepository implements PanacheRepository<Order> {

    /**
     * Find orders by customer ID.
     *
     * @param customerId the customer ID to search for
     * @return list of orders for the specified customer
     */
    public List<Order> findByCustomerId(String customerId) {
        return list("customerId", customerId);
    }

    /**
     * Find orders by status.
     *
     * @param status the status to search for
     * @return list of orders with the specified status
     */
    public List<Order> findByStatus(OrderStatus status) {
        return list("status", status);
    }

    /**
     * Find orders created between the specified dates.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @return list of orders created between the specified dates
     */
    public List<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return list("orderDate >= ?1 AND orderDate <= ?2", startDate, endDate);
    }
}