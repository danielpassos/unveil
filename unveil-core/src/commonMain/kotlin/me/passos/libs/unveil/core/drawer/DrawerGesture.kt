package me.passos.libs.unveil.core.drawer

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Width of the right-edge activation zone.
 *
 * A drag starting within this distance from the right edge
 * opens the drawer when it is closed.
 * Matches the region excluded from system back-gesture
 * interception via [UnveilGestureExclusionEffect].
 */
private val EDGE_ZONE_DP = 40.dp

/**
 * Adds gesture handling for opening and closing the Unveil drawer.
 *
 * This modifier enables interaction with the drawer through horizontal drag
 * gestures, coordinating updates to the drawer state based on user input.
 *
 * @param controller Controller that manages the drawer state.
 * @param drawerWidthPx Width of the drawer in pixels.
 * @param screenWidthPx Width of the available screen area in pixels.
 * @param scope Coroutine scope used to perform drawer state updates.
 * @param density Density used to resolve gesture-related dimensions.
 *
 * @return A [Modifier] that handles drawer gestures.
 */
internal fun Modifier.drawerGesture(
    controller: DrawerController,
    drawerWidthPx: Float,
    screenWidthPx: Float,
    scope: CoroutineScope,
    density: Density
): Modifier =
    this.pointerInput(controller, drawerWidthPx) {
        val edgeZonePx = with(density) { EDGE_ZONE_DP.toPx() }
        var isTracking = false
        var totalDrag = 0f

        detectHorizontalDragGestures(
            onDragStart = { offset ->
                val distanceFromRightEdge = screenWidthPx - offset.x
                // Track this gesture if the drawer is already open (so the user can drag
                // it anywhere to close), or if the drag started in the edge zone.
                isTracking = controller.isOpen || distanceFromRightEdge <= edgeZonePx
                totalDrag = 0f
            },
            onHorizontalDrag = { _, dragAmount ->
                if (!isTracking) return@detectHorizontalDragGestures
                totalDrag += dragAmount
                // A leftward drag (negative dragAmount) increases the visible fraction;
                // a rightward drag decreases it. The fraction is clamped to [0, 1].
                val newFraction =
                    (controller.translationXFraction.value - dragAmount / drawerWidthPx)
                        .coerceIn(0f, 1f)
                scope.launch { controller.snapTo(newFraction) }
            },
            onDragEnd = {
                if (!isTracking) return@detectHorizontalDragGestures
                scope.launch {
                    if (controller.translationXFraction.value > 0.4f) {
                        controller.open()
                    } else {
                        controller.close()
                        // Reset to the plugin list so the next open is always fresh.
                        controller.resetToPluginList()
                    }
                }
            },
            onDragCancel = {
                if (!isTracking) return@detectHorizontalDragGestures
                scope.launch { controller.close() }
            }
        )
    }
