package me.passos.libs.unveil

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import me.passos.libs.unveil.core.drawer.DrawerController
import me.passos.libs.unveil.core.drawer.UnveilGestureExclusionEffect
import me.passos.libs.unveil.core.drawer.drawerGesture
import me.passos.libs.unveil.core.drawer.rememberDrawerController
import me.passos.libs.unveil.ui.UnveilDrawer
import me.passos.libs.unveil.ui.theme.UnveilTheme

/**
 * Hosts Unveil alongside the application's content.
 *
 * This composable is the entry point for rendering Unveil in a Compose hierarchy.
 * It should wrap the root content of the application so Unveil can be presented
 * on top of the existing UI when enabled.
 *
 * When [enabled] is `true`, Unveil is made available over [content]. When
 * `false`, [content] is rendered normally.
 *
 * @param modifier Modifier applied to the host container.
 * @param enabled Whether Unveil is active for this composition. Defaults to
 * [Unveil.isEnabled].
 * @param content The application content hosted by Unveil.
 */
@Composable
fun UnveilHost(
    modifier: Modifier = Modifier,
    enabled: Boolean = Unveil.isEnabled,
    content: @Composable () -> Unit
) {
    val content = remember { movableContentOf { content() } }

    if (!enabled) {
        content()
        return
    }

    val controller: DrawerController = rememberDrawerController()
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    var hostSize by remember { mutableStateOf(IntSize.Zero) }
    val edgeZonePx = with(density) { 40.dp.toPx() }

    UnveilGestureExclusionEffect(edgeZonePx = edgeZonePx)

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .onSizeChanged { hostSize = it }
                .drawerGesture(
                    controller = controller,
                    drawerWidthPx = hostSize.width * 0.9f,
                    screenWidthPx = hostSize.width.toFloat(),
                    scope = scope,
                    density = density
                ),
        contentAlignment = Alignment.TopStart
    ) {
        content()

        UnveilDrawer(
            controller = controller,
            drawerWidthFraction = 0.9f,
            hostWidthPx = hostSize.width.toFloat()
        )

        // Visible handle tab — floated at the right edge so it peeks out like a tab.
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.CenterEnd
        ) {
            DrawerHandle(
                visible = !controller.isOpen,
                onClick = { scope.launch { controller.open() } }
            )
        }
    }
}

/**
 * Handle used to reveal Unveil.
 *
 * Acts as an entry point to open the Unveil panel when it is not currently visible.
 *
 * @param visible Whether the handle is available for interaction.
 * @param onClick Invoked when the handle is activated.
 */
@Composable
private fun DrawerHandle(
    visible: Boolean,
    onClick: () -> Unit
) {
    val colors = UnveilTheme.colors

    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(initialOffsetX = { it }),
        exit = slideOutHorizontally(targetOffsetX = { it })
    ) {
        Box(
            modifier =
                Modifier
                    .width(22.dp)
                    .height(64.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp))
                    .background(colors.surfaceVariant)
                    .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                repeat(3) {
                    Box(
                        modifier =
                            Modifier
                                .width(4.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(50))
                                .background(colors.primary.copy(alpha = 0.8f))
                    )
                }
            }
        }
    }
}
