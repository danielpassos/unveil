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

## Test naming

Test names must be complete English sentences written in backtick notation.
A good name tells you exactly what the system does — without reading the test body.

### The rule

```
`<subject> <verb> <outcome> [when/after/from <condition>]`
```

- **Subject** — the class, method, or concept being exercised (not "test" or "it")
- **Verb** — active voice: *sets*, *returns*, *adds*, *removes*, *ignores*, *forwards*
- **Outcome** — the observable result
- **Condition** — the situation that makes this case distinct (when applicable)

### Examples

| Bad | Good |
|-----|------|
| `request_isCaptured` | `` `sending a GET request forwards it to the interceptor` `` |
| `startsEmpty` | `` `entries is empty before any requests are recorded` `` |
| `record_addsEntry` | `` `record adds the request as a new entry` `` |
| `configure called twice accumulates plugins` | `` `calling configure multiple times registers plugins from each call` `` |
| `complete_doesNothing_forUnknownId` | `` `complete is a no-op when the request id is not found` `` |
| `id_isNetwork` | `` `plugin id is network` `` |

### What makes a name bad

- **`snake_case`** — reads as code, not a sentence: `record_addsEntry`
- **No subject** — reader must infer what's being tested: `startsEmpty`, `isInFlight_whenResponseIsSet`
- **Vague verb** — "captures", "accumulates", "handles" say nothing specific
- **Truncated condition** — `_forUnknownId` is a suffix, not a clause: use "when the id is not found"
- **Restates the implementation** — "calls onRequestSent" describes the mock interaction, not the behaviour

### File naming

```
[Subject]Test.kt
DrawerControllerTest.kt
UnveilRegistryTest.kt
KtorNetworkInterceptorTest.kt
```

---

## Test structure

Follow Arrange / Act / Assert with a blank line between each phase.

```kotlin
@Test
fun `complete sets the response on the matching entry`() {
    store.record(buildRequest("req-1"))          // Arrange
    val response = NetworkResponse(200, ...)

    store.complete("req-1", response)            // Act

    assertEquals(response, store.entries[0].response)  // Assert
    assertNull(store.entries[0].error)
}
```

One behaviour per test. If a test needs a second `assertEquals` to confirm an unrelated property,
split it into two tests.

---

## Fakes over mocks

All test doubles are hand-written fakes — no mocking libraries. Keep fakes in the same
`commonTest` source set as the tests that use them, named `Fake<Type>`.

```kotlin
internal class FakeUnveilPlugin(
    override val id: String = "fake_plugin",
    override val title: String = "Fake Plugin",
    override val icon: UnveilIcon = UnveilIcon.Emoji("🔧")
) : UnveilPlugin {
    @Composable
    override fun Content(scope: UnveilPanelScope) = Unit
}
```

Fakes use sensible defaults so each test only overrides what it cares about.
Do not use mocking frameworks — they add binary dependencies and complicate KMP setup.

---

## Isolation

Tests that touch shared singletons (`Unveil`, `UnveilRegistry`) must reset state in both
`@BeforeTest` and `@AfterTest`. Tests that only exercise local objects (a fresh `NetworkStore`,
a fresh `DrawerController`) do not need lifecycle hooks.

---

## Coroutines

Wrap tests that call `suspend` functions or touch animated state in `runTest { }`.
Do not add `runTest` unless the code under test actually requires it.
