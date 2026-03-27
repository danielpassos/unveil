# unveil-logs

Log Viewer plugin for [Unveil](../README.md).

Displays a live stream of log events with level filtering and free-text search across
tag and message. Buffer size is configurable with a default cap of 100 entries.

## What Is Captured

| Field     | Source                                  |
|-----------|-----------------------------------------|
| Level     | Mapped from framework severity by adapter |
| Tag       | Source tag from the originating logger  |
| Message   | Log message                             |
| Timestamp | Wall-clock time at capture (HH:mm:ss.SSS UTC) |
| Error     | Throwable message, if any               |

## Installation

```kotlin
// build.gradle.kts
implementation("me.passos.libs:unveil-logs:<version>")
```

## Usage

```kotlin
val logsPlugin = LogsPlugin(maxEntries = 200) // default: 100

Unveil.configure {
    register(logsPlugin)
}
```

Pass `logsPlugin.sink` to a framework adapter (e.g. `unveil-logs-kermit`) to forward
log events automatically. The adapter should only be registered in non-production builds.
