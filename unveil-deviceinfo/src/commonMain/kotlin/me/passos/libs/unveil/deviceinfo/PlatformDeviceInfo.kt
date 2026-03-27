package me.passos.libs.unveil.deviceinfo

/**
 * Platform-specific device and display information.
 *
 * Each platform target provides its own `actual` implementation backed by the
 * corresponding native APIs. Values are read once at access time and are not
 * updated after construction.
 */
internal expect object PlatformDeviceInfo {
    val deviceModel: String
    val manufacturer: String
    val osVersion: String
    val screenResolution: String
    val screenDensity: String
    val locale: String
    val timezone: String
}
