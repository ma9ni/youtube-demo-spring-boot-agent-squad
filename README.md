# Spring Boot Agent Squad

A neutral, public demo for the French YouTube video **“J’ai généré un microservice Spring Boot avec 3 agents IA — puis je l’ai audité”**.

Three role prompts guide the work: architect, developer, and reviewer/SRE. The application itself is a small order API designed to make correctness visible through tests and curl commands.

## Stack

Java 17, Spring Boot 4.1.1, Maven, Spring MVC, Validation, JPA, H2, Actuator, JUnit/MockMvc and Docker.

## Run and validate

```bash
./mvnw test
./mvnw spring-boot:run
```

In another terminal:

```bash
curl -i -X POST http://localhost:8080/api/orders \
  -H 'Content-Type: application/json' \
  -H 'Idempotency-Key: youtube-demo-001' \
  -d '{"customerId":"customer-demo-42","items":[{"sku":"JAVA-MUG","quantity":2,"unitPrice":12.50}]}'
```

Repeat the same command: the first response is `201`, the replay is `200`, and only one order exists. Reuse the key with a different quantity to get `409`.

Invalid input:

```bash
curl -i -X POST http://localhost:8080/api/orders \
  -H 'Content-Type: application/json' \
  -H 'Idempotency-Key: youtube-demo-invalid' \
  -d '{"customerId":"customer-demo-42","items":[{"sku":"JAVA-MUG","quantity":0,"unitPrice":12.50}]}'
```

Health:

```bash
curl -s http://localhost:8080/actuator/health
```

## Container

```bash
docker build -t spring-boot-agent-squad .
docker run --rm -p 8080:8080 spring-boot-agent-squad
```

## Demo states

- `before-demo`: specification and prompts, before the implementation.
- `after-demo`: validated application.

For recording order and fallback instructions, see [`docs/OBS-RECORDING.md`](docs/OBS-RECORDING.md).

## Limits

The in-process synchronization makes same-key calls deterministic in this single-instance demo, while the database uniqueness constraint prevents duplicate persisted keys. The in-process lock does **not** coordinate multiple replicas. A production multi-pod design needs a database-native claim/insert strategy and a tested conflict-recovery path. Authentication, PostgreSQL, distributed tracing, load testing and Kubernetes are deliberately out of scope.

## License

MIT.
