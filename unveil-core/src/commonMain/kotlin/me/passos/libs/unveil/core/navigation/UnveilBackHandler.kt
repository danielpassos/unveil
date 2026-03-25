package me.passos.libs.unveil.core.navigation

import androidx.compose.runtime.Composable

/**
 * Platform-agnostic back-press handler.
 *
 * On Android, delegates to `androidx.activity.compose.BackHandler`.
 * On iOS, this is a no-op back navigation within the drawer
 * that is handled solely by the drawer's own gesture and
 * back-button UI.
 *
 * @param enabled Whether the handler is active.
 * @param onBack Callback invoked when a back event is received.
 */
@Composable
expect fun UnveilBackHandler(
    enabled: Boolean,
    onBack: () -> Unit
)
