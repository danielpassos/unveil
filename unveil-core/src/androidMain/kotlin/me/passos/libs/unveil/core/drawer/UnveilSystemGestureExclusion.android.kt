package me.passos.libs.unveil.core.drawer

import android.graphics.Rect
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView

@Composable
internal actual fun UnveilGestureExclusionEffect(edgeZonePx: Float) {
    val view = LocalView.current

    // SideEffect runs after every successful composition on the main thread.
    // Using view.width/height directly guarantees we always have the current dimensions,
    // even after rotation or window resize.
    SideEffect {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val root = view.rootView
            if (root.width > 0) {
                root.systemGestureExclusionRects =
                    listOf(
                        Rect(
                            (root.width - edgeZonePx).toInt().coerceAtLeast(0),
                            0,
                            root.width,
                            root.height
                        )
                    )
            }
        }
    }
}
