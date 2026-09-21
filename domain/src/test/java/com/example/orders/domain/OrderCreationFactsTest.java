package com.example.orders.domain;

import org.junit.jupiter.api.Test;

import static com.example.orders.domain.OrderTestSupport.CREATED_AT;
import static com.example.orders.domain.OrderTestSupport.CUSTOMER_ID;
import static com.example.orders.domain.OrderTestSupport.ORDER_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderCreationFactsTest {

    @Test
    void rejectsMissingId() {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> new OrderCreationFacts(null, CUSTOMER_ID, CREATED_AT));

        assertEquals("Order ID is required", exception.getMessage());
    }

    @Test
    void rejectsMissingCustomer() {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> new OrderCreationFacts(ORDER_ID, null, CREATED_AT));

        assertEquals("Customer ID is required", exception.getMessage());
    }

    @Test
    void rejectsMissingCreationTime() {
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> new OrderCreationFacts(ORDER_ID, CUSTOMER_ID, null));

        assertEquals("Creation timestamp is required", exception.getMessage());
    }
}
