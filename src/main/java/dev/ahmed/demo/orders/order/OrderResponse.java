package dev.ahmed.demo.orders.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        String id,
        String customerId,
        List<Item> items,
        BigDecimal total,
        Instant createdAt
) {
    public record Item(String sku, int quantity, BigDecimal unitPrice) {
    }

    static OrderResponse from(OrderEntity order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getItems().stream()
                        .map(item -> new Item(item.getSku(), item.getQuantity(), item.getUnitPrice()))
                        .toList(),
                order.getTotal(),
                order.getCreatedAt());
    }
}

