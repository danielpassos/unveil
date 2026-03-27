package me.passos.libs.unveil.crash

internal actual object CrashSimulator {
    actual fun throwUnhandledException() = Unit
    actual fun causeStackOverflow() = Unit
    actual fun causeNullDereference() = Unit
    actual fun causeOutOfMemory() = Unit
    actual fun hangMainThread() = Unit
}
