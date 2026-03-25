package me.passos.libs.unveil.core.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

/**
 * Android-specific implementation of [UnveilBackHandler].
 *
 * @param enabled whether the back handler is enabled
 * @param onBack callback invoked when the back button is pressed
 */
@Composable
actual fun UnveilBackHandler(
    enabled: Boolean,
    onBack: () -> Unit
) {
    BackHandler(enabled = enabled, onBack = onBack)
}
