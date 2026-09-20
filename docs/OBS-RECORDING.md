# OBS recording route

1. Show `SPEC.md` and the clean initial repository.
2. Run `./mvnw test`.
3. Execute the architect prompt and capture the accepted plan.
4. Execute the developer prompt; accelerate long tool output.
5. Run the test suite and the application.
6. Record the four curl proofs from the README.
7. Execute the reviewer/SRE prompt and capture one reproduced risk or an honest no-finding result.
8. Rerun tests, build the image, start it and check health.

## Checkpoints

- Keep terminal font at least 18 px.
- Hide notifications and unrelated tabs.
- Never display tokens, internal URLs, client names or shell history.
- Keep a clean clone at `before-demo` and `after-demo` as the fallback.

If the live agent fails, show its saved output, switch to the validated `after-demo` state in a disposable clone, and continue with real commands.
