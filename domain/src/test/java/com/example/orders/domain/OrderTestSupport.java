package com.example.orders.domain;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

final class OrderTestSupport {

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
