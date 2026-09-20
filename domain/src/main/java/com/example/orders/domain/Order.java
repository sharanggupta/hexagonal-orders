package com.example.orders.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public final class Order {
    private final List<OrderItem> items;

    public Order(List<OrderItem> items) {
        Objects.requireNonNull(items, "Order items are required");
        this.items = List.copyOf(items);
        if (this.items.isEmpty()) {
            throw new IllegalArgumentException("An order must contain at least one item");
        }
    }

    public BigDecimal total() {
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : items) {
            BigDecimal itemTotal = item.unitPrice().multiply(BigDecimal.valueOf(item.quantity()));
            total = total.add(itemTotal);
        }
        return total;
    }

    public List<OrderItem> items() {
        return items;
    }
}
