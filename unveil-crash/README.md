# unveil-crash

Crash simulation plugin for Unveil. Provides a set of on-demand failure
modes to help QA and developers reproduce and verify crash handling, crash
reporting integrations, and recovery flows without needing to write
one-off test code.

## Installation

```kotlin
// build.gradle.kts
implementation("me.passos.libs.unveil:unveil-crash:<version>")
```

## Usage

```kotlin
Unveil.configure {
    register(CrashPlugin())
}
```

Install only in debug or QA builds — never in production.

## What is simulated

| Action              | Description                                                   |
|---------------------|---------------------------------------------------------------|
| Unhandled Exception | Throws an uncaught `RuntimeException` on the current thread   |
| Stack Overflow      | Triggers a `StackOverflowError` via infinite recursion        |
| Null Dereference    | Forces a `NullPointerException` by dereferencing null         |
| Out of Memory       | Allocates memory until the process is killed by the OS        |
| Main Thread Hang    | Blocks the main thread (ANR on Android, watchdog kill on iOS) |

Each action requires confirmation via a dialog before it fires.
