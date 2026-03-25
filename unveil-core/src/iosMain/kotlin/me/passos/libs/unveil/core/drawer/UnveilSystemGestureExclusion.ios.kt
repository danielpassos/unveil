package me.passos.libs.unveil.core.drawer

import androidx.compose.runtime.Composable

@Composable
internal actual fun UnveilGestureExclusionEffect(edgeZonePx: Float) {
    // No system gesture conflict on iOS for the right edge.
}
