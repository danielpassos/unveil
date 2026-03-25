package me.passos.libs.unveil.sample

import androidx.compose.runtime.Composable
import me.passos.libs.unveil.QuickAction
import me.passos.libs.unveil.UnveilIcon
import me.passos.libs.unveil.UnveilPlugin
import me.passos.libs.unveil.core.navigation.UnveilPanelScope
import me.passos.libs.unveil.ui.components.UnveilText

/**
 * A sample [UnveilPlugin] that demonstrates how to implement a custom plugin.
 */
class CustomBoxPlugin : UnveilPlugin {
    override val id = "box_plugin"
    override val title = "Box Plugin"
    override val icon = UnveilIcon.Emoji("📦")

    override val quickActions =
        listOf(
            QuickAction(
                label = "Notes",
                icon = UnveilIcon.Emoji("🗓️️"),
                isActive = false
            ) {
                println("Notes...")
            }
        )

    @Composable
    override fun Content(scope: UnveilPanelScope) {
        UnveilText("My box plugin content!")
    }
}
