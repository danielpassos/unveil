package me.passos.libs.unveil.core.drawer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import me.passos.libs.unveil.UnveilPlugin
import me.passos.libs.unveil.core.navigation.UnveilPanelScope

/**
 * Represents a page within the Unveil navigation hierarchy.
 *
 * Defines the different destinations that can be presented as
 * part of the Unveil experience, from the root list of plugins
 * to plugin-specific content and additional pages provided by plugins.
 */
internal sealed class DrawerPage {
    /**
     * The entry point of the Unveil navigation.
     *
     * Represents the list of available plugins and their associated actions.
     */
    data object PluginList : DrawerPage()

    /**
     * A page associated with a plugin.
     *
     * Delegates rendering to the plugin's content and provides it with the
     * necessary scope to interact with the Unveil navigation.
     *
     * @property plugin The plugin represented by this page.
     */
    @Immutable
    data class PluginPage(
        val plugin: UnveilPlugin
    ) : DrawerPage()

    /**
     * A page provided by a plugin.
     *
     * Created through [UnveilPanelScope] to present additional content within
     * the plugin's context.
     *
     * @property title Title associated with the page.
     * @property content Content to be displayed.
     */
    data class SubPage(
        val title: String,
        val content: @Composable () -> Unit
    ) : DrawerPage()
}
