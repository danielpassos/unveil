package me.passos.libs.unveil.core.navigation

import androidx.compose.runtime.Composable

/**
 * No-op implementation of [UnveilBackHandler] for iOS.
 *
 * @param enabled ignored
 * @param onBack ignored
 */
@Suppress("EmptyFunctionBlock")
@Composable
actual fun UnveilBackHandler(
    enabled: Boolean,
    onBack: () -> Unit
) {
}
