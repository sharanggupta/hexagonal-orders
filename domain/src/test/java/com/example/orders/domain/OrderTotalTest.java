package com.example.orders.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static com.example.orders.domain.OrderTestSupport.CUSTOMER_ID;
import static com.example.orders.domain.OrderTestSupport.ORDER_ID;
import static com.example.orders.domain.OrderTestSupport.assertDecimalEquals;
import static com.example.orders.domain.OrderTestSupport.creation;
import static com.example.orders.domain.OrderTestSupport.item;
import static org.junit.jupiter.api.Assertions.assertEquals;

class OrderTotalTest {

    @ParameterizedTest(name = "{0}")
    @MethodSource("orderTotals")
    void calculatesTotalWithoutChangingItems(
            String scenario, List<OrderItem> items, String expectedTotal) {
        Order order = new Order(creation(ORDER_ID, CUSTOMER_ID), items);

        BigDecimal total = order.total();

        assertDecimalEquals(expectedTotal, total);
        assertEquals(items, order.items());
    }

    private static Stream<Arguments> orderTotals() {
        return Stream.of(
                Arguments.of("multiplies price by quantity exactly",
                        List.of(item(3, "0.10")), "0.30"),
                Arguments.of("sums multiple item totals",
                        List.of(item(2, "19.99"), item(3, "0.10")), "40.28"),
                Arguments.of("accepts zero prices with different scales",
                        List.of(item(2, "0"), item(3, "0.00")), "0"),
                Arguments.of("combines free and paid items",
                        List.of(item(5, "0"), item(2, "4.25")), "8.50"),
                Arguments.of("handles large quantities and prices without overflow",
                        List.of(item(Integer.MAX_VALUE, "100000000000000000000.00")),
                        "214748364700000000000000000000.00"),
                Arguments.of("preserves fractional cents without rounding",
                        List.of(item(3, "0.001")), "0.003"));
    }
}
