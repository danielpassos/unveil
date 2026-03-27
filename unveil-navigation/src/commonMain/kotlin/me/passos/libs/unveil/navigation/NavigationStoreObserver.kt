package me.passos.libs.unveil.navigation

/**
 * Internal [NavigationObserver] that forwards every navigation event into [NavigationStore].
 */
internal class NavigationStoreObserver(private val store: NavigationStore) : NavigationObserver {

    override fun onNavigated(entry: NavigationEntry, fullStack: List<StackEntry>) {
        store.record(entry, fullStack)
    }
}
