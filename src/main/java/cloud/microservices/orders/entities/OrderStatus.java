package cloud.microservices.orders.entities;

/**
 * Enum representing the status of an order.
 */
public enum OrderStatus {
    /**
     * Order has been created but not yet processed.
     */
    CREATED,
    
    /**
     * Order has been processed and payment is pending.
     */
    PAYMENT_PENDING,
    
    /**
     * Payment has been received and order is being processed.
     */
    PAID,
    
    /**
     * Order has been shipped to the customer.
     */
    SHIPPED,
    
    /**
     * Order has been delivered to the customer.
     */
    DELIVERED,
    
    /**
     * Order has been cancelled.
     */
    CANCELLED,
    
    /**
     * Order has been returned by the customer.
     */
    RETURNED,
    
    /**
     * Order has been refunded.
     */
    REFUNDED
}