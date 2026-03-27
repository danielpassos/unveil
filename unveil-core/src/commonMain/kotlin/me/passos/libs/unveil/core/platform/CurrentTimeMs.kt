package me.passos.libs.unveil.core.platform

/**
 * Returns the current wall-clock time in epoch milliseconds (UTC).
 *
 * Implemented per platform using the native time API.
 */
expect fun currentTimeMs(): Long
