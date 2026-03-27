package me.passos.libs.unveil.deviceinfo

import androidx.compose.runtime.Composable
import me.passos.libs.unveil.UnveilIcon
import me.passos.libs.unveil.UnveilPlugin
import me.passos.libs.unveil.core.navigation.UnveilPanelScope
import me.passos.libs.unveil.deviceinfo.ui.DeviceInfoPanel

/**
 * Unveil plugin that displays a snapshot of app and device information.
 *
 * App-specific fields must be provided by the host app at construction time.
 * Platform-level details (device model, OS version, screen, locale, timezone)
 * are read from the OS automatically via platform-specific implementations.
 *
 * Usage:
 * ```kotlin
 * Unveil.configure {
 *     register(
 *         DeviceInfoPlugin(
 *             appVersionName = BuildConfig.VERSION_NAME,
 *             appBuildNumber = BuildConfig.VERSION_CODE.toString(),
 *             buildVariant = BuildConfig.BUILD_TYPE,
 *             environment = "staging"
 *         )
 *     )
 * }
 * ```
 *
 * @param appVersionName Human-readable version string (e.g. "1.2.3").
 * @param appBuildNumber Monotonically increasing build identifier (e.g. "42").
 * @param buildVariant Gradle build type or equivalent (e.g. "debug", "release").
 * @param environment Logical deployment environment (e.g. "staging"). Optional.
 */
class DeviceInfoPlugin(
    appVersionName: String,
    appBuildNumber: String,
    buildVariant: String,
    environment: String? = null
) : UnveilPlugin {
    /**
     * Captured snapshot of app and device information.
     *
     * Platform fields are populated from [PlatformDeviceInfo] at construction time.
     */
    val deviceInfo: DeviceInfo =
        DeviceInfo(
            appVersionName = appVersionName,
            appBuildNumber = appBuildNumber,
            buildVariant = buildVariant,
            environment = environment,
            deviceModel = PlatformDeviceInfo.deviceModel,
            manufacturer = PlatformDeviceInfo.manufacturer,
            osVersion = PlatformDeviceInfo.osVersion,
            screenResolution = PlatformDeviceInfo.screenResolution,
            screenDensity = PlatformDeviceInfo.screenDensity,
            locale = PlatformDeviceInfo.locale,
            timezone = PlatformDeviceInfo.timezone
        )

    override val id: String = "device_info"
    override val title: String = "Device Info"
    override val icon: UnveilIcon = UnveilIcon.Emoji("📱")

    @Composable
    override fun Content(scope: UnveilPanelScope) {
        DeviceInfoPanel(deviceInfo = deviceInfo)
    }
}
