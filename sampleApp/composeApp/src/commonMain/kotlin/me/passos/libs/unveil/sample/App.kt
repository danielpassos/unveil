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
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.launch
import me.passos.libs.unveil.Unveil
import me.passos.libs.unveil.UnveilHost
import me.passos.libs.unveil.network.NetworkPlugin
import me.passos.libs.unveil.network.ktor.KtorNetworkPlugin

/**
 * The root composable of the app.
 */
@Composable
fun App() {
    val networkPlugin = NetworkPlugin()

    Unveil.configure {
        register(CustomConfigurationPlugin())
        register(CustomBoxPlugin())
        register(networkPlugin)
    }
    Unveil.enable()

    // -- Http

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
        var uuid by remember { mutableStateOf("") }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = {
                    uuid = ""
                    coroutineScope.launch {
                        uuid = httpClient.get("https://httpbin.org/uuid").body()
                        httpClient
                            .post(
                                urlString = "https://httpbin.org/delay/5"
                            ) {
                                setBody("key=value")
                            }
                    }
                }
            ) {
                Text("Test")
            }

            Text(uuid)
        }
    }
}
