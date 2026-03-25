package me.passos.libs.unveil.core.drawer

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.SpringSpec
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import me.passos.libs.unveil.UnveilPlugin
import me.passos.libs.unveil.core.navigation.UnveilPanelScope

/**
 * Coordinates the state and navigation of the Unveil drawer.
 *
 * Manages the visibility of the drawer and the navigation
 * hierarchy of pages displayed within it. It acts as the single
 * source of truth for both the current drawer state and the active content.
 */
@Stable
internal class DrawerController {
    /**
     * State representing how open the drawer currently is.
     *
     * Values range from `0f` to `1f`, where `0f` means
     * closed and `1f` means fully open.
     */
    val translationXFraction = Animatable(0f)

    private val springSpec = SpringSpec<Float>(dampingRatio = 0.8f, stiffness = 400f)

    /**
     * Whether the drawer is currently visible.
     */
    val isOpen: Boolean
        get() = translationXFraction.value > 0f

    /**
     * Opens the drawer.
     */
    suspend fun open() {
        translationXFraction.animateTo(1f, animationSpec = springSpec)
    }

    /**
     * Closes the drawer.
     */
    suspend fun close() {
        translationXFraction.animateTo(0f, animationSpec = springSpec)
    }

    /**
     * Updates the drawer state immediately.
     *
     * @param fraction New open fraction for the drawer.
     */
    suspend fun snapTo(fraction: Float) {
        translationXFraction.snapTo(fraction.coerceIn(0f, 1f))
    }

    /**
     * Navigation state for the Unveil hierarchy.
     *
     * The first entry is always the root page, and additional entries represent
     * navigation within plugin content.
     */
    val pageStack: SnapshotStateList<DrawerPage> = mutableStateListOf(DrawerPage.PluginList)

    /**
     * The page currently presented by the drawer.
     */
    val currentPage: DrawerPage
        get() = pageStack.last()

    /**
     * Navigates to the page associated with the given plugin.
     *
     * @param plugin Plugin whose page should become active.
     */
    fun navigateTo(plugin: UnveilPlugin) {
        while (pageStack.size > 1) pageStack.removeLast()
        pageStack.add(DrawerPage.PluginPage(plugin))
    }

    /**
     * Navigates to the previous page.
     *
     * @return `true` if navigation occurred, `false` otherwise.
     */
    fun navigateBack(): Boolean =
        if (pageStack.size > 1) {
            pageStack.removeLast()
            true
        } else {
            false
        }

    /**
     * Returns the navigation state to the root page.
     */
    fun resetToPluginList() {
        while (pageStack.size > 1) pageStack.removeLast()
    }

    /**
     * Creates a scope for navigation within the current plugin.
     *
     * The returned scope allows plugins to present additional pages without
     * depending directly on this controller.
     */
    fun createPanelScope(): UnveilPanelScope =
        object : UnveilPanelScope {
            override fun pushPage(
                title: String,
                content: @Composable () -> Unit
            ) {
                if (pageStack.lastOrNull() is DrawerPage.SubPage) pageStack.removeLast()
                pageStack.add(DrawerPage.SubPage(title = title, content = content))
            }

            override fun popPage() {
                if (pageStack.lastOrNull() is DrawerPage.SubPage) pageStack.removeLast()
            }
        }
}

/**
 * Remembers a [DrawerController] instance across recompositions.
 *
 * Provides a stable controller that retains the drawer state and navigation
 * within the current composition.
 */
@Composable
internal fun rememberDrawerController(): DrawerController = remember { DrawerController() }
