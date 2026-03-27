package me.passos.libs.unveil.navigation.compose

import androidx.navigation.NavController
import androidx.navigation.NavGraph
import me.passos.libs.unveil.core.platform.currentTimeMs
import me.passos.libs.unveil.navigation.NavigationDirection
import me.passos.libs.unveil.navigation.NavigationEntry
import me.passos.libs.unveil.navigation.NavigationPlugin
import me.passos.libs.unveil.navigation.StackEntry
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

/**
 * Observes a Compose Multiplatform [NavController] and forwards every navigation event
 * to [NavigationPlugin].
 *
 * Attach in a [androidx.compose.runtime.DisposableEffect] so that the listener is
 * removed when the composable leaves the composition:
 *
 * ```kotlin
 * DisposableEffect(navController) {
 *     val observer = ComposeNavigationObserver(navController, navigationPlugin)
 *     onDispose { observer.dispose() }
 * }
 * ```
 */
@OptIn(ExperimentalUuidApi::class)
class ComposeNavigationObserver(
    private val navController: NavController,
    plugin: NavigationPlugin
) {
    private val observer = plugin.observer
    private val seenEntryIds = mutableSetOf<String>()

    private val listener =
        NavController.OnDestinationChangedListener { controller, destination, arguments ->
            if (destination is NavGraph) return@OnDestinationChangedListener

            val currentEntry =
                controller.currentBackStackEntry
                    ?: return@OnDestinationChangedListener
            val entryId = currentEntry.id
            val direction =
                if (entryId in seenEntryIds) {
                    NavigationDirection.Pop
                } else {
                    NavigationDirection.Push
                }
            seenEntryIds.add(entryId)

            val rawStack =
                controller.currentBackStack.value
                    .filter { it.destination !is NavGraph }
                    .mapNotNull { entry ->
                        entry.destination.route?.let { route ->
                            Pair(
                                entry.id,
                                StackEntry(
                                    route = route,
                                    arguments = bundleToStringMap(entry.arguments)
                                )
                            )
                        }
                    }

            val route = destination.route ?: return@OnDestinationChangedListener
            val args = bundleToStringMap(arguments)

            val truncated = rawStack.dropLastWhile { (id, _) -> id != entryId }
            val fullStack =
                (
                    truncated.ifEmpty {
                        rawStack + Pair(entryId, StackEntry(route = route, arguments = args))
                    }
                ).map { (_, entry) -> entry }

            observer.onNavigated(
                NavigationEntry(
                    id = Uuid.random().toString(),
                    route = route,
                    direction = direction,
                    arguments = args,
                    timestamp = currentTimeMs()
                ),
                fullStack
            )
        }

    init {
        navController.addOnDestinationChangedListener(listener)
    }

    /**
     * Detaches the listener from the [NavController].
     *
     * Call this when the observer is no longer needed to avoid memory leaks.
     */
    fun dispose() {
        navController.removeOnDestinationChangedListener(listener)
    }
}
