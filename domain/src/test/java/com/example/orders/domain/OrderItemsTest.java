package com.example.orders.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.example.orders.domain.OrderTestSupport.ORDER_ID;
import static com.example.orders.domain.OrderTestSupport.item;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OrderItemsTest {

    @Test
    void rejectsEmptyItems() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Order(ORDER_ID, List.of()));

        assertEquals("An order must contain at least one item", exception.getMessage());
    }

    @Test
    void rejectsMissingItems() {
        NullPointerException exception = assertThrows(
                NullPointerException.class,
                () -> new Order(ORDER_ID, null));

        assertEquals("Order items are required", exception.getMessage());
    }

    @Test
    void rejectsNullItem() {
        List<OrderItem> items = Arrays.asList(item(1, "1"), null);

        assertThrows(NullPointerException.class, () -> new Order(ORDER_ID, items));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2})
    void preservesNonEmptyItems(int itemCount) {
        List<OrderItem> items = Collections.nCopies(itemCount, item(1, "1"));

        Order order = new Order(ORDER_ID, items);

        assertEquals(items, order.items());
    }

    @Test
    void changingOriginalListDoesNotChangeOrder() {
        OrderItem item = item(1, "1");
        List<OrderItem> items = new ArrayList<>(List.of(item));
        Order order = new Order(ORDER_ID, items);

        items.clear();

        assertEquals(List.of(item), order.items());
    }

    @Test
    void returnedItemsCannotBeChanged() {
        OrderItem item = item(1, "1");
        Order order = new Order(ORDER_ID, new ArrayList<>(List.of(item)));

        assertThrows(UnsupportedOperationException.class, () -> order.items().clear());

        assertEquals(List.of(item), order.items());
    }

}
