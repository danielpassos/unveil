package me.passos.libs.unveil.network

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

/**
 * In-memory store that holds all [NetworkEntry] instances captured during a session.
 *
 * Entries are kept in a [SnapshotStateList] so that Compose UI observes mutations
 * and recomposes automatically. Mutations from any thread are safe the snapshot
 * system serializes writes to the global snapshot.
 *
 * Newest entries appear at index 0.
 */
class NetworkStore {
    /**
     * Live list of all captured network entries, newest first.
     *
     * Observe this directly in a Composable to react to new requests and responses.
     */
    val entries: SnapshotStateList<NetworkEntry> = mutableStateListOf()

    /**
     * Removes all captured entries.
     */
    fun clear() {
        entries.clear()
    }

    internal fun record(request: NetworkRequest) {
        entries.add(0, NetworkEntry(request = request))
    }

    internal fun complete(
        requestId: String,
        response: NetworkResponse
    ) {
        val index = entries.indexOfFirst { it.request.id == requestId }
        if (index != -1) {
            entries[index] = entries[index].copy(response = response)
        }
    }

    internal fun updateBody(requestId: String, body: String?) {
        val index = entries.indexOfFirst { it.request.id == requestId }
        if (index != -1) {
            val current = entries[index]
            if (current.response != null) {
                entries[index] = current.copy(response = current.response.copy(body = body))
            }
        }
    }

    internal fun fail(
        requestId: String,
        message: String
    ) {
        val index = entries.indexOfFirst { it.request.id == requestId }
        if (index != -1) {
            entries[index] = entries[index].copy(error = message)
        }
    }
}
