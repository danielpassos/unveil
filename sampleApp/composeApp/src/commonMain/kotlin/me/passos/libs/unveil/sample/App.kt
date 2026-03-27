package me.passos.libs.unveil.sample

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import me.passos.libs.unveil.Unveil
import me.passos.libs.unveil.UnveilHost
import me.passos.libs.unveil.crash.CrashPlugin
import me.passos.libs.unveil.deviceinfo.DeviceInfoPlugin
import me.passos.libs.unveil.logs.LogsPlugin
import me.passos.libs.unveil.logs.kermit.KermitLogSink
import me.passos.libs.unveil.navigation.NavigationPlugin
import me.passos.libs.unveil.navigation.compose.ComposeNavigationObserver
import me.passos.libs.unveil.network.NetworkPlugin
import me.passos.libs.unveil.network.ktor.KtorNetworkPlugin
import me.passos.libs.unveil.sample.network.NetworkResult
import me.passos.libs.unveil.sample.network.generateUuid
import me.passos.libs.unveil.sample.resources.Res
import me.passos.libs.unveil.sample.resources.sample_button_http_request
import me.passos.libs.unveil.sample.resources.sample_button_navigate
import me.passos.libs.unveil.sample.resources.sample_http_error
import me.passos.libs.unveil.sample.resources.sample_loading
import me.passos.libs.unveil.sample.resources.sample_network_error
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource

/**
 * The root composable of the app.
 */
@Composable
fun App() {
    Unveil.enable()

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
    }

    // -- Logs ----------------------------------------------------------------

    val logsPlugin = LogsPlugin(maxEntries = 10)
    Logger.addLogWriter(KermitLogSink(logsPlugin))
    Unveil.register(logsPlugin)

    // -- Network -------------------------------------------------------------

    val networkPlugin = NetworkPlugin()
    val httpClient =
        HttpClient {
            install(KtorNetworkPlugin) {
                plugin = networkPlugin
            }
        }
    Unveil.register(networkPlugin)

    // -- Navigation ----------------------------------------------------------

    val navController = rememberNavController()
    val navigationPlugin = NavigationPlugin(maxHistoryEntries = 10)
    Unveil.register(navigationPlugin)

    DisposableEffect(navController) {
        val observer = ComposeNavigationObserver(navController, navigationPlugin)
        onDispose { observer.dispose() }
    }

    UnveilHost(enabled = Unveil.isEnabled) {
        NavHost(
            navController = navController,
            startDestination = AppScreen.Start
        ) {
            composable<AppScreen.Start> {
                AppContent(
                    httpClient = httpClient,
                    onNavigate = {
                        navController.navigate(AppScreen.Screen1)
                    }
                )
            }
            composable<AppScreen.Screen1> {
                Screen(
                    screenName = "Screen 1"
                ) {
                    navController.navigate(AppScreen.Screen2)
                }
            }
            composable<AppScreen.Screen2> {
                Screen(
                    screenName = "Screen 2"
                ) {
                    navController.navigate(AppScreen.Screen3)
                }
            }
            composable<AppScreen.Screen3> {
                Screen(
                    screenName = "Screen 3"
                ) {
                    navController.navigate(AppScreen.Screen1)
                }
            }
        }
    }
}

@Composable
private fun AppContent(
    httpClient: HttpClient,
    onNavigate: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        val coroutineScope = rememberCoroutineScope()
        var httpResult by remember { mutableStateOf("") }
        val loadingText = stringResource(Res.string.sample_loading)
        val httpRequestText = stringResource(Res.string.sample_button_http_request)

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    Logger.i { "UUID requested" }

                    httpResult = loadingText
                    coroutineScope.launch {
                        httpResult =
                            when (val result = generateUuid(httpClient)) {
                                is NetworkResult.Success -> {
                                    Logger.i(tag = "uuid") { "UUID received: ${result.data}" }
                                    result.data
                                }

                                is NetworkResult.HttpError -> {
                                    Logger.e(tag = "uuid") { "Http error: ${result.status}" }
                                    getString(Res.string.sample_http_error, result.status)
                                }

                                is NetworkResult.NetworkError -> {
                                    Logger.e(tag = "uuid") { "Network error" }
                                    getString(Res.string.sample_network_error, result.throwable.message.orEmpty())
                                }
                            }
                    }
                }
            ) {
                Text(httpRequestText)
            }

            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = httpResult,
                    color = Color.White
                )
            }

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onNavigate
            ) {
                Text(stringResource(Res.string.sample_button_navigate))
            }
        }
    }
}

@Composable
private fun Screen(
    screenName: String,
    onNavigate: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = screenName,
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = onNavigate
            ) {
                Text(stringResource(Res.string.sample_button_navigate))
            }
        }
    }
}

@Serializable
private sealed interface AppScreen {
    @Serializable
    object Start : AppScreen

    @Serializable
    object Screen1 : AppScreen

    @Serializable
    object Screen2 : AppScreen

    @Serializable
    object Screen3 : AppScreen
}
