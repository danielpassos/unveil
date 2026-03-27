package me.passos.libs.unveil.crash

/**
 * Platform-specific crash triggers used by [CrashPlugin].
 *
 * Each function is expected to terminate the process immediately when called.
 * Implementations are provided per platform via expect/actual.
 */
internal expect object CrashSimulator {
    fun throwUnhandledException()

    fun causeStackOverflow()

    fun causeNullDereference()

    fun causeOutOfMemory()

    fun hangMainThread()
}
