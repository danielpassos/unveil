package me.passos.libs.unveil.deviceinfo

import android.content.res.Resources
import android.os.Build
import java.util.Locale
import java.util.TimeZone

internal actual object PlatformDeviceInfo {
    actual val deviceModel: String
        get() = Build.MODEL

    actual val manufacturer: String
        get() = Build.MANUFACTURER

    actual val osVersion: String
        get() = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"

    actual val screenResolution: String
        get() {
            val dm = Resources.getSystem().displayMetrics
            return "${dm.widthPixels} × ${dm.heightPixels}"
        }

    @Suppress("MagicNumber")
    actual val screenDensity: String
        get() {
            val dpi = Resources.getSystem().displayMetrics.densityDpi
            val bucket =
                when {
                    dpi <= 120 -> "ldpi"
                    dpi <= 160 -> "mdpi"
                    dpi <= 240 -> "hdpi"
                    dpi <= 320 -> "xhdpi"
                    dpi <= 480 -> "xxhdpi"
                    else -> "xxxhdpi"
                }
            return "$dpi dpi ($bucket)"
        }

    actual val locale: String
        get() = Locale.getDefault().toLanguageTag()

    actual val timezone: String
        get() = TimeZone.getDefault().id
}
