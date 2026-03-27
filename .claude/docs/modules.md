# Unveil — Module Structure

> Update this file in the same PR that adds, removes, or renames a module.
> Never allowed to drift from the actual `settings.gradle.kts`.

---

## Implemented

| Module                      | Role                                                                                                                                                            |
|-----------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `unveil-core`               | Drawer shell, plugin registry, `Unveil` API, theming, shared UI components. Required by all consumers.                                                         |
| `unveil-crash`              | Crash Simulation plugin — five on-demand failure modes (unhandled exception, stack overflow, null dereference, OOM, main thread hang). No adapter needed.       |
| `unveil-deviceinfo`         | Device Info plugin — displays app version, build variant, environment, device model, OS, screen, locale, and timezone. Uses `expect/actual` for platform fields. |
| `unveil-logs`               | Log Viewer plugin — owns `LogSink` interface, `LogsPlugin`, `LogStore`. Live filtering by level and tag/message search.                                         |
| `unveil-logs-kermit`        | Kermit adapter for `unveil-logs` — installs `KermitLogSink` as a Kermit `LogWriter`.                                                                           |
| `unveil-navigation`         | Navigation plugin — owns `NavigationObserver` interface, `NavigationPlugin`, `NavigationStore`. Live back stack and navigation history.                         |
| `unveil-navigation-compose` | Compose Multiplatform Navigation adapter for `unveil-navigation` — attaches `ComposeNavigationObserver` to a `NavController`. KMP (Android + iOS).             |
| `unveil-network`            | Network inspection plugin — owns `NetworkInterceptor` interface, `NetworkPlugin`, `NetworkStore`.                                                               |
| `unveil-network-ktor`       | Ktor adapter for `unveil-network` — installs `KtorNetworkPlugin` on an `HttpClient`.                                                                            |

---

## Planned (not yet implemented)

| Module                        | Role                                                                                                       |
|-------------------------------|------------------------------------------------------------------------------------------------------------|
| `unveil-featureflags`         | Feature Flags plugin — owns `FeatureFlagProvider` interface                                                |
| `unveil-featureflags-firebase`| Firebase Remote Config adapter for `unveil-featureflags`                                                   |
| `unveil-storage`              | Storage plugin — owns `StorageProvider` interface                                                          |
| `unveil-storage-datastore`    | Jetpack DataStore adapter for `unveil-storage`                                                             |
| `unveil-storage-sqldelight`   | SQLDelight adapter for `unveil-storage`                                                                    |

---

## Dependency rules

- `unveil-core` → Compose Multiplatform + Kotlin stdlib only
- Feature modules → `unveil-core` only (never each other, never adapter modules)
- Adapter modules → exactly one feature module + one external framework
