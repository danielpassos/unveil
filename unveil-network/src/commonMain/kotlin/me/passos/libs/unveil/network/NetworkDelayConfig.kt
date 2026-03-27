package me.passos.libs.unveil.network

/**
 * Read-only view of the artificial response delay configuration.
 *
 * Framework adapters (e.g. the Ktor adapter) read this at response time to determine
 * whether an artificial delay should be applied and for how long.
 *
 * Get an instance from [NetworkPlugin], which implements this interface and keeps
 * its state in sync with the Unveil panel UI.
 */
interface NetworkDelayConfig {
    /**
     * Whether artificial delay is currently active.
     */
    val enabled: Boolean

    /**
     * Duration of the artificial delay in milliseconds. Ignored when [enabled] is false.
     */
    val delayMs: Long
}
