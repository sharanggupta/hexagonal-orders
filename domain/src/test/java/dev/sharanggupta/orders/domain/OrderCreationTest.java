package dev.sharanggupta.orders.domain;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static dev.sharanggupta.orders.domain.OrderTestSupport.CREATED_AT;
import static dev.sharanggupta.orders.domain.OrderTestSupport.CUSTOMER_ID;
import static dev.sharanggupta.orders.domain.OrderTestSupport.ORDER_ID;
import static dev.sharanggupta.orders.domain.OrderTestSupport.item;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderCreationTest {

    @ParameterizedTest
    @ValueSource(strings = {"2020-01-01T00:00:00Z", "2026-09-21T12:34:56.123456789Z"})
    void cancellationPreservesExactCreationTime(String timestamp) {
        Instant createdAt = Instant.parse(timestamp);
        Order order = new Order(ORDER_ID, CUSTOMER_ID, createdAt,
                List.of(item(1, "1")));

        assertEquals(createdAt, order.createdAt());

        order.cancel();

        assertEquals(createdAt, order.createdAt());
    }

    @Test
    void rejectsMissingId() {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> new Order(null, CUSTOMER_ID, CREATED_AT, List.of(item(1, "1"))));

        assertEquals("Order ID is required", exception.getMessage());
    }

    @Test
    void rejectsMissingCustomer() {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> new Order(ORDER_ID, null, CREATED_AT, List.of(item(1, "1"))));

        assertEquals("Customer ID is required", exception.getMessage());
    }

    @Test
    void rejectsMissingCreationTime() {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> new Order(ORDER_ID, CUSTOMER_ID, null, List.of(item(1, "1"))));

        assertEquals("Creation timestamp is required", exception.getMessage());
    }
}
