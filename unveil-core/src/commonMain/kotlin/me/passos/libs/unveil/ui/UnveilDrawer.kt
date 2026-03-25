package me.passos.libs.unveil.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.passos.libs.unveil.Unveil
import me.passos.libs.unveil.core.drawer.DrawerController
import me.passos.libs.unveil.core.drawer.DrawerPage
import me.passos.libs.unveil.core.navigation.UnveilBackHandler
import me.passos.libs.unveil.ui.theme.UnveilTheme
import me.passos.libs.unveil.ui.theme.UnveilThemeProvider

/**
 * Hosts the Unveil drawer.
 *
 * This composable is responsible for presenting the Unveil overlay and
 * reflecting the current drawer state in the UI. It also handles navigation
 * within the Unveil hierarchy, including back navigation from plugin panels
 * and subpages.
 *
 * @param controller Controller that manages the drawer state and navigation.
 * @param drawerWidthFraction Fraction of the available width used by the drawer.
 * @param hostWidthPx Width of the host content in pixels.
 * @param modifier Modifier applied to the drawer host.
 */
@Composable
internal fun UnveilDrawer(
    controller: DrawerController,
    drawerWidthFraction: Float,
    hostWidthPx: Float,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val drawerWidthPx = hostWidthPx * drawerWidthFraction
    val drawerWidthDp = with(density) { drawerWidthPx.toDp() }

    // Register back handler when inside a plugin panel or sub-page
    val canGoBack = controller.pageStack.size > 1
    UnveilBackHandler(enabled = canGoBack) {
        if (!controller.navigateBack()) {
            scope.launch { controller.close() }
        }
    }

    UnveilThemeProvider {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.TopEnd
        ) {
            // Scrim — tapping it closes the drawer
            if (controller.isOpen) {
                Scrim(scope, controller)
            }

            // Drawer panel
            DrawerContent(drawerWidthDp, drawerWidthPx, controller)
        }
    }
}

/**
 * Intercepts interactions behind the Unveil drawer.
 *
 * When active, it blocks interaction with the underlying content and triggers
 * the closing of the drawer when activated.
 *
 * @param scope Coroutine scope used to perform drawer actions.
 * @param controller Controller used to update the drawer state.
 */
@Composable
private fun Scrim(
    scope: CoroutineScope,
    controller: DrawerController
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(UnveilTheme.colors.scrim)
                .clickable { scope.launch { controller.close() } }
    )
}

/**
 * Renders the content of the Unveil drawer.
 *
 * Responsible for presenting the current screen based on the drawer state
 * and delegating to the appropriate content.
 *
 * @param drawerWidthDp Width of the drawer in dp.
 * @param drawerWidthPx Width of the drawer in pixels.
 * @param controller Controller that manages the drawer state and navigation.
 */
@Composable
private fun DrawerContent(
    drawerWidthDp: Dp,
    drawerWidthPx: Float,
    controller: DrawerController
) {
    val edgeColor = UnveilTheme.colors.primary.copy(alpha = 0.35f)
    Box(
        modifier =
            Modifier
                .width(drawerWidthDp)
                .fillMaxHeight()
                .graphicsLayer {
                    translationX = drawerWidthPx * (1f - controller.translationXFraction.value)
                }.background(UnveilTheme.colors.surface)
                .drawBehind {
                    // Subtle left-edge accent line
                    drawLine(
                        color = edgeColor,
                        start = Offset(0f, 0f),
                        end = Offset(0f, size.height),
                        strokeWidth = 1.5.dp.toPx()
                    )
                }
    ) {
        AnimatedContent(
            targetState = controller.currentPage,
            transitionSpec = {
                val isForward = targetState !is DrawerPage.PluginList
                if (isForward) {
                    slideInHorizontally { it } togetherWith slideOutHorizontally { -it / 3 }
                } else {
                    slideInHorizontally { -it / 3 } togetherWith slideOutHorizontally { it }
                }
            },
            label = "UnveilDrawerNav"
        ) { page ->
            when (page) {
                is DrawerPage.PluginList -> {
                    PluginPage(controller)
                }

                is DrawerPage.PluginPage -> {
                    PluginPage(page, controller)
                }

                is DrawerPage.SubPage -> {
                    SubPage(page, controller)
                }
            }
        }
    }
}

/**
 * Displays the list of registered plugins.
 *
 * Acts as the entry point of the Unveil navigation, allowing selection of a plugin
 * to navigate into its corresponding page.
 *
 * @param controller Controller used to handle navigation actions.
 */
@Composable
private fun PluginPage(controller: DrawerController) {
    PluginListScreen(
        plugins = Unveil.registry.plugins,
        onPluginSelect = { controller.navigateTo(it) }
    )
}

/**
 * Displays the page associated with a plugin.
 *
 * Delegates rendering to the plugin's content and provides the required scope
 * for interaction with the Unveil navigation system.
 *
 * @param page The plugin page to display.
 * @param controller Controller used to manage navigation state.
 */
@Composable
private fun PluginPage(
    page: DrawerPage.PluginPage,
    controller: DrawerController
) {
    Page(
        title = page.plugin.title,
        controller = controller,
        content = { page.plugin.Content(scope = controller.createPanelScope()) }
    )
}

/**
 * Displays a subpage within a plugin.
 *
 * Plugins create subpages to present additional content within the
 * Unveil navigation hierarchy.
 *
 * @param page The subpage to display.
 * @param controller Controller used to manage navigation state.
 */
@Composable
private fun SubPage(
    page: DrawerPage.SubPage,
    controller: DrawerController
) {
    Page(
        title = page.title,
        controller = controller,
        content = { page.content() }
    )
}

/**
 * Provides a common structure for pages within Unveil.
 *
 * Hosts the page title and content and integrates with the
 * navigation system to handle back navigation.
 *
 * @param title Title associated with the page.
 * @param controller Controller used to handle navigation actions.
 * @param content Composable content of the page.
 */
@Composable
private fun Page(
    title: String,
    controller: DrawerController,
    content: @Composable () -> Unit
) {
    Column(
        modifier =
            Modifier.padding(
                top = 48.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp
            )
    ) {
        DrawerBackHeader(
            title = title,
            onBack = { controller.navigateBack() }
        )
        content()
    }
}
