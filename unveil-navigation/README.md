# unveil-navigation

Navigation inspection plugin for [Unveil](../README.md).

Displays the live back stack and a scrollable history of navigation events. Each event records
the destination route, direction of travel (push or pop), resolved arguments, and a timestamp.

## What Is Captured

| Field      | Source                                                   |
|------------|----------------------------------------------------------|
| Route      | Route template as declared in the navigation graph       |
| Direction  | Push (forward) or Pop (back)                             |
| Arguments  | Actual argument values at the time of navigation         |
| Timestamp  | Wall-clock time at capture (HH:mm:ss.SSS UTC)            |
| Back Stack | Full stack snapshot after each navigation event          |

## Installation

```kotlin
// build.gradle.kts
implementation("me.passos.libs:unveil-navigation:<version>")
```

## Usage

```kotlin
val navigationPlugin = NavigationPlugin(maxHistoryEntries = 50) // default: 50

Unveil.configure {
    register(navigationPlugin)
}
```

Pass `navigationPlugin.observer` to a navigation framework adapter (e.g. `unveil-navigation-jetpack`)
to forward events automatically. The adapter should only be attached in non-production builds.
