package me.passos.libs.unveil.sample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import kotlinx.coroutines.launch
import me.passos.libs.unveil.Unveil
import me.passos.libs.unveil.UnveilHost
import me.passos.libs.unveil.crash.CrashPlugin
import me.passos.libs.unveil.deviceinfo.DeviceInfoPlugin
import me.passos.libs.unveil.logs.LogsPlugin
import me.passos.libs.unveil.logs.kermit.KermitLogSink
import me.passos.libs.unveil.network.NetworkPlugin
import me.passos.libs.unveil.network.ktor.KtorNetworkPlugin
import me.passos.libs.unveil.sample.network.NetworkResult
import me.passos.libs.unveil.sample.network.generateUuid

/**
 * The root composable of the app.
 */
@Composable
fun App() {
    val logsPlugin = LogsPlugin(maxEntries = 10)
    val networkPlugin = NetworkPlugin()

    Unveil.configure {
        register(CustomBoxPlugin())
        register(CrashPlugin())
        register(
            DeviceInfoPlugin(
                appVersionName = "1.0.0",
                appBuildNumber = "636749",
                buildVariant = "debug",
                environment = "staging"
            )
        )
        register(logsPlugin)
        register(networkPlugin)
    }
    Unveil.enable()

    // -- Kermit --------------------------------------------------------------

    Logger.addLogWriter(KermitLogSink(logsPlugin))

    // -- Ktor ----------------------------------------------------------------

    val httpClient =
        HttpClient {
            install(KtorNetworkPlugin) {
                plugin = networkPlugin
            }
        }

    UnveilHost(enabled = Unveil.isEnabled) {
        AppContent(httpClient)
    }
}

@Composable
private fun AppContent(httpClient: HttpClient) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val coroutineScope = rememberCoroutineScope()
        var httpResult by remember { mutableStateOf("-") }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    Logger.i { "UUID requested" }

                    httpResult = "Loading..."
                    coroutineScope.launch {
                        httpResult =
                            when (val result = generateUuid(httpClient)) {
                                is NetworkResult.Success -> {
                                    Logger.i(tag = "uuid") { "UUID received: ${result.data}" }
                                    result.data
                                }

                                is NetworkResult.HttpError -> {
                                    Logger.e(tag = "uuid") { "Http error: ${result.status}" }
                                    "HTTP error: ${result.status}"
                                }

                                is NetworkResult.NetworkError -> {
                                    Logger.e(tag = "uuid") { "Network error" }
                                    "Network error: ${result.throwable.message}"
                                }
                            }
                    }
                }
            ) {
                Text("Test")
            }

            Text(httpResult)
        }
    }
}
