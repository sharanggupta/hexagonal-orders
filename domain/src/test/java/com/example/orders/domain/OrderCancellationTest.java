package com.example.orders.domain;

import java.util.List;

import org.junit.jupiter.api.Test;

import static com.example.orders.domain.OrderTestSupport.CREATED_AT;
import static com.example.orders.domain.OrderTestSupport.CUSTOMER_ID;
import static com.example.orders.domain.OrderTestSupport.ORDER_ID;
import static com.example.orders.domain.OrderTestSupport.assertDecimalEquals;
import static com.example.orders.domain.OrderTestSupport.item;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderCancellationTest {

    @Test
    void newOrderIsCreated() {
        Order order = new Order(ORDER_ID, CUSTOMER_ID, CREATED_AT, List.of(item(1, "1")));

        assertEquals(OrderStatus.CREATED, order.status());
    }

    @Test
    void cancellationChangesStatusAndPreservesItemsAndTotal() {
        List<OrderItem> items = pricedItems();
        Order order = new Order(ORDER_ID, CUSTOMER_ID, CREATED_AT, items);

        order.cancel();

        assertEquals(OrderStatus.CANCELLED, order.status());
        assertEquals(items, order.items());
        assertDecimalEquals("40.28", order.total());
    }

    @Test
    void rejectsCancellingAnAlreadyCancelledOrderWithoutChangingIt() {
        List<OrderItem> items = pricedItems();
        Order order = new Order(ORDER_ID, CUSTOMER_ID, CREATED_AT, items);
        order.cancel();

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                order::cancel);

        assertEquals("Order is already cancelled", exception.getMessage());
        assertEquals(OrderStatus.CANCELLED, order.status());
        assertEquals(items, order.items());
        assertDecimalEquals("40.28", order.total());
    }

    private static List<OrderItem> pricedItems() {
        return List.of(item(2, "19.99"), item(3, "0.10"));
    }
}
