package me.passos.libs.unveil.network

/**
 * Read-only view of the response status code override configuration.
 *
 * Framework adapters (e.g. the Ktor adapter) read this when recording a response to
 * determine whether the captured status code should be replaced with a fixed value.
 *
 * Get an instance from [NetworkPlugin], which implements this interface and keeps
 * its state in sync with the Unveil panel UI.
 */
interface NetworkStatusOverrideConfig {
    /**
     * Whether the status code override is currently active.
     */
    val enabled: Boolean

    /**
     * The status code to report instead of the real one. Ignored when [enabled] is false.
     */
    val statusCode: Int
}
