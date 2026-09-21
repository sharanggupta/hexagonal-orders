package com.example.orders.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class Order {
    private final OrderCreation creation;
    private final List<OrderItem> items;
    private OrderStatus status = OrderStatus.CREATED;

    public Order(OrderCreation creation, List<OrderItem> items) {
        this.creation = Objects.requireNonNull(creation, "Order creation is required");
        Objects.requireNonNull(items, "Order items are required");
        this.items = List.copyOf(items);
        if (this.items.isEmpty()) {
            throw new IllegalArgumentException("An order must contain at least one item");
        }
    }

    public Instant createdAt() {
        return creation.createdAt();
    }

    public UUID customerId() {
        return creation.customerId();
    }

    public UUID id() {
        return creation.id();
    }

    public OrderStatus status() {
        return status;
    }

    public void cancel() {
        if (status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order is already cancelled");
        }
        status = OrderStatus.CANCELLED;
    }

    public BigDecimal total() {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : items) {
            total = total.add(item.subtotal());
        }
        return total;
    }

    public List<OrderItem> items() {
        return items;
    }
}
