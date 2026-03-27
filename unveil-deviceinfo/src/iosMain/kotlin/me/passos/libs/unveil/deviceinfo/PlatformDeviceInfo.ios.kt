@file:OptIn(ExperimentalForeignApi::class)

package me.passos.libs.unveil.deviceinfo

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.Foundation.NSLocale
import platform.Foundation.NSTimeZone
import platform.Foundation.currentLocale
import platform.Foundation.localTimeZone
import platform.Foundation.localeIdentifier
import platform.UIKit.UIDevice
import platform.UIKit.UIScreen

internal actual object PlatformDeviceInfo {
    actual val deviceModel: String
        get() = UIDevice.currentDevice.model

    actual val manufacturer: String
        get() = "Apple"

    actual val osVersion: String
        get() = "iOS ${UIDevice.currentDevice.systemVersion}"

    actual val screenResolution: String
        get() =
            UIScreen.mainScreen.nativeBounds.useContents {
                "${size.width.toInt()} × ${size.height.toInt()}"
            }

    actual val screenDensity: String
        get() = "@${UIScreen.mainScreen.nativeScale.toInt()}x"

    actual val locale: String
        get() = NSLocale.currentLocale.localeIdentifier

    actual val timezone: String
        get() = NSTimeZone.localTimeZone.name
}
