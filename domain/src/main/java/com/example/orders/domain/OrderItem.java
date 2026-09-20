package com.example.orders.domain;

public record OrderItem(int quantity) {

    public OrderItem {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
    }
}
