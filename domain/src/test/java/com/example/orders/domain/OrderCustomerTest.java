package com.example.orders.domain;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.example.orders.domain.OrderTestSupport.ORDER_ID;
import static com.example.orders.domain.OrderTestSupport.item;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderCustomerTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "c1234567-e89b-42d3-a456-426614174000",
            "d9876543-e21b-43d3-b456-426614174001"
    })
    void cancellationPreservesTheSuppliedCustomer(String suppliedCustomerId) {
        UUID customerId = UUID.fromString(suppliedCustomerId);
        Order order = new Order(ORDER_ID, customerId, List.of(item(1, "1")));

        assertEquals(customerId, order.customerId());
        assertEquals(ORDER_ID, order.id());

        order.cancel();

        assertEquals(customerId, order.customerId());
        assertEquals(ORDER_ID, order.id());
    }

    @Test
    void rejectsMissingCustomer() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new Order(ORDER_ID, null, List.of(item(1, "1"))));

        assertEquals("Customer ID is required", exception.getMessage());
    }
}
