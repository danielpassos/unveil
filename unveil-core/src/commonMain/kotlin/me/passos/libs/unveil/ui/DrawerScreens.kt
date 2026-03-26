package me.passos.libs.unveil.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import me.passos.libs.unveil.QuickAction
import me.passos.libs.unveil.UnveilIcon
import me.passos.libs.unveil.UnveilPlugin
import me.passos.libs.unveil.ui.theme.UnveilTheme

/**
 * Displays the list of available plugins in Unveil.
 *
 * Serves as the entry point of the Unveil navigation, presenting all registered
 * plugins and allowing selection of a plugin to navigate into its content.
 *
 * @param plugins List of plugins to be displayed.
 * @param onPluginSelect Invoked when a plugin is selected.
 * @param modifier Modifier applied to the screen.
 */
@Composable
internal fun PluginListScreen(
    plugins: List<UnveilPlugin>,
    onPluginSelect: (UnveilPlugin) -> Unit,
    modifier: Modifier = Modifier
) {
    val allQuickActions = plugins.flatMap { it.quickActions }

    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(top = 48.dp, bottom = 32.dp)
    ) {
        item {
            PluginListHeader()
        }

        if (allQuickActions.isNotEmpty()) {
            item {
                QuickActionsBar(quickActions = allQuickActions)
                Spacer(Modifier.height(8.dp))
            }
        }

        item {
            UnveilDivider()
        }

        items(plugins, key = { it.id }) { plugin ->
            PluginListItem(
                plugin = plugin,
                onClick = { onPluginSelect(plugin) }
            )
            UnveilDivider()
        }
    }
}

/**
 * Displays the header for the plugin list screen.
 *
 * @param modifier Modifier applied to the header.
 */
@Composable
private fun PluginListHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        BasicText(
            text = "Unveil",
            style = UnveilTheme.typography.drawerTitle.copy(color = UnveilTheme.colors.onSurface)
        )
        Spacer(Modifier.height(2.dp))
        BasicText(
            text = "Developer Tools",
            style = UnveilTheme.typography.label.copy(color = UnveilTheme.colors.onSurfaceMuted)
        )
    }
}

/**
 * Displays a selectable item representing a plugin.
 *
 * Used within the plugin list to allow navigation to a plugin's page.
 *
 * @param plugin The plugin represented by this item.
 * @param onClick Invoked when the item is selected.
 * @param modifier Modifier applied to the item.
 */
@Composable
internal fun PluginListItem(
    plugin: UnveilPlugin,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(
                    if (isPressed) UnveilTheme.colors.surfaceVariant else Color.Transparent
                ).clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
                .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PluginIconView(icon = plugin.icon)
        Spacer(Modifier.width(12.dp))
        BasicText(
            text = plugin.title,
            style = UnveilTheme.typography.body.copy(color = UnveilTheme.colors.onSurface),
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.width(8.dp))
        BasicText(
            text = "›",
            style = UnveilTheme.typography.body.copy(color = UnveilTheme.colors.onSurfaceMuted)
        )
    }
}

/**
 * A back-navigation header shown on [DrawerPage.PluginPage][me.passos.libs.unveil.core.drawer.DrawerPage.PluginPage]
 * and [DrawerPage.SubPage][me.passos.libs.unveil.core.drawer.DrawerPage.SubPage] screens.
 *
 * Contains a tappable back button and the screen title. Followed by a [UnveilDivider].
 *
 * @param title  The screen title to display.
 * @param onBack Callback invoked when the back button is tapped.
 * @param modifier Modifier to apply to this composable.
 */
@Composable
internal fun DrawerBackHeader(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isPressed) {
                                UnveilTheme.colors.primary.copy(alpha = 0.15f)
                            } else {
                                Color.Transparent
                            }
                        ).clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = onBack
                        ).heightIn(min = 52.dp)
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                BasicText(
                    text = "‹",
                    style = UnveilTheme.typography.drawerTitle.copy(color = UnveilTheme.colors.primary)
                )
            }
            BasicText(
                text = title,
                style = UnveilTheme.typography.drawerTitle.copy(color = UnveilTheme.colors.onSurface),
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        UnveilDivider()
    }
}

/**
 * Displays the set of quick actions provided by plugins.
 *
 * Aggregates and presents actions that can be triggered without navigating
 * into a specific plugin, allowing direct interaction with Unveil features.
 *
 * @param quickActions List of actions to be displayed.
 * @param modifier Modifier applied to the container.
 */
@Composable
internal fun QuickActionsBar(
    quickActions: List<QuickAction>,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(quickActions) { action ->
            UnveilChip(action = action)
        }
    }
}

/**
 * Represents a quick action as an interactive element.
 *
 * Used within the quick actions bar to trigger a plugin-provided action.
 *
 * @param action The action associated with this element.
 * @param modifier Modifier applied to the component.
 */
@Composable
internal fun UnveilChip(
    action: QuickAction,
    modifier: Modifier = Modifier
) {
    val colors = UnveilTheme.colors
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val backgroundColor =
        when {
            isPressed && action.isActive -> colors.chipActive.copy(alpha = 0.75f)
            isPressed -> colors.surfaceVariant
            action.isActive -> colors.chipActive
            else -> colors.chipIdle
        }
    val textColor = if (action.isActive) colors.chipOnActive else colors.chipOnIdle

    Row(
        modifier =
            modifier
                .clip(RoundedCornerShape(50))
                .background(backgroundColor)
                .clickable(interactionSource = interactionSource, indication = null) {
                    action.onToggle(!action.isActive)
                }.padding(horizontal = 12.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        action.icon?.let { PluginIconView(icon = it) }
        BasicText(
            text = action.label,
            style = UnveilTheme.typography.chip.copy(color = textColor)
        )
    }
}

/**
 * Separates content within Unveil.
 *
 * Used to provide visual grouping between elements.
 *
 * @param modifier Modifier applied to the divider.
 */
@Composable
internal fun UnveilDivider(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(UnveilTheme.colors.divider)
    )
}

/**
 * Displays the icon associated with a plugin.
 *
 * Supports different icon representations and resolves them into a form that
 * can be rendered within the Unveil UI.
 *
 * @param icon Icon to be displayed.
 * @param modifier Modifier applied to the icon.
 */
@Composable
internal fun PluginIconView(
    icon: UnveilIcon,
    modifier: Modifier = Modifier
) {
    when (icon) {
        is UnveilIcon.Builtin -> {
            // TODO: replace placeholder with actual vector asset rendering
            BasicText(
                text = "▪",
                style = UnveilTheme.typography.body.copy(color = UnveilTheme.colors.onSurfaceMuted),
                modifier = modifier
            )
        }

        is UnveilIcon.Emoji -> {
            BasicText(
                text = icon.character,
                style = UnveilTheme.typography.body.copy(color = UnveilTheme.colors.onSurface),
                modifier = modifier
            )
        }
    }
}
