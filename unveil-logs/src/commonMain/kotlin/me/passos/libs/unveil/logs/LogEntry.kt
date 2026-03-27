package me.passos.libs.unveil.logs

/**
 * A single captured log event.
 *
 * @property id Stable unique identifier used as a Compose list key.
 * @property level Severity level of the event.
 * @property tag Source tag identifying the component that emitted the log.
 * @property message Human-readable log message.
 * @property timestamp Wall-clock time of capture in epoch milliseconds (UTC).
 * @property error Optional error or throwable message associated with the event.
 */
data class LogEntry(
    val id: String,
    val level: LogLevel,
    val tag: String,
    val message: String,
    val timestamp: Long,
    val error: String? = null
)
