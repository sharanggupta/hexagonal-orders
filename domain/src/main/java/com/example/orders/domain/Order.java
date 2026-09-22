package com.example.orders.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class Order {
    private final UUID id;
    private final UUID customerId;
    private final Instant createdAt;
    private final List<OrderItem> items;
    private OrderStatus status = OrderStatus.CREATED;

    public Order(UUID id, UUID customerId, Instant createdAt, List<OrderItem> items) {
        this.id = Objects.requireNonNull(id, "Order ID is required");
        this.customerId = Objects.requireNonNull(customerId, "Customer ID is required");
        this.createdAt = Objects.requireNonNull(createdAt, "Creation timestamp is required");
        Objects.requireNonNull(items, "Order items are required");
        for (OrderItem item : items) {
            Objects.requireNonNull(item, "Order items must not contain null");
        }
        this.items = List.copyOf(items);
        if (this.items.isEmpty()) {
            throw new IllegalArgumentException("An order must contain at least one item");
        }
    }

    public UUID id() {
        return id;
    }

    public UUID customerId() {
        return customerId;
    }

    public Instant createdAt() {
        return createdAt;
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
