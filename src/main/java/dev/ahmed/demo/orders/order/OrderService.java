package dev.ahmed.demo.orders.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class OrderService {

    private final OrderRepository repository;
    private final Clock clock = Clock.systemUTC();

    OrderService(OrderRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public synchronized CreationResult create(String idempotencyKey, CreateOrderRequest request) {
        String fingerprint = fingerprint(request);
        var existing = repository.findByIdempotencyKey(idempotencyKey);
        if (existing.isPresent()) {
            if (!existing.get().getRequestFingerprint().equals(fingerprint)) {
                throw new IdempotencyConflictException();
            }
            return new CreationResult(OrderResponse.from(existing.get()), false);
        }

        List<OrderItemEntity> items = request.items().stream()
                .map(item -> new OrderItemEntity(item.sku(), item.quantity(), item.unitPrice()))
                .toList();
        BigDecimal total = request.items().stream()
                .map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        var order = new OrderEntity(
                UUID.randomUUID().toString(), idempotencyKey, fingerprint,
                request.customerId(), items, total, Instant.now(clock));
        return new CreationResult(OrderResponse.from(repository.save(order)), true);
    }

    @Transactional(readOnly = true)
    OrderResponse get(String id) {
        return repository.findById(id)
                .map(OrderResponse::from)
                .orElseThrow(OrderNotFoundException::new);
    }

    private String fingerprint(CreateOrderRequest request) {
        String value = request.customerId() + "|" + request.items().stream()
                .map(item -> item.sku() + ":" + item.quantity() + ":" + item.unitPrice().stripTrailingZeros().toPlainString())
                .reduce((left, right) -> left + "|" + right)
                .orElse("");
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }

    record CreationResult(OrderResponse order, boolean created) {
    }
}

