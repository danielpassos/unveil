# Unveil — Module Structure

> Update this file in the same PR that adds, removes, or renames a module.
> Never allowed to drift from the actual `settings.gradle.kts`.

---

## Implemented

| Module                | Role                                                                                                   |
|-----------------------|--------------------------------------------------------------------------------------------------------|
| `unveil-core`         | Drawer shell, plugin registry, `Unveil` API, theming, shared UI components. Required by all consumers. |
| `unveil-network`      | Network inspection plugin — owns `NetworkInterceptor` interface, `NetworkPlugin`, `NetworkStore`.      |
| `unveil-network-ktor` | Ktor adapter for `unveil-network` — installs `KtorNetworkPlugin` on an `HttpClient`.                  |

---

## Planned (not yet implemented)

| Module                        | Role                                                                                                       |
|-------------------------------|------------------------------------------------------------------------------------------------------------|
| `unveil-logs`                 | Log Viewer plugin — owns `LogSink` interface                                                               |
| `unveil-logs-kermit`          | Kermit adapter for `unveil-logs`                                                                           |
| `unveil-featureflags`         | Feature Flags plugin — owns `FeatureFlagProvider` interface                                                |
| `unveil-featureflags-firebase`| Firebase Remote Config adapter for `unveil-featureflags`                                                   |
| `unveil-state`                | State Inspector plugin — owns `StateRegistration`                                                          |
| `unveil-storage`              | Storage plugin — owns `StorageProvider` interface                                                          |
| `unveil-storage-datastore`    | Jetpack DataStore adapter for `unveil-storage`                                                             |
| `unveil-storage-sqldelight`   | SQLDelight adapter for `unveil-storage`                                                                    |
| `unveil-navigation`           | Navigation plugin — owns `NavigationObserver` interface                                                    |
| `unveil-navigation-decompose` | Decompose adapter for `unveil-navigation`                                                                  |
| `unveil-navigation-voyager`   | Voyager adapter for `unveil-navigation`                                                                    |
| `unveil-deviceinfo`           | Device Info plugin — no adapter needed (uses `DeviceInfoProvider` interface with `expect/actual` default)  |
| `unveil-crash`                | Crash Simulation plugin — no adapter needed                                                                |

---

## Dependency rules

- `unveil-core` → Compose Multiplatform + Kotlin stdlib only
- Feature modules → `unveil-core` only (never each other, never adapter modules)
- Adapter modules → exactly one feature module + one external framework
