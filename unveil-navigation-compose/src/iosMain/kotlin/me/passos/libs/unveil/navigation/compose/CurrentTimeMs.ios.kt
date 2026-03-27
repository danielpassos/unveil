package me.passos.libs.unveil.navigation.compose

import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970

@Suppress("MagicNumber")
internal actual fun currentTimeMs(): Long =
    (NSDate().timeIntervalSince1970 * 1000).toLong()
