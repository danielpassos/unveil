package me.passos.libs.unveil.core.platform

import platform.Foundation.NSDate
import platform.Foundation.date
import platform.Foundation.timeIntervalSince1970

@Suppress("UndocumentedPublicFunction")
actual fun currentTimeMs(): Long = (NSDate.date().timeIntervalSince1970 * 1000).toLong()
