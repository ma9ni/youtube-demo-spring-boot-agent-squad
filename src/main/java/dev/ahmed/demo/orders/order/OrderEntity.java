package dev.ahmed.demo.orders.order;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders", uniqueConstraints = @UniqueConstraint(name = "uk_order_idempotency", columnNames = "idempotency_key"))
public class OrderEntity {

    @Id
    private String id;

    @Column(name = "idempotency_key", nullable = false, updatable = false)
    private String idempotencyKey;

    @Column(nullable = false, updatable = false)
    private String requestFingerprint;

    @Column(nullable = false, updatable = false)
    private String customerId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "order_items", joinColumns = @JoinColumn(name = "order_id"))
    private List<OrderItemEntity> items = new ArrayList<>();

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal total;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected OrderEntity() {
    }

    public OrderEntity(String id, String idempotencyKey, String requestFingerprint, String customerId,
                       List<OrderItemEntity> items, BigDecimal total, Instant createdAt) {
        this.id = id;
        this.idempotencyKey = idempotencyKey;
        this.requestFingerprint = requestFingerprint;
        this.customerId = customerId;
        this.items = new ArrayList<>(items);
        this.total = total;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public String getRequestFingerprint() { return requestFingerprint; }
    public String getCustomerId() { return customerId; }
    public List<OrderItemEntity> getItems() { return List.copyOf(items); }
    public BigDecimal getTotal() { return total; }
    public Instant getCreatedAt() { return createdAt; }
}

