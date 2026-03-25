package me.passos.libs.unveil

import androidx.compose.runtime.Composable
import me.passos.libs.unveil.core.navigation.UnveilPanelScope

internal class FakeUnveilPlugin(
    override val id: String = "fake_plugin",
    override val title: String = "Fake Plugin",
    override val icon: UnveilIcon = UnveilIcon.Emoji("🔧"),
) : UnveilPlugin {
    @Composable
    override fun Content(scope: UnveilPanelScope) = Unit
}
