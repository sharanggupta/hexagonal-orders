package dev.sharanggupta.orders.application;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import dev.sharanggupta.orders.domain.Order;
import dev.sharanggupta.orders.domain.OrderItem;

public final class CreateOrderService {

    public Order createOrder(UUID customerId, List<OrderItem> items) {
        return new Order(UUID.randomUUID(), customerId, Instant.now(), items);
    }
}
