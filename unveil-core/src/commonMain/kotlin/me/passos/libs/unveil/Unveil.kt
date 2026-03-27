package me.passos.libs.unveil

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import me.passos.libs.unveil.Unveil.disable
import me.passos.libs.unveil.Unveil.enable

/**
 * Entry point for configuring and controlling Unveil.
 *
 * ## Configuration
 *
 * Register plugins before the first composition:
 *
 * ```kotlin
 * Unveil.configure {
 *     register(SomePlugin())
 * }
 * ```
 *
 * ## Activation
 *
 * The host application controls when Unveil is active. The library does not
 * assume any build variant or environment:
 *
 * ```kotlin
 * Unveil.enable()
 * Unveil.disable()
 * ```
 *
 * ## Usage
 *
 * Wrap your application content with [UnveilHost]:
 *
 * ```kotlin
 * UnveilHost(enabled = Unveil.isEnabled) {
 *     AppNavHost()
 * }
 * ```
 */
object Unveil {
    private var _isEnabled: Boolean by mutableStateOf(false)

    /**
     * Whether Unveil is currently active.
     *
     * Updated via [enable] and [disable].
     */
    val isEnabled: Boolean
        get() = _isEnabled

    /**
     * Registers plugins in Unveil.
     *
     * - Can be invoked multiple times.
     * - Plugin identifiers must be unique.
     */
    fun configure(block: UnveilConfig.() -> Unit) {
        UnveilConfig().apply(block)
    }

    /**
     * Registers a plugin in Unveil.
     *
     * @param plugin Plugin to register.
     */
    fun register(plugin: UnveilPlugin) {
        UnveilRegistry.register(plugin)
    }

    /**
     * Enables Unveil.
     */
    fun enable() {
        _isEnabled = true
    }

    /**
     * Disables Unveil.
     */
    fun disable() {
        _isEnabled = false
    }

    internal val registry: UnveilRegistry
        get() = UnveilRegistry
}

/**
 * DSL receiver for [Unveil.configure].
 */
class UnveilConfig internal constructor() {
    /**
     * Registers a [UnveilPlugin] into the drawer.
     *
     * Plugins are displayed in the plugin list in the order they are registered.
     */
    fun register(plugin: UnveilPlugin) {
        UnveilRegistry.register(plugin)
    }
}
