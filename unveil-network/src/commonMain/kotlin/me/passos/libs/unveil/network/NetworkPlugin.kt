package me.passos.libs.unveil.network

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
 * delay and status override) take effect automatically.
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
     */
    val interceptor: NetworkInterceptor = NetworkStoreInterceptor(store)

    // region — Delay

    /**
     * Configuration that framework adapters read to apply artificial response delay.
     */
    val delayConfig: NetworkDelayConfig = DelayState()

    /**
     * Whether artificial response delay is currently active.
     */
    var delayEnabled: Boolean
        get() = (delayConfig as DelayState).enabled
        set(value) { (delayConfig as DelayState).enabled = value }

    /**
     * Duration of the artificial delay in seconds.
     */
    var delaySeconds: Float
        get() = (delayConfig as DelayState).delaySeconds
        set(value) { (delayConfig as DelayState).delaySeconds = value }

    // endregion

    // region — Status override

    /**
     * Configuration that framework adapters read to override the recorded response status code.
     */
    val statusOverrideConfig: NetworkStatusOverrideConfig = StatusOverrideState()

    /**
     * Whether the response status code override is currently active.
     */
    var statusOverrideEnabled: Boolean
        get() = (statusOverrideConfig as StatusOverrideState).enabled
        set(value) { (statusOverrideConfig as StatusOverrideState).enabled = value }

    /**
     * The status code to use when [statusOverrideEnabled] is true.
     */
    var statusOverrideCode: Int
        get() = (statusOverrideConfig as StatusOverrideState).statusCode
        set(value) { (statusOverrideConfig as StatusOverrideState).statusCode = value }

    // endregion

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
            delayEnabled = delayEnabled,
            delaySeconds = delaySeconds,
            onDelayEnabledChange = { delayEnabled = it },
            onDelaySecondsChange = { delaySeconds = it },
            statusOverrideEnabled = statusOverrideEnabled,
            statusOverrideCode = statusOverrideCode,
            onStatusOverrideEnabledChange = { statusOverrideEnabled = it },
            onStatusOverrideCodeChange = { statusOverrideCode = it },
            scope = scope
        )
    }

    private class DelayState : NetworkDelayConfig {
        override var enabled: Boolean by mutableStateOf(false)
        var delaySeconds: Float by mutableFloatStateOf(1f)
        override val delayMs: Long get() = (delaySeconds * 1000).toLong()
    }

    private class StatusOverrideState : NetworkStatusOverrideConfig {
        override var enabled: Boolean by mutableStateOf(false)
        override var statusCode: Int by mutableIntStateOf(500)
    }
}
