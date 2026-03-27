<p align="center">
  <picture>
    <source media="(prefers-color-scheme: dark)" srcset="assets/logo_dark.svg">
    <img src="assets/logo.svg" alt="Unveil" width="500">
  </picture>
</p>

> *The hidden side of your app, one swipe away.*

Every app has two faces.

The one your users see, polished, intentional, pixel-perfect and the one underneath,
network calls firing, flags toggling, logs streaming, navigation stacks unwinding.
Most of the time that second face is invisible. It has to be.

**Unveil is the moment you choose to see the truth.**

A single swipe from the right edge of your screen. Suddenly the invisible becomes
visible, every request your app made, every flag that's active, every log line that
fired, the exact navigation stack your QA engineer is staring at when they file
that bug. When you're done, fold it back. Your users will never know it was there.

## Design Principles

**It doesn't touch your app.** `UnveilHost` wraps your content composable.
That's the only change to your existing code. Everything else, plugins, interceptors,
observers is wired up separately in your initialization layer.

**It disappears completely.** Call `Unveil.disable()` and the library exerts zero overhead.
No gesture detection, no UI, no background work. The consumer decides when Unveil is active.
The library makes no assumptions about build variants or environments.

**It doesn't depend on your stack.** No Material3. No Koin. No Jetpack Navigation. Unveil
ships its own minimal design system, and every integration point is an interface. It can
be extracted from any Compose Multiplatform project and dropped into any other.

**It's a plugin system, not a monolith.** Features register themselves. The drawer knows
nothing about the features. You can write and ship your own plugins using the same
`UnveilPlugin` interface.

## Platform Support

| Platform                    | Support        |
|-----------------------------|----------------|
| Android                     | ✅              |
| iOS (Compose Multiplatform) | ✅              |
| Desktop                     | 🔲 Not Planned |

## What It Looks Like

```kotlin
// One-time setup
Unveil.configure {
    register(SomePlugin())
}

// Your call. Your environment. Your rules.
if (BuildConfig.DEBUG) Unveil.enable()

// Wrap your app at once. Touch nothing else.
@Composable
fun App() {
    UnveilHost(enabled = Unveil.isEnabled) {
        AppNavHost()
    }
}
```

## Features

Unveil is a drawer panel that slides in from the right edge of your screen.
It's built around a **plugin system**, every feature is self-contained and optional.
Add only what you need.

### 📱 Device Info
See app version, build variant, environment, device model, OS version, screen resolution and
density, locale, and timezone, all in one place.

### 📋 Logs
Stream live log events from your app with filtering by level (V/D/I/W/E/A) and free-text
search across tag and message. Configurable buffer size. Zero changes to existing log call sites.

### 🌐 Network
Inspect every HTTP request and response in real time, delay responses and override status codes.
All without touching your app code or restarting a server.

## Installation

```toml
# gradle/libs.versions.toml
[versions]
unveil = "current_version"

[libraries]
# Core — required
unveil-core = { module = "me.passos.libs.unveil:unveil-core", version.ref = "unveil" }

# Features — add only what you need
unveil-network      = { module = "me.passos.libs.unveil:unveil-network",  version.ref = "unveil" }
unveil-network-ktor = { module = "io.github.yourname:unveil-network-log", version.ref = "unveil" }
```

```kotlin
// build.gradle.kts (common module)
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(libs.unveil.core)
            implementation(libs.unveil.network)
            implementation(libs.unveil.network-ktor)
        }
    }
}
```

### Extra considerations

`unveil-core` additionally requires:

- `compose.ui`, `compose.animation`, `compose.material3`
- `androidx.activity:activity-compose` in `androidMain` (for `BackHandler`)

## Writing a Custom Plugin

```kotlin
class MyCustomPlugin : UnveilPlugin {
    override val id = "my_plugin"
    override val title = "My Plugin"
    override val icon = UnveilIcon.Emoji("🔧")

    // Optional: chip-strip shortcuts at the top of the drawer
    override val quickActions = listOf(
        QuickAction(
            label = "Reset",
            icon = UnveilIcon.Emoji("♻️"),
            isActive = false
        ) {
            // your reset logic
        }
    )

    @Composable
    override fun Content(scope: UnveilPanelScope) {
        // Your plugin UI -> full Compose, no restrictions
        // Use scope.pushPage(...) to open a sub-page (level 2 navigation)
    }
}

// Register it alongside the built-in plugins
Unveil.configure {
    register(MyCustomPlugin())
}
```

## Adapters

Unveil has **zero opinion about your stack**. Every integration point is an interface.
Use the adapter for your framework or implement the interface yourself.

| Integration      | Interface             | Built-in Adapters                            |
|------------------|-----------------------|----------------------------------------------|
| HTTP client      | `NetworkInterceptor`  | Ktor (`unveil-network-ktor`)                 |

**Using OkHttp? Timber? LaunchDarkly?** Implement the interface. It's a handful of methods.

```kotlin
// Example: custom

// TODO
```
