package me.passos.libs.unveil.navigation.compose

import android.os.Bundle

internal actual fun bundleToStringMap(bundle: Any?): Map<String, String> =
    (bundle as? Bundle)?.keySet()?.associateWith { key -> bundle.get(key)?.toString() ?: "" }
        ?: emptyMap()
