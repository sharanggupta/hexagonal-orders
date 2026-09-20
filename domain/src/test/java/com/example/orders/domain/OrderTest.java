package com.example.orders.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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

    @ParameterizedTest
    @MethodSource("orderTotals")
    void calculatesTotalWithoutChangingItems(List<OrderItem> items, String expectedTotal) {
        Order order = new Order(items);

        BigDecimal total = order.total();

        assertEquals(0, new BigDecimal(expectedTotal).compareTo(total),
                () -> "Expected total " + expectedTotal + " but was " + total);
        assertEquals(items, order.items());
    }

    private static Stream<Arguments> orderTotals() {
        return Stream.of(
                Arguments.of(
                        List.of(new OrderItem(3, new BigDecimal("0.10"))),
                        "0.30"),
                Arguments.of(
                        List.of(
                                new OrderItem(2, new BigDecimal("19.99")),
                                new OrderItem(3, new BigDecimal("0.10"))),
                        "40.28"),
                Arguments.of(
                        List.of(
                                new OrderItem(2, BigDecimal.ZERO),
                                new OrderItem(3, new BigDecimal("0.00"))),
                        "0"),
                Arguments.of(
                        List.of(
                                new OrderItem(5, BigDecimal.ZERO),
                                new OrderItem(2, new BigDecimal("4.25"))),
                        "8.50"),
                Arguments.of(
                        List.of(new OrderItem(
                                Integer.MAX_VALUE,
                                new BigDecimal("100000000000000000000.00"))),
                        "214748364700000000000000000000.00"),
                Arguments.of(
                        List.of(new OrderItem(3, new BigDecimal("0.001"))),
                        "0.003"));
    }

    private static OrderItem validItem() {
        return new OrderItem(1, BigDecimal.ONE);
    }
}
