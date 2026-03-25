package me.passos.libs.unveil

import androidx.annotation.VisibleForTesting

/**
 * Internal singleton registry that holds the ordered
 * list of registered [UnveilPlugin]s.
 */
internal object UnveilRegistry {
    private val _plugins: MutableList<UnveilPlugin> = mutableListOf()

    /**
     * Ordered, immutable snapshot of all registered plugins.
     */
    val plugins: List<UnveilPlugin>
        get() = _plugins.toList()

    /**
     * Registers a plugin.
     *
     * Plugins appear in the plugin list in registration order.
     * If a plugin with the same [UnveilPlugin.id]
     * is already registered, it is replaced.
     */
    fun register(plugin: UnveilPlugin) {
        val index = _plugins.indexOfFirst { it.id == plugin.id }
        if (index >= 0) {
            _plugins[index] = plugin
        } else {
            _plugins.add(plugin)
        }
    }

    /**
     * Removes all registered plugins.
     *
     * ⚠️ Intended for testing only.
     */
    @VisibleForTesting
    internal fun clearAll() {
        _plugins.clear()
    }
}
