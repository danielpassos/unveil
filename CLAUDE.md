# Unveil — Claude Code Project Memory

## What This Project Is

**Unveil** is a Kotlin Multiplatform (KMP) + Compose Multiplatform developer/QA panel library
for Android and iOS. It provides a hidden drawer (swipe from right edge) that exposes the
internal state of a running app — network requests, logs, feature flags, navigation stack,
storage, device info, crash simulation, and more.

It is designed to be:

- **Fully agnostic** — zero dependency on any host app's design system, DI framework, or nav lib
- **Plugin-based** — every feature is a self-contained module that self-registers
- **Extractable** — can be dropped into any KMP project without modification

---

## Package

All source code lives under: `me.passos.libs.unveil`

---

### Dependency rules — strictly enforced

- No feature module depends on another feature module
- No feature module depends on any adapter module
- Adapter modules depend on exactly one feature module + one external framework
- `unveil-core` depends only on Compose Multiplatform + Kotlin stdlib
- Violations of these rules must be flagged and fixed immediately

---

## The Adapter Pattern

Every integration point follows the same pattern:

```
unveil-[feature]/
  └── [Feature]Interface.kt     ← interface owned by the feature module (no framework imports)

unveil-[feature]-[framework]/
  └── [Framework][Feature].kt   ← concrete adapter (depends on feature module + framework)
```

**Examples:**

- `NetworkInterceptor` lives in `unveil-network` — zero Ktor knowledge
- `KtorNetworkInterceptor` lives in `unveil-network-ktor` — implements `NetworkInterceptor`
- `LogSink` lives in `unveil-logs` — zero Kermit knowledge
- `KermitLogSink` lives in `unveil-logs-kermit` — implements `LogSink`

When adding new integrations, always follow this pattern. Never put framework-specific
code in a feature module.

---

## Platform Targets

| Platform | Support                        |
|----------|--------------------------------|
| Android  | ✅ Full                         |
| iOS      | ✅ Full (Compose Multiplatform) |
| Desktop  | 🔲 Not planned                 |

Platform-specific files follow the naming convention:

- `*.android.kt` for `androidMain`
- `*.ios.kt` for `iosMain`

---

## UI Rules

- All design tokens come from `UnveilTheme` (colors, typography)
- Shared UI components live in `unveil-core/ui/components/` and are prefixed `Unveil*`
  (e.g. `UnveilSectionHeader`, `UnveilToggleRow`, `UnveilValueRow`, `UnveilButton`, `UnveilTextField`)
- Feature modules import shared components from `unveil-core` — never duplicate them

## KDoc Rules

KDoc must describe **what** something is and **why** it exists — not how it looks or how it is implemented.

**Never include in KDoc:**

- Colors, color tokens, or hex values (e.g. "rendered in `error` color", "filled with `chipActive`")
- Sizes, dimensions, or dp values (e.g. "52 dp tap target", "0.5 dp hairline", "rendered at 40 dp")
- Layout positions or structure (e.g. "on the left", "at the top", "full-width", "horizontal strip")
- Implementation mechanics (e.g. "uses `MutableInteractionSource`", "via `graphicsLayer`", "via `BasicText`")
- Style details (e.g. "pill-shaped", "monospace font", "semi-transparent")

**Good KDoc:** explains the role of the element, its contract, and its parameters.
**Bad KDoc:** describes what a reader can see by looking at the code for 5 seconds.

---

## Naming Conventions

| Thing                      | Convention                      | Example                                   |
|----------------------------|---------------------------------|-------------------------------------------|
| Plugin class               | `[Feature]Plugin`               | `NetworkPlugin`                           |
| Interface (feature module) | Descriptive noun                | `NetworkInterceptor`, `LogSink`           |
| Adapter class              | `[Framework][Interface]`        | `KtorNetworkInterceptor`, `KermitLogSink` |
| Adapter factory            | `[Framework]InterceptorFactory` | `KtorInterceptorFactory`                  |
| UI composables             | `Unveil*` prefix                | `UnveilSectionHeader`                     |
| Plugin IDs                 | `snake_case`                    | `"network"`, `"log_viewer"`               |
| Emoji icons                | Use meaningful emoji            | `UnveilIcon.Emoji("🌐")` for network      |

---

## TODOs Tracking

When you find a `// TODO:` comment, don't remove it unless you're implementing it.
Prefix completed TODOs with `// DONE:` until the next cleanup pass.

---

## README

`README.md` is at the project root. It contains the storytelling-based introduction,
installation guide, adapter table, and custom plugin example.
Do not auto-generate or overwrite it — edit it surgically.

## New Module Checklist

Every new module requires all three of the following before it is considered done:

1. **Module README** — create `<module>/README.md` covering: what it does, installation
   (Gradle dependency), usage example, and (for adapters) a "what is captured" table.

2. **Root README update** — edit `README.md` surgically:
   - Feature modules → add an entry under `## Features`
   - Adapter modules → add a row to the `## Adapters` table

3. **`modules.md` update** — move the module from Planned to Implemented in
   `.claude/docs/modules.md`.

## Documentation

Read these before touching the corresponding area. They are always kept current.

| Document                | Path                             |
|-------------------------|----------------------------------|
| Architecture            | `.claude/docs/architecture.md`   |
| Architecture Diagrams   | `.claude/docs/diagrams.md`       |
| Module Structure        | `.claude/docs/modules.md`        |
| Testing Guidelines      | `.claude/docs/testing.md`        |
| Linting & Code Style    | `.claude/docs/linting.md`        |
