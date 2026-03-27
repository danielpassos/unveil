package me.passos.libs.unveil.navigation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.passos.libs.unveil.navigation.NavigationDirection
import me.passos.libs.unveil.navigation.NavigationEntry
import me.passos.libs.unveil.navigation.NavigationStore
import me.passos.libs.unveil.navigation.StackEntry
import me.passos.libs.unveil.navigation.displayRoute
import me.passos.libs.unveil.ui.components.UnveilSectionHeader
import me.passos.libs.unveil.ui.theme.UnveilTheme

@Composable
internal fun NavigationPanel(store: NavigationStore) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
    ) {
        UnveilSectionHeader(title = "Current Stack (${store.stack.size})")

        if (store.stack.isEmpty()) {
            Text(
                text = "No navigation recorded yet.",
                style = UnveilTheme.typography.body,
                color = UnveilTheme.colors.onSurfaceMuted,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        } else {
            store.stack.forEachIndexed { index, entry ->
                StackEntryRow(
                    entry = entry,
                    isCurrent = index == store.stack.lastIndex
                )
            }
        }

        UnveilSectionHeader(title = "History")

        if (store.history.isEmpty()) {
            Text(
                text = "No navigation recorded yet.",
                style = UnveilTheme.typography.body,
                color = UnveilTheme.colors.onSurfaceMuted,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        } else {
            store.history.forEach { entry ->
                HistoryEntryRow(entry = entry)
            }
        }
    }
}

@Composable
private fun StackEntryRow(
    entry: StackEntry,
    isCurrent: Boolean
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "→ ",
            style = UnveilTheme.typography.body,
            color = if (isCurrent) UnveilTheme.colors.onSurface else UnveilTheme.colors.onSurfaceMuted
        )
        Text(
            text = entry.displayRoute(),
            style = UnveilTheme.typography.body,
            color = if (isCurrent) UnveilTheme.colors.onSurface else UnveilTheme.colors.onSurfaceMuted
        )
    }
}

@Composable
private fun HistoryEntryRow(entry: NavigationEntry) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val (arrow, arrowColor) =
            when (entry.direction) {
                NavigationDirection.Push -> "→" to UnveilTheme.colors.success
                NavigationDirection.Pop -> "←" to UnveilTheme.colors.onSurfaceMuted
            }
        Text(
            text = "$arrow ",
            style = UnveilTheme.typography.body,
            color = arrowColor
        )
        Text(
            text = entry.displayRoute(),
            style = UnveilTheme.typography.mono,
            color = UnveilTheme.colors.onSurface,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = entry.timestamp.formatTimestamp(),
            style = UnveilTheme.typography.label,
            color = UnveilTheme.colors.onSurfaceMuted
        )
    }
}

private fun Long.formatTimestamp(): String {
    val ms = this % 1000
    val totalSeconds = this / 1000
    val seconds = totalSeconds % 60
    val minutes = (totalSeconds / 60) % 60
    val hours = (totalSeconds / 3600) % 24
    return "${hours.pad()}:${minutes.pad()}:${seconds.pad()}.${ms.padMs()}"
}

private fun Long.pad() = toString().padStart(2, '0')

private fun Long.padMs() = toString().padStart(3, '0')
