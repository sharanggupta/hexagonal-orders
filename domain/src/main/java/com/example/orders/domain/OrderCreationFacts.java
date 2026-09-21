package com.example.orders.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record OrderCreationFacts(UUID id, UUID customerId, Instant createdAt) {

    public OrderCreationFacts {
        Objects.requireNonNull(id, "Order ID is required");
        Objects.requireNonNull(customerId, "Customer ID is required");
        Objects.requireNonNull(createdAt, "Creation timestamp is required");
    }
}
