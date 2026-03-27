# Unveil Sample App

[![Kotlin Multiplatform](https://img.shields.io/badge/Kotlin-Multiplatform-blue.svg)]()
[![Compose Multiplatform](https://img.shields.io/badge/Compose-Multiplatform-green.svg)]()

This sample app demonstrates how to integrate and use **Unveil** in a real application.

> The hidden side of your app, one swipe away.

---

## Overview

This project showcases how to:

- Configure Unveil
- Register plugins
- Enable and host the Unveil drawer
- Interact with developer tools at runtime

---

## Important

This app is intentionally minimal.

### It is meant to:
- Demonstrate Unveil integration
- Provide a working reference
- Help you get started quickly

### It is NOT meant to:
- Teach Compose Multiplatform
- Represent production architecture
- Showcase best practices
- Demonstrate DI, navigation, or app structure

---

## Setup

Register plugins during initialization:

```kotlin
Unveil.configure {
    register(NetworkLabPlugin(...))
    register(LogViewerPlugin(...))
    register(DeviceInfoPlugin())
}
```

Enable Unveil:

```kotlin
Unveil.enable()
```

Wrap your app:

```kotlin
UnveilHost {
    App()
}
```

---

## Usage

1. Run the app
2. Swipe from the right edge
3. Explore tools:

- 💥 Crash Simulation
- 📱 Device Info
- 📋 Logs
- 🌐 Network Lab
- 🧭 Navigation

---

## Plugins

Each feature is implemented as a plugin.

You can:
- Add only what you need
- Remove any plugin
- Create your own plugins

---

## Why This Exists

This app exists to:

- Reduce onboarding friction
- Provide a copy-paste starting point
- Demonstrate real usage of the API

---

## Next Steps

- Explore the main README
- Try enabling/disabling plugins
- Build your own plugin
- Integrate Unveil into your app

