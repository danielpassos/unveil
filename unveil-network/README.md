# unveil-network

Network inspection plugin for [Unveil](../../README.md).

Captures every outgoing HTTP request and its response, method, URL, headers,
body, status code, and round-trip duration and displays them in the Unveil
drawer panel in real time.

This module owns the `NetworkInterceptor` interface. It has **no opinion about
your HTTP client**. Wire in the adapter for your framework
(e.g. `unveil-network-ktor`) or implement the interface yourself.

## Installation

```toml
# gradle/libs.versions.toml
unveil-network = { module = "me.passos.libs.unveil:unveil-network", version.ref = "unveil" }
```

```kotlin
// build.gradle.kts
commonMain.dependencies {
    implementation(libs.unveil.network)
}
```

## Usage

```kotlin
val networkPlugin = NetworkPlugin()

Unveil.configure {
    register(networkPlugin)
}
```

Then pass `networkPlugin.interceptor` to the adapter for your HTTP client.

## Implementing a custom adapter

If there is no built-in adapter for your HTTP framework, implement
`NetworkInterceptor` directly:

```kotlin
class MyHttpClientInterceptor(
    private val interceptor: NetworkInterceptor
) {
    fun onRequest(id: String, method: String, url: String) {
        interceptor.onRequestSent(
            NetworkRequest(
                id = id,
                method = method,
                url = url,
                headers = emptyMap(),
                body = null,
                sentAt = System.currentTimeMillis()
            )
        )
    }

    fun onResponse(id: String, statusCode: Int, durationMs: Long) {
        interceptor.onResponseReceived(
            id,
            NetworkResponse(
                statusCode = statusCode,
                headers = emptyMap(),
                body = null,
                durationMs = durationMs
            )
        )
    }

    fun onFailure(id: String, message: String) {
        interceptor.onError(id, message)
    }
}
```

## Built-in adapters

| Framework | Module                |
|-----------|-----------------------|
| Ktor      | `unveil-network-ktor` |
