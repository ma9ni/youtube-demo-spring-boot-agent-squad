package dev.ahmed.demo.orders.order;

import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
class OrderController {

    private final OrderService service;

    OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    ResponseEntity<OrderResponse> create(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody CreateOrderRequest request) {
        var result = service.create(idempotencyKey, request);
        if (result.created()) {
            return ResponseEntity.created(URI.create("/api/orders/" + result.order().id())).body(result.order());
        }
        return ResponseEntity.ok(result.order());
    }

    @GetMapping("/{id}")
    OrderResponse get(@PathVariable String id) {
        return service.get(id);
    }
}

