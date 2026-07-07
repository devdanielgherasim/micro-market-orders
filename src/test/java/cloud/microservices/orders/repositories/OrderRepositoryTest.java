package cloud.microservices.orders.repositories;

import cloud.microservices.orders.entities.Order;
import cloud.microservices.orders.entities.OrderItem;
import cloud.microservices.orders.entities.OrderStatus;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
class OrderRepositoryTest {

    @Inject
    OrderRepository orderRepository;

    @Test
    @TestTransaction
    void findsOrdersByCustomerStatusAndDateRange() {
        Order order = new Order();
        order.setCustomerId("customer-1");
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.CREATED);
        order.addItem(new OrderItem(null, 101L, "Compiler Pro", new BigDecimal("25.00"), 2));
        orderRepository.persist(order);

        assertEquals(1, orderRepository.countByCustomerId("customer-1"));
        assertEquals(1, orderRepository.countByStatus(OrderStatus.CREATED));
        assertEquals(1, orderRepository.countByOrderDateBetween(
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now().plusHours(1)));
    }
}
