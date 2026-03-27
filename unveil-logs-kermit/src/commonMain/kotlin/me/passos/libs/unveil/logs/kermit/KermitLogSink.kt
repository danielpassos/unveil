package me.passos.libs.unveil.logs.kermit

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Severity
import me.passos.libs.unveil.logs.LogLevel
import me.passos.libs.unveil.logs.LogsPlugin

/**
 * Kermit [LogWriter] that forwards log events to an Unveil [LogsPlugin].
 *
 * Attach this writer to Kermit's logger during app initialization, in debug or
 * non-production builds only. Existing log call sites require no changes.
 *
 * Usage:
 * ```kotlin
 * val logsPlugin = LogsPlugin()
 *
 * Unveil.configure {
 *     register(logsPlugin)
 * }
 *
 * // Add in debug builds only — this is the only change to your logging setup
 * Logger.addLogWriter(KermitLogSink(logsPlugin))
 * ```
 *
 * @param plugin The [LogsPlugin] instance that will receive all forwarded events.
 */
class KermitLogSink(
    private val plugin: LogsPlugin
) : LogWriter() {
    override fun log(
        severity: Severity,
        message: String,
        tag: String,
        throwable: Throwable?
    ) {
        plugin.sink.onLog(
            level = severity.toLogLevel(),
            tag = tag,
            message = message,
            error = throwable?.message
        )
    }
}

private fun Severity.toLogLevel(): LogLevel =
    when (this) {
        Severity.Verbose -> LogLevel.Verbose
        Severity.Debug -> LogLevel.Debug
        Severity.Info -> LogLevel.Info
        Severity.Warn -> LogLevel.Warn
        Severity.Error -> LogLevel.Error
        Severity.Assert -> LogLevel.Assert
    }
