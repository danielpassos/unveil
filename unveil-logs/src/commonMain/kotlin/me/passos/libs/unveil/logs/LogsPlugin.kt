package me.passos.libs.unveil.logs

import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.Composable
import me.passos.libs.unveil.QuickAction
import me.passos.libs.unveil.UnveilIcon
import me.passos.libs.unveil.UnveilPlugin
import me.passos.libs.unveil.core.navigation.UnveilPanelScope
import me.passos.libs.unveil.logs.ui.LogPanel

/**
 * Unveil plugin that captures and displays a live stream of log events.
 *
 * Pass [sink] to a logging framework adapter (e.g. KermitLogSink)
 * to forward log events into the panel automatically.
 *
 * Usage:
 * ```kotlin
 * val logsPlugin = LogsPlugin()
 *
 * Unveil.configure {
 *     register(logsPlugin)
 * }
 *
 * // Kermit example — add in debug builds only
 * Logger.addLogWriter(KermitLogSink(logsPlugin))
 * ```
 *
 * @param maxEntries Maximum number of log entries to retain.
 * When exceeded, the oldest entry is discarded. Defaults to 100.
 */
class LogsPlugin(
    maxEntries: Int = 100
) : UnveilPlugin {
    /**
     * Store holding the captured log entries.
     */
    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    val store: LogStore = LogStore(maxEntries)

    /**
     * Receiver that framework adapters call to forward log events.
     */
    val sink: LogSink = LogStoreSink(store)

    override val id: String = "logs"
    override val title: String = "Logs"
    override val icon: UnveilIcon = UnveilIcon.Emoji("📋")

    override val quickActions: List<QuickAction> =
        listOf(
            QuickAction(
                label = "Clear",
                icon = UnveilIcon.Emoji("📋"),
                onToggle = { store.clear() }
            )
        )

    @Composable
    override fun Content(scope: UnveilPanelScope) {
        LogPanel(store = store, scope = scope)
    }
}
