package com.example.orders.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.example.orders.domain.OrderTestSupport.CUSTOMER_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderIdentityTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "123e4567-e89b-42d3-a456-426614174000",
            "987e6543-e21b-43d3-b456-426614174001"
    })
    void preservesSuppliedIdAfterCancellation(String suppliedId) {
        UUID id = UUID.fromString(suppliedId);
        Order order = new Order(id, CUSTOMER_ID, List.of(new OrderItem(1, BigDecimal.ONE)));

        assertEquals(id, order.id());

        order.cancel();

        assertEquals(id, order.id());
    }

    @Test
    void rejectsMissingId() {
        List<OrderItem> items = List.of(new OrderItem(1, BigDecimal.ONE));

        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new Order(null, CUSTOMER_ID, items));

        assertEquals("Order ID is required", exception.getMessage());
    }
}
