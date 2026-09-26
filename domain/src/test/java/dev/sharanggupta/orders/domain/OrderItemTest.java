package dev.sharanggupta.orders.domain;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
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
                () -> new OrderItem(quantity, BigDecimal.ONE));

        assertEquals("Quantity must be greater than zero", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, Integer.MAX_VALUE})
    void preservesPositiveQuantity(int quantity) {
        OrderItem item = new OrderItem(quantity, BigDecimal.ONE);

        assertEquals(quantity, item.quantity());
    }

    @ParameterizedTest
    @ValueSource(strings = {"-0.01", "-1", "-100.50"})
    void rejectsNegativeUnitPrice(String price) {
        BigDecimal unitPrice = new BigDecimal(price);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new OrderItem(1, unitPrice));

        assertEquals("Unit price must not be negative", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "0.00", "0.01", "19.99", "100000000000000000000.00"})
    void preservesNonNegativeUnitPrice(String price) {
        BigDecimal unitPrice = new BigDecimal(price);

        OrderItem item = new OrderItem(1, unitPrice);

        assertEquals(unitPrice, item.unitPrice());
    }

    @Test
    void rejectsMissingUnitPrice() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new OrderItem(1, null));

        assertEquals("Unit price is required", exception.getMessage());
    }
}
