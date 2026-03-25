package me.passos.libs.unveil.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable

/**
 * Scope provided to plugins for interacting with the Unveil navigation.
 *
 * Allows plugins to present additional content and control
 * navigation within their own context in the Unveil hierarchy.
 */
@Stable
interface UnveilPanelScope {
    /**
     * Requests navigation to a new page within the current plugin.
     *
     * The provided [content] is associated with the given [title]
     * and becomes the active page for this scope.
     *
     * @param title Title associated with the new page.
     * @param content Content to be displayed for the new page.
     */
    fun pushPage(
        title: String,
        content: @Composable () -> Unit
    )

    /**
     * Requests navigation to the previous page within the current plugin.
     *
     * If no additional page is present, this call has no effect.
     */
    fun popPage()
}
