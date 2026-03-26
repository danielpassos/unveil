# unveil-network-ktor

Ktor adapter for [unveil-network](../unveil-network/README.md).

Installs a Ktor `HttpClient` plugin that forwards every request and response to
Unveil's `NetworkInterceptor`. Works alongside content negotiation and any other
Ktor plugins, response bodies are captured at the earliest pipeline phase before
any transforms run.

## Installation

```toml
# gradle/libs.versions.toml
unveil-network-ktor = { module = "me.passos.libs.unveil:unveil-network-ktor", version.ref = "unveil" }
```

```kotlin
// build.gradle.kts
commonMain.dependencies {
    implementation(libs.unveil.network)
    implementation(libs.unveil.network.ktor)
}
```

## Usage

```kotlin
val networkPlugin = NetworkPlugin()

Unveil.configure {
    register(networkPlugin)
}

val httpClient = HttpClient {
    install(KtorNetworkPlugin) {
        interceptor = networkPlugin.interceptor
    }
}
```

## What is captured

| Field            | Captured                                       |
|------------------|------------------------------------------------|
| Method           | ✅                                              |
| URL              | ✅                                              |
| Request headers  | ✅                                              |
| Request body     | ✅ `String` and `ByteArray` bodies              |
| Status code      | ✅                                              |
| Response headers | ✅                                              |
| Response body    | ✅ when the caller reads the response body      |
| Duration         | ✅ round-trip from dispatch to headers received |
| Network errors   | ✅ connection failures, timeouts                |
