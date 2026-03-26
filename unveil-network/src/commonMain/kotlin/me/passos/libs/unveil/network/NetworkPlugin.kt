package me.passos.libs.unveil.network

import androidx.compose.runtime.Composable
import me.passos.libs.unveil.QuickAction
import me.passos.libs.unveil.UnveilIcon
import me.passos.libs.unveil.UnveilPlugin
import me.passos.libs.unveil.core.navigation.UnveilPanelScope
import me.passos.libs.unveil.network.ui.NetworkPanel

/**
 * Unveil plugin that captures and displays HTTP network traffic.
 *
 * Wire [interceptor] into a network framework adapter (e.g. the Ktor adapter) so that
 * outgoing requests and their responses are forwarded to the plugin's internal store.
 * The panel then presents the captured traffic in real time.
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
 *         interceptor = networkPlugin.interceptor
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
        NetworkPanel(store = store, scope = scope)
    }
}
