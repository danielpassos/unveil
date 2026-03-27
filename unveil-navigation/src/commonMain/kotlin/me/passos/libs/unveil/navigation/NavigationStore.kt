package me.passos.libs.unveil.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

/**
 * In-memory store that holds the live back stack and navigation history.
 *
 * Both collections are backed by [SnapshotStateList] so that Compose UI observes mutations
 * and recomposes automatically. History entries are kept newest-first and trimmed to
 * [maxHistoryEntries] when exceeded.
 *
 * @property maxHistoryEntries Maximum number of history entries to retain. When exceeded,
 * the oldest entry is discarded. Defaults to 50.
 */
internal class NavigationStore(
    val maxHistoryEntries: Int = 50
) {
    /**
     * Live representation of the current back stack, ordered from bottom to top.
     *
     * Replaced in full on each navigation event.
     */
    val stack: SnapshotStateList<StackEntry> = mutableStateListOf()

    /**
     * Live list of all captured navigation events, newest first.
     *
     * Observe this directly in a Composable to react to new events and clears.
     */
    val history: SnapshotStateList<NavigationEntry> = mutableStateListOf()

    /**
     * Records a navigation event and updates the live back stack.
     *
     * The stack is replaced with [fullStack] and [entry] is prepended to history.
     * If history exceeds [maxHistoryEntries], the oldest entry is removed.
     */
    fun record(
        entry: NavigationEntry,
        fullStack: List<StackEntry>
    ) {
        stack.clear()
        stack.addAll(fullStack)
        history.add(0, entry)
        if (history.size > maxHistoryEntries) history.removeAt(history.lastIndex)
    }

    /**
     * Removes all captured history entries. The live stack is not affected.
     */
    fun clearHistory() {
        history.clear()
    }
}
