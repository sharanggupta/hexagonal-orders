package dev.sharanggupta.orders.application;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import dev.sharanggupta.orders.domain.Order;
import dev.sharanggupta.orders.domain.OrderItem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateOrderServiceTest {
    private static final UUID CUSTOMER_ID = UUID.fromString("c1234567-e89b-42d3-a456-426614174000");

    @Test
    void createsAnOrderForTheCustomer() {
        CreateOrderService service = new CreateOrderService();
        List<OrderItem> items = List.of(new OrderItem(2, new BigDecimal("19.99")));

        Order order = service.createOrder(CUSTOMER_ID, items);

        assertEquals(CUSTOMER_ID, order.customerId());
        assertEquals(items, order.items());
    }
}
