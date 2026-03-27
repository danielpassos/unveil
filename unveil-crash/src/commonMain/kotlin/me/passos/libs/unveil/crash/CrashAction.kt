package me.passos.libs.unveil.crash

/**
 * Represents a crash scenario that can be triggered on demand.
 *
 * Each action encapsulates a specific failure mode that, when [trigger]
 * is invoked, will terminate the process immediately.
 *
 * @property label Short name identifying the crash type.
 * @property description Human-readable explanation of what the action does and which
 * failure it simulates.
 * @property trigger Invokes the crash. Calling this terminates the process.
 */
data class CrashAction(
    val label: String,
    val description: String,
    val trigger: () -> Unit
)
