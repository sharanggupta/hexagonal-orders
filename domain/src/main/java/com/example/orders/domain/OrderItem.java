package com.example.orders.domain;

import java.math.BigDecimal;
import java.util.Objects;

public record OrderItem(int quantity, BigDecimal unitPrice) {

    public OrderItem {
        Objects.requireNonNull(unitPrice, "Unit price is required");
        if (unitPrice.signum() < 0) {
            throw new IllegalArgumentException("Unit price must not be negative");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
    }
}
