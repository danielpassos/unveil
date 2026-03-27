package me.passos.libs.unveil.navigation

import androidx.compose.runtime.Composable
import me.passos.libs.unveil.QuickAction
import me.passos.libs.unveil.UnveilIcon
import me.passos.libs.unveil.UnveilPlugin
import me.passos.libs.unveil.core.navigation.UnveilPanelScope
import me.passos.libs.unveil.navigation.ui.NavigationPanel

/**
 * Unveil plugin that captures and displays the navigation history and live back stack.
 *
 * Pass [observer] to a navigation framework adapter (e.g. [JetpackNavigationObserver])
 * to forward navigation events into the panel automatically.
 *
 * Usage:
 * ```kotlin
 * val navigationPlugin = NavigationPlugin()
 *
 * Unveil.configure {
 *     register(navigationPlugin)
 * }
 *
 * // Jetpack Compose Navigation example — attach in a DisposableEffect
 * DisposableEffect(navController) {
 *     val observer = JetpackNavigationObserver(navController, navigationPlugin)
 *     onDispose { observer.dispose() }
 * }
 * ```
 *
 * @param maxHistoryEntries Maximum number of navigation history entries to retain.
 * When exceeded, the oldest entry is discarded. Defaults to 50.
 */
class NavigationPlugin(maxHistoryEntries: Int = 50) : UnveilPlugin {

    internal val store: NavigationStore = NavigationStore(maxHistoryEntries)

    /**
     * Receiver that navigation framework adapters call to forward navigation events.
     */
    val observer: NavigationObserver = NavigationStoreObserver(store)

    override val id: String = "navigation"
    override val title: String = "Navigation"
    override val icon: UnveilIcon = UnveilIcon.Emoji("🧭")

    override val quickActions: List<QuickAction> =
        listOf(
            QuickAction(
                label = "Clear",
                icon = UnveilIcon.Emoji("🧭"),
                onToggle = { store.clearHistory() }
            )
        )

    @Composable
    override fun Content(scope: UnveilPanelScope) {
        NavigationPanel(store = store)
    }
}
