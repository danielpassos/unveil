# Unveil — Linting & Code Style

Both tools run on every subproject automatically. Generated code in `/build/` is excluded.

---

## Tools

| Tool    | Plugin                          | Config                          |
|---------|---------------------------------|---------------------------------|
| ktlint  | `org.jlleitschuh.gradle.ktlint` | `.editorconfig` (project root)  |
| detekt  | `io.gitlab.arturbosch.detekt`   | `detekt.yml` (project root)     |

Both also include the **[nlopez/compose-rules](https://github.com/mrmans0n/compose-rules)** ruleset,
which enforces Compose-specific best practices (stable lambdas, remember usage, modifier ordering, etc.).

---

## Formatting rules (`.editorconfig`)

| Rule                   | Value                                                                                                 |
|------------------------|-------------------------------------------------------------------------------------------------------|
| Indent                 | 4 spaces (no tabs)                                                                                    |
| Max line length        | 120 characters                                                                                        |
| Trailing commas        | Disabled (on both declarations and call sites)                                                        |
| Continuation indent    | 4 spaces                                                                                              |
| Final newline          | Required                                                                                              |
| Trailing whitespace    | Trimmed                                                                                               |
| Function naming        | `ktlint_standard_function-naming` is disabled — Composable functions use PascalCase, which is correct |

---

## KDoc — enforced by detekt

The following detekt rules are **active**:

| Rule                          | Meaning                                                            |
|-------------------------------|--------------------------------------------------------------------|
| `UndocumentedPublicClass`     | Every `public` / `internal` class must have a KDoc                 |
| `UndocumentedPublicFunction`  | Every `public` / `internal` function must have a KDoc              |
| `UndocumentedPublicProperty`  | Every `public` / `internal` property must have a KDoc              |
| `OutdatedDocumentation`       | KDoc `@param` / `@return` tags must match the current signature    |

**Consequence:** any public API added without KDoc will fail the detekt check.
Always write KDoc when adding or changing public/internal declarations.

For what KDoc must and must not contain, see the KDoc Rules section in `CLAUDE.md`.

---

## Rules that are intentionally disabled

| Rule              | Why disabled                                                              |
|-------------------|---------------------------------------------------------------------------|
| `ForbiddenComment`| `// TODO:` comments are allowed — see TODO Tracking section in `CLAUDE.md`|
| `MagicNumber`     | Compose UI code uses literal dp/float values by design                    |

---

## Running locally

```bash
# ktlint
./gradlew ktlintCheck        # check
./gradlew ktlintFormat       # auto-fix

# detekt
./gradlew detekt             # check (HTML report in build/reports/detekt/)
```

> There is no auto-fix for detekt — violations must be fixed manually.
