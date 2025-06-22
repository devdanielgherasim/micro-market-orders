package cloud.microservices.orders.repositories;

import cloud.microservices.orders.entities.Order;
import cloud.microservices.orders.entities.OrderStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Page;
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
     * Find orders by customer ID with pagination.
     *
     * @param customerId the customer ID to search for
     * @param page the page number (0-based)
     * @param size the page size
     * @return list of orders for the specified customer for the requested page
     */
    public List<Order> findByCustomerIdPaginated(String customerId, int page, int size) {
        return find("customerId", customerId)
                .page(Page.of(page, size))
                .list();
    }

    /**
     * Count orders by customer ID.
     *
     * @param customerId the customer ID to search for
     * @return the count of orders for the specified customer
     */
    public long countByCustomerId(String customerId) {
        return count("customerId", customerId);
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
     * Find orders by status with pagination.
     *
     * @param status the status to search for
     * @param page the page number (0-based)
     * @param size the page size
     * @return list of orders with the specified status for the requested page
     */
    public List<Order> findByStatusPaginated(OrderStatus status, int page, int size) {
        return find("status", status)
                .page(Page.of(page, size))
                .list();
    }

    /**
     * Count orders by status.
     *
     * @param status the status to search for
     * @return the count of orders with the specified status
     */
    public long countByStatus(OrderStatus status) {
        return count("status", status);
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

    /**
     * Find orders created between the specified dates with pagination.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @param page the page number (0-based)
     * @param size the page size
     * @return list of orders created between the specified dates for the requested page
     */
    public List<Order> findByOrderDateBetweenPaginated(LocalDateTime startDate, LocalDateTime endDate, int page, int size) {
        return find("orderDate >= ?1 AND orderDate <= ?2", startDate, endDate)
                .page(Page.of(page, size))
                .list();
    }

    /**
     * Count orders created between the specified dates.
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @return the count of orders created between the specified dates
     */
    public long countByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return count("orderDate >= ?1 AND orderDate <= ?2", startDate, endDate);
    }

    /**
     * Find all orders with pagination.
     *
     * @param page the page number (0-based)
     * @param size the page size
     * @return list of orders for the requested page
     */
    public List<Order> findAllPaginated(int page, int size) {
        return findAll().page(Page.of(page, size)).list();
    }
}
