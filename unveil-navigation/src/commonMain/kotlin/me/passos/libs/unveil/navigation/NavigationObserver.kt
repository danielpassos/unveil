package me.passos.libs.unveil.navigation

/**
 * Receiver that navigation framework adapters call to forward navigation events into Unveil.
 *
 * Implement this interface in an adapter module (e.g. `unveil-navigation-jetpack`) and call
 * [onNavigated] for every destination change. The adapter is responsible for mapping
 * framework-specific back stack state to [NavigationEntry] and [StackEntry] instances.
 *
 * Get a ready-to-use implementation from [NavigationPlugin.observer].
 */
interface NavigationObserver {
    /**
     * Records a single navigation event along with the full back stack state at that moment.
     *
     * @param entry The navigation event that just occurred.
     * @param fullStack The complete back stack after the navigation, ordered from bottom to top.
     */
    fun onNavigated(
        entry: NavigationEntry,
        fullStack: List<StackEntry>
    )
}
