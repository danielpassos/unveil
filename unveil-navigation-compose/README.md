# unveil-navigation-compose

Compose Multiplatform Navigation adapter for [unveil-navigation](../unveil-navigation/README.md).

Bridges the `NavController` destination-changed listener to the Unveil navigation
inspection panel. Works on both Android and iOS via Compose Multiplatform Navigation.
Every destination change is captured as a `NavigationEntry` with the resolved route,
direction, arguments, and timestamp.

## Installation

```kotlin
// build.gradle.kts
implementation("me.passos.libs:unveil-navigation:<version>")
implementation("me.passos.libs:unveil-navigation-compose:<version>")
```

## Usage

Create a `NavigationPlugin`, register it with Unveil, then attach `ComposeNavigationObserver`
inside a `DisposableEffect` so the listener is cleaned up when the composable leaves
the composition.

```kotlin
val navigationPlugin = NavigationPlugin()

Unveil.configure {
    register(navigationPlugin)
}

// In your root composable — attach only in non-production builds
DisposableEffect(navController) {
    val observer = ComposeNavigationObserver(navController, navigationPlugin)
    onDispose { observer.dispose() }
}
```

The observer detaches itself from the `NavController` when `dispose()` is called,
preventing memory leaks when the `NavController` outlives the composable.
