# Unveil — Testing Guidelines

---

## Scope

Tests live inside each module under `src/commonTest/`. Platform-specific tests
go under `src/androidTest/` or `src/iosTest/` only when the behaviour genuinely
diverges per platform.

---

## What to test

| Layer                    | What to verify                                                                        |
|--------------------------|---------------------------------------------------------------------------------------|
| `DrawerController`       | Navigation stack mutations (`navigateTo`, `navigateBack`, `resetToPluginList`)        |
| `UnveilRegistry`         | Plugin deduplication by `id`, registration order                                      |
| Plugin interfaces        | Contract compliance of adapter implementations (e.g. `LogSink`, `NetworkInterceptor`) |
| Gesture thresholds       | Commit threshold (0.4f), edge zone (40 dp) — logic only, not rendering                |

## What not to test

- Composable rendering or layout — use manual inspection or screenshot tests sparingly.
- Platform-specific OS APIs (gesture exclusion rects, `BackHandler`) — these are tested by the platform.
- Third-party adapters (Ktor, Kermit, Firebase) — test the interface contract, not the framework.

---

## Naming

```
[Subject]Test.kt
DrawerControllerTest.kt
UnveilRegistryTest.kt
KtorNetworkInterceptorTest.kt
```

---

## Fake/stub policy

- Prefer handwritten fakes to mocking libraries.
- Fakes live in `src/commonTest/` alongside the tests that use them.
- Name them `Fake[Interface]` (e.g. `FakeLogSink`, `FakeNetworkInterceptor`).
- Do not use mocking frameworks — they add binary dependencies and complicate KMP setup.
