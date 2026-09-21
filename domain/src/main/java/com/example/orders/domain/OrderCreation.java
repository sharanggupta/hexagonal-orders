package com.example.orders.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record OrderCreation(UUID id, UUID customerId, Instant createdAt) {

    public OrderCreation {
        Objects.requireNonNull(id, "Order ID is required");
        Objects.requireNonNull(customerId, "Customer ID is required");
        Objects.requireNonNull(createdAt, "Creation timestamp is required");
    }
}
