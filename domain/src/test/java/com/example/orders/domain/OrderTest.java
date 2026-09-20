package com.example.orders.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderTest {

    @Test
    void rejectsEmptyItems() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Order(List.of()));

        assertEquals("An order must contain at least one item", exception.getMessage());
    }

    @Test
    void rejectsMissingItems() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new Order(null));

        assertEquals("Order items are required", exception.getMessage());
    }

    @Test
    void rejectsNullItem() {
        List<OrderItem> items = Arrays.asList(validItem(), null);

        assertThrows(NullPointerException.class, () -> new Order(items));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    void preservesNonEmptyItems(int itemCount) {
        List<OrderItem> items = Collections.nCopies(itemCount, validItem());

        Order order = new Order(items);

        assertEquals(items, order.items());
    }

    @Test
    void changingOriginalListDoesNotChangeOrder() {
        OrderItem item = validItem();
        List<OrderItem> items = new ArrayList<>(List.of(item));
        Order order = new Order(items);

        items.clear();

        assertEquals(List.of(item), order.items());
    }

    @Test
    void returnedItemsCannotBeChanged() {
        OrderItem item = validItem();
        Order order = new Order(new ArrayList<>(List.of(item)));

        assertThrows(UnsupportedOperationException.class, () -> order.items().clear());

        assertEquals(List.of(item), order.items());
    }

    private static OrderItem validItem() {
        return new OrderItem(1, BigDecimal.ONE);
    }
}
