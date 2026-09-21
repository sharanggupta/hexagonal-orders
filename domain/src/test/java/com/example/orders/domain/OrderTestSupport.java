package com.example.orders.domain;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class OrderTestSupport {
    static final UUID ORDER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    private OrderTestSupport() {
    }

    static OrderItem item(int quantity, String unitPrice) {
        return new OrderItem(quantity, new BigDecimal(unitPrice));
    }

    static void assertDecimalEquals(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual),
                () -> "Expected decimal value " + expected + " but was " + actual);
    }
}
