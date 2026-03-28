<div align="center">

<picture>
  <source media="(prefers-color-scheme: dark)" srcset="assets/logo_dark.svg">
  <img src="assets/logo.svg" alt="Unveil" width="420">
</picture>

<br/>

> *The hidden side of your app, one swipe away.*

<br/>

[![Build](https://img.shields.io/github/actions/workflow/status/danielpassos/unveil/gradle.yml)](https://github.com/danielpassos/unveil/actions/workflows/gradle.yml)
[![Maven Central](https://img.shields.io/maven-central/v/me.passos.libs.unveil/unveil-core)](https://search.maven.org/search?q=g:me.passos.libs.unveil)
[![KMP](https://img.shields.io/badge/Kotlin-Multiplatform-blue.svg)](https://kotlinlang.org/multiplatform/)
[![Compose](https://img.shields.io/badge/Compose-Multiplatform-green.svg)](https://kotlinlang.org/compose-multiplatform/)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

</div>

---

## Why Unveil

Every app has two faces.

The one your users see — polished, intentional, pixel-perfect.
And the one underneath — network calls, logs, flags, navigation, state.

That second face is where bugs live.
That second face is what QA sees.
That second face is what you debug every day.

**Unveil makes it visible.**

A single swipe.
Everything is there.

And when you're done — it's gone.

---

## ✨ What You Get

- 💥 Trigger crashes on demand
- 📱 Device and environment info
- 📋 Stream and filter logs
- 🧭 Visualize navigation state
- 🌐 Inspect network requests in real time

All inside your app.
No external tools. No rebuilds.

---

## ⚡ Quick Start

```kotlin
Unveil.configure {
    register(NetworkPlugin(...))
    register(LogPlugin(...))
}

if (BuildConfig.DEBUG) Unveil.enable()

@Composable
fun App() {
    UnveilHost(enabled = Unveil.isEnabled) {
        AppNavHost()
    }
}
```

That’s it.

---

## 🧩 Plugin System

Unveil is not a tool.

It’s a **platform for tools**.

Everything is a plugin.

```kotlin
Unveil.configure {
    register(NetworkPlugin(...))
    register(LogPlugin(...))
}
```

And when you need flexibility:

```kotlin
Unveil.register(SomePlugin())
```

No coupling. No assumptions. Just composition.

---

## 🏗 Architecture

- **Zero impact on your app**
  `UnveilHost` wraps your UI. Nothing else changes.

- **Zero overhead when disabled**
  No UI. No gestures. No background work.

- **Zero dependency on your stack**
  No Material. No DI. No navigation library.

- **Fully modular**
  Core knows nothing about features.

---

## 📦 Installation

Unveil is modular.

Start with the core, then add only the plugins you need.

---

### Core

```toml
[versions]
unveil = "current_version"

[libraries]
unveil-core = { module = "me.passos.libs.unveil:unveil-core", version.ref = "unveil" }
```

```kotlin
commonMain.dependencies {
    implementation(libs.unveil.core)
}
```

---

### Add plugins

Pick the features you need:

```toml
unveil-crash  = { module = "me.passos.libs.unveil:unveil-crash", version.ref = "unveil" }
unveil-deviceinfo = { module = "me.passos.libs.unveil:unveil-deviceinfo", version.ref = "unveil" }
unveil-logs = { module = "me.passos.libs.unveil:unveil-logs", version.ref = "unveil" }
unveil-navigation = { module = "me.passos.libs.unveil:unveil-navigation", version.ref = "unveil" }
unveil-network = { module = "me.passos.libs.unveil:unveil-network", version.ref = "unveil" }
```

```kotlin
commonMain.dependencies {
    implementation(libs.unveil.logs)
    implementation(libs.unveil.network)
}
```

---

### Add adapters

Adapters connect Unveil to your stack:

```toml
unveil-logs-kermit = { module = "me.passos.libs.unveil:unveil-logs-kermit", version.ref = "unveil" }
unveil-network-ktor = { module = "me.passos.libs.unveil:unveil-network-ktor", version.ref = "unveil" }
unveil-navigation-compose  = { module = "me.passos.libs.unveil:unveil-navigation-compose", version.ref = "unveil" }
```

---

### Full setup (copy & paste)

Want everything?

```toml
unveil-core = { module = "me.passos.libs.unveil:unveil-core", version.ref = "unveil" }

unveil-crash = { module = "me.passos.libs.unveil:unveil-crash", version.ref = "unveil" }
unveil-deviceinfo = { module = "me.passos.libs.unveil:unveil-deviceinfo", version.ref = "unveil" }

unveil-logs = { module = "me.passos.libs.unveil:unveil-logs", version.ref = "unveil" }
unveil-logs-kermit = { module = "me.passos.libs.unveil:unveil-logs-kermit", version.ref = "unveil" }

unveil-navigation = { module = "me.passos.libs.unveil:unveil-navigation", version.ref = "unveil" }
unveil-navigation-compose = { module = "me.passos.libs.unveil:unveil-navigation-compose", version.ref = "unveil" }

unveil-network = { module = "me.passos.libs.unveil:unveil-network", version.ref = "unveil" }
unveil-network-ktor = { module = "me.passos.libs.unveil:unveil-network-ktor", version.ref = "unveil" }
```

---

### Available modules

| Category   | Modules                                          |
|------------|--------------------------------------------------|
| Core       | `unveil-core`                                    |
| Crash      | `unveil-crash`                                   |
| Device     | `unveil-deviceinfo`                              |
| Logs       | `unveil-logs`, `unveil-logs-kermit`              |
| Navigation | `unveil-navigation`, `unveil-navigation-compose` |
| Network    | `unveil-network`, `unveil-network-ktor`          |

---

### Requirements

`unveil-core` requires:

- `compose.ui`
- `compose.animation`
- `compose.material3`
- `androidx.activity:activity-compose` (Android only)

---

## 🧪 Example Plugin

```kotlin
class MyPlugin : UnveilPlugin {
    override val id = "my_plugin"
    override val title = "My Plugin"
    override val icon = UnveilIcon.Emoji("🔧")

    @Composable
    override fun Content(scope: UnveilPanelScope) {
        // Build anything you want
    }
}
```

---

## 🔌 Adapters

Unveil is stack-agnostic.

Each integration point is defined as an interface.
You can use a built-in adapter or provide your own implementation.

### Built-in adapters

| Integration | Interface            | Adapter                      |
|-------------|----------------------|------------------------------|
| Logs        | `LogSink`            | Kermit                       |
| Network     | `NetworkInterceptor` | Ktor                         |
| Navigation  | `NavigationObserver` | Compose Navigation           |

Using OkHttp? Timber? Voyager?
Implement the interface — it’s a handful of methods.

---

## 📱 Platforms

| Platform | Support |
|---------|----|
| Android | ✅  |
| iOS     | ✅  |
| Desktop | 🔲 |

---

## 🧠 Philosophy

Unveil is not a debug panel.

It’s **observability inside your UI**.

---

## 📄 License

Apache 2.0
