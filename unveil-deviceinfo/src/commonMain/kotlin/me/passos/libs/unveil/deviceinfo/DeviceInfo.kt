package me.passos.libs.unveil.deviceinfo

/**
 * Snapshot of app and device information captured when [DeviceInfoPlugin] is constructed.
 *
 * App-specific fields ([appVersionName], [appBuildNumber], [buildVariant], [environment])
 * are provided by the host app. Platform fields are read from the OS automatically.
 *
 * @property appVersionName Human-readable version string (e.g. "1.2.3").
 * @property appBuildNumber Monotonically increasing build identifier (e.g. "42").
 * @property buildVariant Gradle build type or equivalent (e.g. "debug", "release").
 * @property environment Logical deployment environment (e.g. "staging"). Null when not set.
 * @property deviceModel Hardware model name reported by the OS.
 * @property manufacturer Device manufacturer name.
 * @property osVersion Full OS version string including API level or system version.
 * @property screenResolution Physical pixel dimensions of the primary display.
 * @property screenDensity Screen density in dpi with bucket classification (Android) or scale factor (iOS).
 * @property locale BCP 47 locale tag active at the time of capture (e.g. "en-US").
 * @property timezone IANA time zone identifier active at the time of capture (e.g. "America/New_York").
 */
data class DeviceInfo(
    val appVersionName: String,
    val appBuildNumber: String,
    val buildVariant: String,
    val environment: String?,
    val deviceModel: String,
    val manufacturer: String,
    val osVersion: String,
    val screenResolution: String,
    val screenDensity: String,
    val locale: String,
    val timezone: String,
)
