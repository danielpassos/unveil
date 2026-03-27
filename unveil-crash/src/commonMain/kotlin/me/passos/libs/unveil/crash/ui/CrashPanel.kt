package me.passos.libs.unveil.crash.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.passos.libs.unveil.crash.CrashAction
import me.passos.libs.unveil.crash.resources.Res
import me.passos.libs.unveil.crash.resources.crash_button_cancel
import me.passos.libs.unveil.crash.resources.crash_button_confirm
import me.passos.libs.unveil.crash.resources.crash_button_trigger
import me.passos.libs.unveil.crash.resources.crash_dialog_message
import me.passos.libs.unveil.crash.resources.crash_section_header
import me.passos.libs.unveil.ui.components.UnveilButton
import me.passos.libs.unveil.ui.components.UnveilSectionHeader
import me.passos.libs.unveil.ui.theme.UnveilTheme
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun CrashPanel(actions: List<CrashAction>) {
    var pendingAction by remember { mutableStateOf<CrashAction?>(null) }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
    ) {
        UnveilSectionHeader(title = stringResource(Res.string.crash_section_header))

        actions.forEach { action ->
            CrashActionRow(
                action = action,
                onTrigger = { pendingAction = action }
            )
        }
    }

    val current = pendingAction
    if (current != null) {
        AlertDialog(
            onDismissRequest = { pendingAction = null },
            title = { Text(current.label) },
            text = { Text(stringResource(Res.string.crash_dialog_message)) },
            confirmButton = {
                TextButton(onClick = { current.trigger() }) {
                    Text(text = stringResource(Res.string.crash_button_confirm), color = UnveilTheme.colors.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingAction = null }) {
                    Text(stringResource(Res.string.crash_button_cancel))
                }
            }
        )
    }
}

@Composable
private fun CrashActionRow(
    action: CrashAction,
    onTrigger: () -> Unit
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = action.label,
                style = UnveilTheme.typography.body,
                color = UnveilTheme.colors.onSurface
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = action.description,
                style = UnveilTheme.typography.bodySmall,
                color = UnveilTheme.colors.onSurfaceMuted
            )
        }
        UnveilButton(
            label = stringResource(Res.string.crash_button_trigger),
            onClick = onTrigger
        )
    }
}
