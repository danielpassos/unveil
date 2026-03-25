package me.passos.libs.unveil

import androidx.compose.runtime.Composable
import me.passos.libs.unveil.core.navigation.UnveilPanelScope

/**
 * Represents a plugin recognized by Unveil.
 *
 * Implementations are registered via [UnveilConfig.register] and become
 * available to Unveil during configuration. Once registered, a plugin can
 * be accessed and used alongside other registered plugins.
 *
 * This interface defines the primary extension point of the system.
 *
 * Example:
 * ```kotlin
 * Unveil.configure {
 *     register(SomePlugin())
 * }
 * ```
 *
 * @see UnveilConfig.register
 */
interface UnveilPlugin {
    /**
     * Unique identifier for this plugin.
     * Used as a stable key in the registry.
     * Must be unique across all registered plugins.
     *
     * Convention: snake_case, e.g. "network_lab", "log_viewer".
     */
    val id: String

    /**
     * Human-readable title displayed in the plugin list.
     */
    val title: String

    /**
     * Icon displayed next to [title] in the plugin list.
     */
    val icon: UnveilIcon

    /**
     * Optional quick actions shown in
     * [QuickActionsBar][me.passos.libs.unveil.ui.QuickActionsBar]
     * on the plugin list screen.
     *
     * Quick actions are one-tap shortcuts for the most frequently
     * used settings in this plugin, allowing users to act without
     * navigating into the full panel.
     */
    val quickActions: List<QuickAction>
        get() = emptyList()

    /**
     * The full UI content for this plugin's panel (level 1).
     *
     * Rendered when the user taps this plugin's row in the plugin list.
     *
     * Use [scope] to push subpages (level 2) via [UnveilPanelScope.pushPage].
     */
    @Composable
    fun Content(scope: UnveilPanelScope)
}

/**
 * A quick-action shortcut displayed in
 * [QuickActionsBar][me.passos.libs.unveil.ui.QuickActionsBar].
 *
 * Each plugin declares its own quick actions.
 * They are rendered in plugin registration order.
 *
 * @property label The text label displayed to the user.
 * @property icon Optional icon displayed next to [label].
 * @property isActive Whether this quick action is currently active.
 * @property onToggle Callback invoked when the user taps this quick action.
 */
data class QuickAction(
    val label: String,
    val icon: UnveilIcon? = null,
    val isActive: Boolean = false,
    val onToggle: (Boolean) -> Unit
)

/**
 * Icon descriptor used by Unveil UI components.
 */
sealed class UnveilIcon {
    /**
     * A built-in icon from the Unveil icon set, identified by name.
     *
     * @property name The name of the icon, e.g. "network_lab", "log_viewer".
     */
    data class Builtin(
        val name: String
    ) : UnveilIcon()

    /**
     * A Unicode emoji or text character used as an icon.
     *
     * @property character The emoji or text character.
     */
    data class Emoji(
        val character: String
    ) : UnveilIcon()
}
