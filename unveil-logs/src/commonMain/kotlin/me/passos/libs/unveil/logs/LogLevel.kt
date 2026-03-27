package me.passos.libs.unveil.logs

/**
 * Represents the severity of a log entry.
 *
 * Defines a standardized set of levels used to classify logs by importance,
 * enabling filtering and analysis within Unveil.
 */
enum class LogLevel {
    /** Detailed information intended for low-level debugging. */
    Verbose,

    /** Diagnostic information useful during development. */
    Debug,

    /** General information about application behavior. */
    Info,

    /** Indicates a potential issue or unexpected behavior. */
    Warn,

    /** Indicates a failure that affects a specific operation. */
    Error,

    /** Indicates a critical failure that may terminate the application. */
    Assert
}
