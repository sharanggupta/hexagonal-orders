package com.example.orders.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class Order {
    private final OrderCreationFacts creationFacts;
    private final List<OrderItem> items;
    private OrderStatus status = OrderStatus.CREATED;

    public Order(OrderCreationFacts creationFacts, List<OrderItem> items) {
        this.creationFacts = Objects.requireNonNull(creationFacts, "Order creation is required");
        Objects.requireNonNull(items, "Order items are required");
        this.items = List.copyOf(items);
        if (this.items.isEmpty()) {
            throw new IllegalArgumentException("An order must contain at least one item");
        }
    }

    public UUID id() {
        return creationFacts.id();
    }

    public UUID customerId() {
        return creationFacts.customerId();
    }

    public Instant createdAt() {
        return creationFacts.createdAt();
    }

    public List<OrderItem> items() {
        return items;
    }

    public OrderStatus status() {
        return status;
    }

    public BigDecimal total() {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : items) {
            total = total.add(item.subtotal());
        }
        return total;
    }

    public void cancel() {
        if (status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order is already cancelled");
        }
        status = OrderStatus.CANCELLED;
    }
}
