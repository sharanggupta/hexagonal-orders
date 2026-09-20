package com.example.orders.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderItemTest {

    @ParameterizedTest
    @ValueSource(ints = {0, -1, Integer.MIN_VALUE})
    void rejectsNonPositiveQuantity(int quantity) {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new OrderItem(quantity));

        assertEquals("Quantity must be greater than zero", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, Integer.MAX_VALUE})
    void preservesPositiveQuantity(int quantity) {
        OrderItem item = new OrderItem(quantity);

        assertEquals(quantity, item.quantity());
    }
}
