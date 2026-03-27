package me.passos.libs.unveil.logs

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

/**
 * In-memory store that holds captured [LogEntry] instances up to [maxEntries].
 *
 * Entries are kept in a [SnapshotStateList] so that Compose UI observes mutations
 * and recomposes automatically. Oldest entries are discarded when [maxEntries] is
 * exceeded.
 *
 * Newest entries appear at index 0.
 *
 * @property maxEntries Maximum number of entries to retain. When exceeded, the oldest
 * entry is removed to make room for the new one. Defaults to 100.
 */
internal class LogStore(
    val maxEntries: Int = 100
) {
    /**
     * Live list of all captured log entries, newest first.
     *
     * Observe this directly in a Composable to react to new log events and clears.
     */
    val entries: SnapshotStateList<LogEntry> = mutableStateListOf()

    /**
     * Removes all captured entries.
     */
    fun clear() {
        entries.clear()
    }

    internal fun add(entry: LogEntry) {
        entries.add(0, entry)
        if (entries.size > maxEntries) {
            entries.removeAt(entries.lastIndex)
        }
    }
}
