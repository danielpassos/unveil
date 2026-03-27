package me.passos.libs.unveil.logs

import platform.Foundation.NSDate
import platform.Foundation.date
import platform.Foundation.timeIntervalSince1970

internal actual fun currentTimeMs(): Long = (NSDate.date().timeIntervalSince1970 * 1000).toLong()
