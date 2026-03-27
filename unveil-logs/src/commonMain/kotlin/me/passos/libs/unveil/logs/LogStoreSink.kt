package me.passos.libs.unveil.logs

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
private fun newEntryId() = Uuid.random().toString()

internal class LogStoreSink(
    private val store: LogStore
) : LogSink {
    override fun onLog(
        level: LogLevel,
        tag: String,
        message: String,
        error: String?
    ) {
        store.add(
            LogEntry(
                id = newEntryId(),
                level = level,
                tag = tag,
                message = message,
                timestamp = currentTimeMs(),
                error = error
            )
        )
    }
}
