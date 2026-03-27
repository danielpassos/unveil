# unveil-logs-kermit

Kermit adapter for [unveil-logs](../unveil-logs/README.md).

Adds a Kermit `LogWriter` that forwards all log events to an Unveil `LogsPlugin`.
Existing `Logger.d(...)` / `Logger.e(...)` call sites require no changes.

## Installation

```kotlin
// build.gradle.kts
implementation("me.passos.libs:unveil-logs-kermit:<version>")
```

## Usage

```kotlin
val logsPlugin = LogsPlugin()

Unveil.configure {
    register(logsPlugin)
}

// Add in debug / non-production builds only
if (BuildConfig.DEBUG) {
    Logger.addLogWriter(KermitLogSink(logsPlugin))
}
```

## Severity mapping

| Kermit `Severity` | Unveil `LogLevel` |
|-------------------|-------------------|
| Verbose           | Verbose           |
| Debug             | Debug             |
| Info              | Info              |
| Warn              | Warn              |
| Error             | Error             |
| Assert            | Assert            |
