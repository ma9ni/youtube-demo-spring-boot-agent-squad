# Agent 1 — architecte

Read `SPEC.md` and the current repository. Do not modify any file.

Produce a concise implementation plan that defines:

1. domain boundaries and invariants;
2. endpoints and exact HTTP semantics;
3. the idempotency strategy, including concurrent requests;
4. the minimum persistence model;
5. tests that prove every visible behavior.

Constraints: keep one deployable service, use only existing dependencies, and call out anything the specification leaves ambiguous. Expected result: a plan the developer agent can implement without inventing requirements. Verification: every requirement in `SPEC.md` maps to at least one test.

Fallback: if the repository cannot be read, stop and list the missing paths instead of guessing.
