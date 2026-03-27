package me.passos.libs.unveil.network.ktor

import me.passos.libs.unveil.network.NetworkPlugin

/**
 * Configuration for [KtorNetworkPlugin].
 */
class KtorNetworkPluginConfig {
    /**
     * The [NetworkPlugin] instance to connect to this Ktor client.
     *
     * All network traffic captured by the client will be forwarded to the plugin,
     * and any configuration set in the panel (e.g. response delay) will be applied
     * automatically.
     */
    lateinit var plugin: NetworkPlugin
}
