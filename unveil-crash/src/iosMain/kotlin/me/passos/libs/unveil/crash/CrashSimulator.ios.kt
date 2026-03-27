package me.passos.libs.unveil.crash

import platform.Foundation.NSThread

internal actual object CrashSimulator {
    actual fun throwUnhandledException() {
        @Suppress("TooGenericExceptionThrown")
        throw RuntimeException("Crash triggered via Unveil")
    }

    actual fun causeStackOverflow() {
        causeStackOverflow()
    }

    actual fun causeNullDereference() {
        @Suppress("CAST_NEVER_SUCCEEDS")
        (null as String).length
    }

    @Suppress("InfiniteRecursion")
    actual fun causeOutOfMemory() {
        val chunks = mutableListOf<ByteArray>()
        @Suppress("MagicNumber")
        while (true) {
            chunks.add(ByteArray(1024 * 1024))
        }
    }

    actual fun hangMainThread() {
        @Suppress("MagicNumber")
        NSThread.sleepForTimeInterval(60.0)
    }
}
