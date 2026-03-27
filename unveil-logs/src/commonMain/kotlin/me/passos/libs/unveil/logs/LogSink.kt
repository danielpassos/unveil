package me.passos.libs.unveil.logs

/**
 * Receiver that logging framework adapters call to forward log events into Unveil.
 *
 * Implement this interface in an adapter module (e.g. `unveil-logs-kermit`) and
 * forward each log event by calling [onLog]. The adapter is responsible for mapping
 * framework-specific severity levels and metadata to the parameters below.
 *
 * Get a ready-to-use implementation from [LogsPlugin.sink].
 */
interface LogSink {
    /**
     * Records a single log event.
     *
     * @param level Severity of the event.
     * @param tag Source tag from the originating logger.
     * @param message Human-readable log message.
     * @param error Optional throwable or error message. Null when no error is associated.
     */
    fun onLog(
        level: LogLevel,
        tag: String,
        message: String,
        error: String? = null
    )
}
