package me.passos.libs.unveil.crash

import androidx.compose.runtime.Composable
import me.passos.libs.unveil.UnveilIcon
import me.passos.libs.unveil.UnveilPlugin
import me.passos.libs.unveil.core.navigation.UnveilPanelScope
import me.passos.libs.unveil.crash.ui.CrashPanel

/**
 * Unveil plugin that provides on-demand crash simulation for QA and debug builds.
 *
 * Exposes a set of [CrashAction] entries covering the most common failure modes:
 * unhandled exception, stack overflow, null dereference, out of memory, and
 * main thread hang. Every action terminates the process immediately after a
 * confirmation step in the UI.
 *
 * Install only in debug or QA builds — never in production.
 *
 * Usage:
 * ```kotlin
 * Unveil.configure {
 *     register(CrashPlugin())
 * }
 * ```
 */
class CrashPlugin : UnveilPlugin {
    internal val actions: List<CrashAction> =
        listOf(
            CrashAction(
                label = "Unhandled Exception",
                description = "Throws an uncaught RuntimeException on the current thread.",
                trigger = CrashSimulator::throwUnhandledException
            ),
            CrashAction(
                label = "Stack Overflow",
                description = "Triggers a StackOverflowError via infinite recursion.",
                trigger = CrashSimulator::causeStackOverflow
            ),
            CrashAction(
                label = "Null Dereference",
                description = "Forces a NullPointerException by dereferencing null.",
                trigger = CrashSimulator::causeNullDereference
            ),
            CrashAction(
                label = "Out of Memory",
                description = "Allocates memory until the process is killed by the OS.",
                trigger = CrashSimulator::causeOutOfMemory
            ),
            CrashAction(
                label = "Main Thread Hang",
                description = "Blocks the main thread to trigger an ANR on Android or a watchdog kill on iOS.",
                trigger = CrashSimulator::hangMainThread
            )
        )

    override val id: String = "crash"
    override val title: String = "Crash"
    override val icon: UnveilIcon = UnveilIcon.Emoji("💥")

    @Composable
    override fun Content(scope: UnveilPanelScope) {
        CrashPanel(actions = actions)
    }
}
