# Unveil — Architecture

> Every structural change new modules, new interfaces, renamed files, new adapters
> must be reflected here in the same commit/PR that introduces the change.
> Never allowed to drift.

---

## Table of Contents

1. [Overview](#1-overview)
2. [The Adapter Pattern](#2-the-adapter-pattern)
3. [Source Set Conventions](#3-source-set-conventions)
4. [Core Concepts](#4-core-concepts)
5. [Drawer Navigation Model](#5-drawer-navigation-model)
6. [Platform Targets](#6-platform-targets)
7. [Drawer Rendering Pipeline](#7-drawer-rendering-pipeline)
8. [Gesture System](#8-gesture-system)
9. [Navigation Model — Technical](#9-navigation-model--technical)
10. [Back Navigation](#10-back-navigation)
11. [Theming](#11-theming)
12. [Drawer UI Principles](#12-drawer-ui-principles)
13. [Key Decisions](#13-key-decisions)

---

## 1. Overview

**Unveil** is a self-contained Compose Multiplatform + Kotlin Multiplatform library providing
a developer/QA drawer panel for Android and iOS applications.

- **Package root:** `me.passos.libs.unveil`
- **Platforms:** Android ✅, iOS ✅ (Compose Multiplatform), Desktop 🔲 (not in scope)
- **Maven group:** `me.passos.libs.unveil`

It renders a slide-in drawer on top of any Compose application without touching the
host application's layout tree.

```
┌──────────────────────────────────────┐
│  UnveilHost (full-screen Box)        │
│  ┌────────────────────────────────┐  │
│  │  content() — host app UI       │  │
│  └────────────────────────────────┘  │
│  ┌──────────────────────────────┐    │
│  │  UnveilDrawer (overlay)      │    │
│  │  ┌────────────────────────┐  │    │
│  │  │  AnimatedContent       │  │    │
│  │  │  (PluginList/Panel/Sub)│  │    │
│  │  └────────────────────────┘  │    │
│  └──────────────────────────────┘    │
│  DrawerHandle (right-edge tab)       │
└──────────────────────────────────────┘
```

When `enabled = false`, `content()` is rendered directly with zero overhead — no
extra layers, no gesture detectors.

### Non-negotiable design rules

1. **Agnostic core** — `unveil-core` and every feature module have zero dependency on any host
   application framework, design system, DI container, or navigation library.

2. **Every integration point is an interface** — HTTP clients, loggers, feature flag providers,
   navigation libraries, and storage layers are interfaces owned by the feature module.
   Concrete implementations live in separate adapter modules.

3. **Adapter modules are always optional** — consumers add only the adapters they need.
   Teams using Ktor add `unveil-network-ktor`. Teams using Kermit add `unveil-logs-kermit`.
   Teams with a custom logger implement `LogSink` directly.

4. **Architecture doc is always current** — updated in the same change that modifies the code.

---

## 2. The Adapter Pattern

Every feature module that integrates with an external framework follows this pattern:

```
unveil-[feature]/
  └── [Feature]Interface.kt     ← interface owned by the feature module
                                   NO framework imports allowed here

unveil-[feature]-[framework]/
  └── [Framework][Feature].kt   ← concrete adapter
                                   depends on: unveil-[feature] + the external framework
```

The plugin receives the interface (or a factory for it) at construction time.
The feature module **never** imports the adapter module.

---

## 3. Source Set Conventions

### Directory structure per module

```
unveil-[module]/
└── src/
    ├── commonMain/
    │   └── kotlin/me/passos/libs/unveil/[module]/
    ├── androidMain/
    │   └── kotlin/me/passos/libs/unveil/[module]/
    └── iosMain/
        └── kotlin/me/passos/libs/unveil/[module]/
```

### expect/actual file naming

Platform-specific files use the `.platform.kt` suffix convention:

```
UnveilBackHandler.kt              ← expect declaration (commonMain)
UnveilBackHandler.android.kt      ← actual for Android (androidMain)
UnveilBackHandler.ios.kt          ← actual for iOS (iosMain)
```

---

## 4. Core Concepts

### UnveilPlugin

Central contract. Every feature module implements this interface.

```kotlin
interface UnveilPlugin {
    val id: String                                       // unique key, e.g. "network_lab"
    val title: String                                    // shown in drawer index
    val icon: UnveilIcon                                 // Builtin(name) or Emoji(char)
    val quickActions: List<QuickAction>                  // default: emptyList()

    @Composable
    fun Content(scope: UnveilPanelScope)
}
```

### UnveilPanelScope

Two-level navigation within the drawer. No external nav library.

```kotlin
interface UnveilPanelScope {
    fun pushPage(title: String, content: @Composable () -> Unit)  // level 2
    fun popPage()                                                 // back to level 1
}
```

### QuickAction

Shortcut chip in the horizontal strip at the top of the drawer index.
Each plugin declares its own. Rendered in registration order, left to right.

```kotlin
data class QuickAction(
    val label: String,
    val icon: UnveilIcon?,
    val isActive: Boolean,
    val onToggle: (Boolean) -> Unit,
)
```

### UnveilIcon

```kotlin
sealed class UnveilIcon {
    data class Builtin(val name: String) : UnveilIcon()  // library's own icon set
    data class Emoji(val character: String) : UnveilIcon()
}
```

---

## 5. Drawer Navigation Model

```
Level 0 — Index
  ┌────────────────────────────────────────┐
  │ Unveil                          [✕]    │
  ├────────────────────────────────────────┤
  │ [✈️ Offline]   [🐢 Slow 3G]            │  ← QuickActions (L → R)
  ├────────────────────────────────────────┤
  │ 🌐  Network Lab                 >      │
  │ 📋  Logs                        >      │
  │ 🏳  Feature Flags               >      │
  │ 🔍  State Inspector             >      │
  │ 🗄  Storage                     >      │
  │ 🧭  Navigation                  >      │
  │ 📱  Device Info                 >      │
  │ 💥  Crash Simulation            >      │
  └────────────────────────────────────────┘

Level 1 — Plugin Panel
  ┌────────────────────────────────────┐
  │  ←  Network Lab                    │
  │  ──────────────────────────────    │
  │  Request Inspector (12)  [Clear]   │
  │  GET /api/users  200  142ms  >     │
  └────────────────────────────────────┘

Level 2 — Sub-page (pushed by plugin via scope.pushPage)
  ┌────────────────────────────────────┐
  │  ←  GET /api/users                 │
  │  ──────────────────────────────    │
  │  Status    200 OK                  │
  │  Duration  142ms                   │
  └────────────────────────────────────┘
```

---

## 6. Platform Targets

| Target  | Support                          |
|---------|----------------------------------|
| Android | ✅ Full                           |
| iOS     | ✅ Full (Compose Multiplatform)   |
| Desktop | 🔲 Not in scope for v1           |


## 7. Drawer Rendering Pipeline

### Why `graphicsLayer` instead of animated layout

The drawer panel is always present in the layout tree at its natural position
(right-aligned, occupying `drawerWidthFraction × hostWidth` pixels). It is
**visually translated** off-screen to the right via a GPU layer transform:

```kotlin
Modifier.graphicsLayer {
    translationX = drawerWidthPx * (1f - controller.translationXFraction.value)
}
```

| `translationXFraction` | `translationX`      | Result                       |
|------------------------|---------------------|------------------------------|
| `0f`                   | `drawerWidthPx`     | Panel fully off-screen right |
| `0.5f`                 | `drawerWidthPx / 2` | Panel half-revealed          |
| `1f`                   | `0`                 | Panel fully visible          |

Using `graphicsLayer` avoids layout reflows during animation. Only the GPU
layer matrix changes — the layout bounds are stable — so the draw pass is
cheaper and the animation runs on the render thread without main-thread
composition involvement.

### `Animatable` drives the fraction

`DrawerController.translationXFraction` is an `Animatable<Float>`. It supports
two modes:

- **Spring animation** — `open()` and `close()` call `animateTo()` with a
  `SpringSpec(dampingRatio=0.8f, stiffness=400f)`, producing a fast,
  slightly bouncy snap.
- **Instant snap** — `snapTo()` is called on every frame during a live drag,
  keeping the panel glued to the user's finger position without interpolation.

### Scrim

When `controller.isOpen` is `true`, a full-screen `Box` is placed behind the
drawer panel. Tapping the scrim launches `controller.close()` via a coroutine scope.

### Left-edge accent line

A vertical line is painted at `x = 0` of the drawer panel using `Modifier.drawBehind`.
It serves as a visual separator between the host app content and the Unveil panel.

### In-drawer navigation transitions

`AnimatedContent` is keyed on `controller.currentPage`. The transition direction
is determined by whether the target page is `DrawerPage.PluginList` (backward) or not
(forward):

```
Forward  →  slideInHorizontally { it }    togetherWith  slideOutHorizontally { -it/3 }
Backward →  slideInHorizontally { -it/3 } togetherWith  slideOutHorizontally { it }
```

The outgoing screen exits at 1/3 speed while the incoming screen enters at full
speed. This gives a sense of depth — deeper pages slide in faster than the index retreats.

---

## 8. Gesture System

### Architecture

The `.drawerGesture()` modifier is attached to the full-screen `Box` in
`UnveilHost`, covering the entire window. It uses
`detectHorizontalDragGestures` from Compose Foundation.

```
UnveilHost Box
└── .drawerGesture(controller, drawerWidthPx, screenWidthPx, scope, density)
    └── pointerInput
        └── detectHorizontalDragGestures
```

### Opening — drawer closed

A drag is tracked only when it starts within `EDGE_ZONE_DP = 40 dp` of the
right edge:

```
distanceFromRightEdge = screenWidthPx - offset.x
isTracking = controller.isOpen || distanceFromRightEdge <= edgeZonePx
```

While tracked, each drag delta updates the fraction:

```
newFraction = (currentFraction - dragAmount / drawerWidthPx).coerceIn(0f, 1f)
```

A leftward drag produces a **negative** `dragAmount`, so subtracting it
increases the fraction (more drawer revealed).

### Closing — drawer open

When `controller.isOpen` is `true`, `isTracking` is set unconditionally on
`onDragStart`, so the user can drag the drawer closed from anywhere on screen —
not just the edge.

### Commit threshold

On `onDragEnd`, the current fraction is compared to **0.4**:

```
if (fraction > 0.4f) → controller.open()               // spring to 1f
else                 → controller.close()              // spring to 0f
                       controller.resetToPluginList()  // reset nav stack
```

Resetting to the index on implicit close ensures the next open always shows the
plugin list, not a stale panel from a previous session.

### Tap pass-through

`detectHorizontalDragGestures` only takes ownership after the system touch-slop
threshold (~8 dp by default) is exceeded. Taps never reach slop and propagate
normally to child composables.

### Android system gesture exclusion

On Android 10+ (API 29+), the OS interprets right-edge swipes as a back
gesture before Compose sees them. `UnveilGestureExclusionEffect` calls
`View.systemGestureExclusionRects` to exclude the rightmost `edgeZonePx`
column from OS interception. This is re-applied after every composition via
`SideEffect` to survive rotation and window resize. On iOS the function is
a no-op.

---

## 9. Navigation Model — Technical

### Stack structure

`DrawerController.pageStack` is a `SnapshotStateList<DrawerPage>`. It starts
with a single `DrawerPage.PluginList` entry and grows to a maximum depth of 3:

```
[0] DrawerPage.PluginList   — plugin list; always present; cannot be popped
[1] DrawerPage.PluginPanel  — a plugin's root panel; optional
[2] DrawerPage.SubPage      — plugin-pushed sub-page; at most one at a time
```

### Why `SnapshotStateList`

Structural mutations on a `SnapshotStateList` are automatically observable by
any composable that reads the list during composition. `currentPage` is a
derived property:

```kotlin
val currentPage: DrawerPage
    get() = pageStack.last()
```

Reading `pageStack.last()` registers a dependency. When `navigateTo`,
`navigateBack`, or `pushPage` mutate the stack, Compose schedules a
recomposition of every affected composable — including `UnveilDrawer`, which
hands the new `currentPage` to `AnimatedContent`.

### Navigation operations

| Operation                     | Stack before                         | Stack after                             |
|-------------------------------|--------------------------------------|-----------------------------------------|
| `navigateTo(plugin)`          | `[PluginList]`                       | `[PluginList, PluginPanel(plugin)]`     |
| `navigateTo(plugin)`          | `[PluginList, PluginPanel(old)]`     | `[PluginList, PluginPanel(plugin)]`     |
| `scope.pushPage(title) { … }` | `[PluginList, PluginPanel]`          | `[PluginList, PluginPanel, SubPage]`    |
| `scope.pushPage(title) { … }` | `[PluginList, PluginPanel, SubPage]` | `[PluginList, PluginPanel, SubPage']`   |
| `navigateBack()`              | `[PluginList, PluginPanel, SubPage]` | `[PluginList, PluginPanel]`             |
| `navigateBack()`              | `[PluginList, PluginPanel]`          | `[PluginList]`                          |
| `navigateBack()`              | `[PluginList]`                       | `[PluginList]` (no-op, returns `false`) |
| `resetToPluginList()`         | `[PluginList, PluginPanel, SubPage]` | `[PluginList]`                          |

### Sub-page uniqueness

Only one sub-page can exist at a time. `pushPage` checks whether the top of the
stack is already a `SubPage` and replaces it rather than stacking a second one:

```kotlin
if (pageStack.lastOrNull() is DrawerPage.SubPage) pageStack.removeLast()
pageStack.add(DrawerPage.SubPage(title, content))
```

---

## 10. Back Navigation

Three mechanisms allow the user to navigate backward:

| Mechanism                           | Condition                         | Action                      |
|-------------------------------------|-----------------------------------|-----------------------------|
| "‹" button in `DrawerBackHeader`    | Always visible in Panel / SubPage | `controller.navigateBack()` |
| Android system back (`BackHandler`) | `pageStack.size > 1`              | `controller.navigateBack()` |
| Scrim tap                           | `controller.isOpen`               | `controller.close()`        |

`UnveilBackHandler` is registered in `UnveilDrawer` with `enabled = pageStack.size > 1`.
When disabled, the system back propagates normally to the host application's back
stack — Unveil does not interfere when the drawer is on the index screen.

If `navigateBack()` returns `false` (already at index), the back handler falls
through to `controller.close()`.

---

## 11. Theming

### No Material dependency

The drawer uses no `MaterialTheme`. This is intentional:

- The library is framework-agnostic — the host app may or may not use Material.
- The drawer must remain visually distinct from the host UI to avoid confusion.

All design tokens come from `UnveilTheme`.

### Composition locals

```kotlin
internal val LocalUnveilColors     = staticCompositionLocalOf { DefaultDarkColors }
internal val LocalUnveilTypography = staticCompositionLocalOf { DefaultTypography }

internal object UnveilTheme {
    val colors: UnveilColors         @Composable get() = LocalUnveilColors.current
    val typography: UnveilTypography @Composable get() = LocalUnveilTypography.current
}
```

`staticCompositionLocalOf` provides default values without requiring an explicit
`CompositionLocalProvider` at the host level. `UnveilThemeProvider` is called
once at the top of `UnveilDrawer`, providing the palette to all descendant
composables.

### Interactive press feedback

Because no `MaterialTheme` is in scope, `LocalIndication.current` is null
inside `UnveilDrawer` and `clickable` shows no ripple. Manual press feedback
is implemented everywhere via `MutableInteractionSource` +
`collectIsPressedAsState`:

```kotlin
val interactionSource = remember { MutableInteractionSource() }
val isPressed by interactionSource.collectIsPressedAsState()

Modifier
    .background(if (isPressed) UnveilTheme.colors.surfaceVariant else Color.Transparent)
    .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
```

---

## 12. Drawer UI Principles

- Drawer width: 90% of screen, slides in from the right edge.
- Gesture activation zone: 40dp sliver on the right edge (closed state).
- Uses only Compose Multiplatform primitives — no Material3, no host design system.
- Internal design tokens in `UnveilTheme` (dark palette by default — visually distinct from production UI).
- Animation: `Animatable<Float>` driving `graphicsLayer { translationX }`. No `ModalDrawerSheet`.
- Scrim rendered as a semi-transparent `Box` behind the drawer when open.
- Shared UI components prefixed `Unveil*` — live in `unveil-core/ui/components/`.

---

## 13. Key Decisions

These decisions are intentional. Do not reverse without updating this doc.

| Decision                                                                             | Rationale                                                                                                                              |
|--------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------|
| Two-level nav via `SnapshotStateList` instead of NavHost                             | NavHost requires a navigation library dependency. Unveil's drawer nav is 3 levels max — a list is the simplest correct solution        |
| `NetworkInterceptorFactory` (factory pattern) instead of direct `NetworkInterceptor` | The interceptor must be created after the listener exists. A factory defers construction cleanly                                       |
| Plugin self-registration via `Unveil.configure { register(...) }`                    | Avoids reflection, annotation processing, or ServiceLoader. The consumer is explicit about what features are active                    |
| Dark theme by default in `UnveilTheme`                                               | The panel should be immediately recognizable as a dev tool, not mistaken for production UI                                             |
| `DeviceInfoProvider` as an interface (not `expect/actual`)                           | Allows consumers to inject custom build metadata (git commit, flavor, CI number) by wrapping a platform provider                       |
| `graphicsLayer` translation instead of animated layout                               | Avoids layout reflows during animation; only the GPU layer matrix changes                                                              |

