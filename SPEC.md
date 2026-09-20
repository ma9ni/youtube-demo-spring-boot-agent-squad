# Order Service — demo contract

Build a small REST service for fictional orders.

## API

- `POST /api/orders` requires an `Idempotency-Key` header and returns `201 Created` for a new order.
- Replaying the same key with the same payload returns the existing order with `200 OK`.
- Reusing the same key with a different payload returns `409 Conflict`.
- `GET /api/orders/{id}` returns an order or `404 Not Found`.
- Invalid input returns `400 Bad Request` with a stable JSON error shape.
- `GET /actuator/health` remains available for the demo.

## Invariants

- `customerId`, `sku`, quantity and price are required.
- Quantity must be positive and price must be greater than zero.
- An order contains at least one item.
- The server calculates the total; clients never submit it.
- `Idempotency-Key` is unique at the database layer.

## Done means

`./mvnw test` passes, the curl walkthrough in `README.md` is reproducible, and no secret or real customer data is present.
