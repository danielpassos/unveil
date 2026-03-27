package me.passos.libs.unveil.logs

/**
 * Returns the current wall-clock time in epoch milliseconds (UTC).
 *
 * Implemented per platform using the native time API.
 */
internal expect fun currentTimeMs(): Long
