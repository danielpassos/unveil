package me.passos.libs.unveil.logs.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import me.passos.libs.unveil.core.navigation.UnveilPanelScope
import me.passos.libs.unveil.logs.LogEntry
import me.passos.libs.unveil.logs.LogLevel
import me.passos.libs.unveil.logs.LogStore
import me.passos.libs.unveil.logs.resources.Res
import me.passos.libs.unveil.logs.resources.logs_action_clear
import me.passos.libs.unveil.logs.resources.logs_detail_label_level
import me.passos.libs.unveil.logs.resources.logs_detail_label_tag
import me.passos.libs.unveil.logs.resources.logs_detail_label_time
import me.passos.libs.unveil.logs.resources.logs_detail_section_error
import me.passos.libs.unveil.logs.resources.logs_detail_section_log
import me.passos.libs.unveil.logs.resources.logs_detail_section_message
import me.passos.libs.unveil.logs.resources.logs_empty
import me.passos.libs.unveil.logs.resources.logs_empty_filtered
import me.passos.libs.unveil.logs.resources.logs_header
import me.passos.libs.unveil.logs.resources.logs_header_count
import me.passos.libs.unveil.logs.resources.logs_header_filtered
import me.passos.libs.unveil.logs.resources.logs_search_placeholder
import me.passos.libs.unveil.ui.components.UnveilSectionHeader
import me.passos.libs.unveil.ui.components.UnveilText
import me.passos.libs.unveil.ui.components.UnveilTextField
import me.passos.libs.unveil.ui.components.UnveilValueRow
import me.passos.libs.unveil.ui.theme.UnveilTheme
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun LogPanel(
    store: LogStore,
    scope: UnveilPanelScope
) {
    val allEntries = store.entries
    var selectedLevels by remember { mutableStateOf(LogLevel.entries.toSet()) }
    var query by remember { mutableStateOf("") }

    val filtered =
        allEntries.filter { entry ->
            entry.level in selectedLevels &&
                (
                    query.isBlank() ||
                        entry.tag.contains(query, ignoreCase = true) ||
                        entry.message.contains(query, ignoreCase = true)
                )
        }

    Column(modifier = Modifier.fillMaxSize()) {
        LevelFilterRow(
            selectedLevels = selectedLevels,
            onToggle = { level ->
                selectedLevels =
                    if (level in selectedLevels) selectedLevels - level else selectedLevels + level
            }
        )
        UnveilTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = stringResource(Res.string.logs_search_placeholder),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
        )
        UnveilSectionHeader(
            title =
                when {
                    allEntries.isEmpty() -> stringResource(Res.string.logs_header)
                    filtered.size == allEntries.size -> stringResource(Res.string.logs_header_count, allEntries.size)
                    else -> stringResource(Res.string.logs_header_filtered, filtered.size, allEntries.size)
                },
            actionLabel = if (allEntries.isNotEmpty()) stringResource(Res.string.logs_action_clear) else null,
            onAction = if (allEntries.isNotEmpty()) ({ store.clear() }) else null
        )

        if (filtered.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                UnveilText(
                    text =
                        if (allEntries.isEmpty()) {
                            stringResource(Res.string.logs_empty)
                        } else {
                            stringResource(Res.string.logs_empty_filtered)
                        }
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filtered, key = { it.id }) { entry ->
                    LogEntryRow(entry = entry) {
                        scope.pushPage("${entry.level.name}: ${entry.tag}") {
                            LogEntryDetail(entry = entry)
                        }
                    }
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(0.5.dp)
                                .background(UnveilTheme.colors.divider)
                    )
                }
            }
        }
    }
}

@Composable
private fun LevelFilterRow(
    selectedLevels: Set<LogLevel>,
    onToggle: (LogLevel) -> Unit
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        LogLevel.entries.forEach { level ->
            LevelChip(
                level = level,
                selected = level in selectedLevels,
                onClick = { onToggle(level) }
            )
        }
    }
}

@Composable
private fun LevelChip(
    level: LogLevel,
    selected: Boolean,
    onClick: () -> Unit
) {
    val color = levelColor(level)
    Text(
        text = level.badge,
        style = UnveilTheme.typography.label,
        color = if (selected) color else color.copy(alpha = 0.25f),
        modifier =
            Modifier
                .clickable(onClick = onClick)
                .padding(4.dp)
    )
}

@Composable
private fun LogEntryRow(
    entry: LogEntry,
    onClick: () -> Unit
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = entry.level.badge,
            style = UnveilTheme.typography.label,
            color = levelColor(entry.level),
            modifier = Modifier.width(12.dp)
        )
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = entry.tag,
                    style = UnveilTheme.typography.label,
                    color = UnveilTheme.colors.onSurfaceMuted,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = entry.timestamp.formatTimestamp(),
                    style = UnveilTheme.typography.label,
                    color = UnveilTheme.colors.onSurfaceMuted
                )
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = entry.message,
                style = UnveilTheme.typography.body,
                color = UnveilTheme.colors.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun LogEntryDetail(entry: LogEntry) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
    ) {
        UnveilSectionHeader(title = stringResource(Res.string.logs_detail_section_log))
        UnveilValueRow(label = stringResource(Res.string.logs_detail_label_level), value = entry.level.name)
        UnveilValueRow(label = stringResource(Res.string.logs_detail_label_tag), value = entry.tag)
        UnveilValueRow(
            label = stringResource(Res.string.logs_detail_label_time),
            value = entry.timestamp.formatTimestamp()
        )

        UnveilSectionHeader(title = stringResource(Res.string.logs_detail_section_message))
        UnveilText(
            text = entry.message,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        if (entry.error != null) {
            UnveilSectionHeader(title = stringResource(Res.string.logs_detail_section_error))
            UnveilText(
                text = entry.error,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun levelColor(level: LogLevel): Color =
    when (level) {
        LogLevel.Verbose -> UnveilTheme.colors.onSurfaceMuted
        LogLevel.Debug -> UnveilTheme.colors.primary
        LogLevel.Info -> UnveilTheme.colors.success
        LogLevel.Warn -> UnveilTheme.colors.warning
        LogLevel.Error -> UnveilTheme.colors.error
        LogLevel.Assert -> UnveilTheme.colors.error
    }

private val LogLevel.badge: String
    get() =
        when (this) {
            LogLevel.Verbose -> "V"
            LogLevel.Debug -> "D"
            LogLevel.Info -> "I"
            LogLevel.Warn -> "W"
            LogLevel.Error -> "E"
            LogLevel.Assert -> "A"
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
