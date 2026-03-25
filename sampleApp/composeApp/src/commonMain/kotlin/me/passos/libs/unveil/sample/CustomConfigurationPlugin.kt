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
class CustomConfigurationPlugin : UnveilPlugin {
    override val id = "config_plugin"
    override val title = "Configuration"
    override val icon = UnveilIcon.Emoji("⚙️")

    override val quickActions =
        listOf(
            QuickAction(
                label = "Reset",
                icon = UnveilIcon.Emoji("♻️"),
                isActive = false
            ) {
                println("Resetting...")
            }
        )

    @Composable
    override fun Content(scope: UnveilPanelScope) {
        UnveilText("My configuration plugin content!")
    }
}
