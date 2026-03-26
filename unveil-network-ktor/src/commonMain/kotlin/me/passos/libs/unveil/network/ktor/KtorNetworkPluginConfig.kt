package me.passos.libs.unveil.network.ktor

import me.passos.libs.unveil.network.NetworkInterceptor

/**
 * Configuration for [KtorNetworkPlugin].
 */
class KtorNetworkPluginConfig {
    /**
     * The [NetworkInterceptor] that receives captured traffic. Obtain this from
     * [NetworkPlugin.interceptor](me.passos.libs.unveil.network.NetworkPlugin.interceptor).
     */
    lateinit var interceptor: NetworkInterceptor
}
