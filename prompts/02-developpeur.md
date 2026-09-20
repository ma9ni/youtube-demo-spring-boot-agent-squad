# Agent 2 — développeur

Implement the accepted plan for `SPEC.md` in this repository.

Work in small steps. After each behavior, run the narrowest useful test; finish with `./mvnw test`. Do not add a dependency or endpoint outside the contract. Keep controllers thin, calculate totals server-side, enforce idempotency in the database, and return a stable error response.

Expected result: a runnable Spring Boot service and tests covering creation, replay, conflicting replay, invalid input, lookup and health. Verification: all tests pass and the README curl commands match the implementation.

Fallback: if a dependency or framework API is unavailable, report the exact failing command and choose the smallest compatible implementation.
