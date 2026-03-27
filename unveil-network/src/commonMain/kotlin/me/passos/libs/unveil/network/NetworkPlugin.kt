package me.passos.libs.unveil.network

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import me.passos.libs.unveil.QuickAction
import me.passos.libs.unveil.UnveilIcon
import me.passos.libs.unveil.UnveilPlugin
import me.passos.libs.unveil.core.navigation.UnveilPanelScope
import me.passos.libs.unveil.network.ui.NetworkPanel

/**
 * Unveil plugin that captures and displays HTTP network traffic.
 *
 * Pass this plugin directly to a network framework adapter (e.g. the Ktor adapter) to
 * forward captured traffic to the panel and make all panel controls (such as response
 * delay) take effect automatically.
 *
 * Usage:
 * ```kotlin
 * val networkPlugin = NetworkPlugin()
 *
 * Unveil.configure {
 *     register(networkPlugin)
 * }
 *
 * val httpClient = HttpClient {
 *     install(KtorNetworkPlugin) {
 *         plugin = networkPlugin
 *     }
 * }
 * ```
 */
class NetworkPlugin : UnveilPlugin {
    /**
     * Backing store that holds all captured [NetworkEntry] instances.
     *
     * Exposed so that host apps can query captured traffic programmatically if needed.
     */
    val store: NetworkStore = NetworkStore()

    /**
     * Receiver that framework adapters call to report request and response events.
     *
     * Pass this to the adapter of your choice (e.g. KtorNetworkPlugin) during
     * HTTP client setup.
     */
    val interceptor: NetworkInterceptor = NetworkStoreInterceptor(store)

    /**
     * Configuration that framework adapters read to apply artificial response delay.
     *
     * Pass this to the adapter of your choice during HTTP client setup to allow
     * delay to be controlled from the panel UI at runtime.
     */
    val delayConfig: NetworkDelayConfig = DelayState()

    private val delayState get() = delayConfig as DelayState

    override val id: String = "network"
    override val title: String = "Network"
    override val icon: UnveilIcon = UnveilIcon.Emoji("🌐")

    override val quickActions: List<QuickAction> =
        listOf(
            QuickAction(
                label = "Clear",
                icon = UnveilIcon.Emoji("🌐"),
                onToggle = { store.clear() }
            )
        )

    @Composable
    override fun Content(scope: UnveilPanelScope) {
        NetworkPanel(
            store = store,
            delayEnabled = delayState.enabled,
            delaySeconds = delayState.delaySeconds,
            onDelayEnabledChange = { delayState.enabled = it },
            onDelaySecondsChange = { delayState.delaySeconds = it },
            scope = scope
        )
    }

    private class DelayState : NetworkDelayConfig {
        override var enabled: Boolean by mutableStateOf(false)
        var delaySeconds: Float by mutableFloatStateOf(1f)
        override val delayMs: Long get() = (delaySeconds * 1000).toLong()
    }
}
