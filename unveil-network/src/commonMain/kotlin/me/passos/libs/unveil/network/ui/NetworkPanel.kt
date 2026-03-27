package me.passos.libs.unveil.network.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import me.passos.libs.unveil.network.NetworkEntry
import me.passos.libs.unveil.network.NetworkStore
import me.passos.libs.unveil.ui.components.UnveilSectionHeader
import me.passos.libs.unveil.ui.components.UnveilSliderRow
import me.passos.libs.unveil.ui.components.UnveilText
import me.passos.libs.unveil.ui.components.UnveilTextField
import me.passos.libs.unveil.ui.components.UnveilToggleRow
import me.passos.libs.unveil.ui.components.UnveilValueRow
import me.passos.libs.unveil.ui.theme.UnveilTheme

@Composable
internal fun NetworkPanel(
    store: NetworkStore,
    delayEnabled: Boolean,
    delaySeconds: Float,
    onDelayEnabledChange: (Boolean) -> Unit,
    onDelaySecondsChange: (Float) -> Unit,
    statusOverrideEnabled: Boolean,
    statusOverrideCode: Int,
    onStatusOverrideEnabledChange: (Boolean) -> Unit,
    onStatusOverrideCodeChange: (Int) -> Unit,
    scope: UnveilPanelScope
) {
    val entries = store.entries

    Column(modifier = Modifier.fillMaxSize()) {
        UnveilSectionHeader(title = "Delay")
        UnveilToggleRow(
            label = "Delay responses",
            checked = delayEnabled,
            onCheckedChange = onDelayEnabledChange
        )
        if (delayEnabled) {
            UnveilSliderRow(
                label = "Duration",
                value = delaySeconds,
                valueRange = 0f..10f,
                onValueChange = onDelaySecondsChange,
                valueLabel = "${delaySeconds.toInt()} s"
            )
        }
        UnveilSectionHeader(title = "Status Override")
        UnveilToggleRow(
            label = "Override status code",
            checked = statusOverrideEnabled,
            onCheckedChange = onStatusOverrideEnabledChange
        )
        if (statusOverrideEnabled) {
            var codeText by remember(statusOverrideCode) { mutableStateOf(statusOverrideCode.toString()) }
            UnveilTextField(
                value = codeText,
                onValueChange = { input ->
                    codeText = input
                    input.toIntOrNull()?.let { code ->
                        if (code in 100..599) onStatusOverrideCodeChange(code)
                    }
                },
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        UnveilSectionHeader(
            title = if (entries.isEmpty()) "Requests" else "Requests (${entries.size})",
            actionLabel = if (entries.isNotEmpty()) "Clear" else null,
            onAction = if (entries.isNotEmpty()) ({ store.clear() }) else null
        )

        if (entries.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                UnveilText(text = "No requests captured yet")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(entries, key = { it.request.id }) { entry ->
                    NetworkEntryRow(entry = entry) {
                        scope.pushPage(entry.shortUrl) {
                            NetworkEntryDetail(entry = entry)
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
private fun NetworkEntryRow(
    entry: NetworkEntry,
    onClick: () -> Unit
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MethodLabel(method = entry.request.method)
        Spacer(Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = entry.shortUrl,
                style = UnveilTheme.typography.body,
                color = UnveilTheme.colors.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (entry.error != null) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = entry.error,
                    style = UnveilTheme.typography.bodySmall,
                    color = UnveilTheme.colors.error,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Spacer(Modifier.width(8.dp))
        StatusLabel(entry = entry)
    }
}

@Composable
private fun MethodLabel(method: String) {
    Text(
        text = method.uppercase(),
        style = UnveilTheme.typography.label,
        color = methodColor(method)
    )
}

@Composable
private fun StatusLabel(entry: NetworkEntry) {
    val (text, color) =
        when {
            entry.error != null -> {
                "ERR" to UnveilTheme.colors.error
            }

            entry.response != null -> {
                val statusColor =
                    when (entry.response.statusCode / 100) {
                        2 -> UnveilTheme.colors.success
                        3 -> UnveilTheme.colors.primary
                        4 -> UnveilTheme.colors.warning
                        5 -> UnveilTheme.colors.error
                        else -> UnveilTheme.colors.onSurfaceMuted
                    }
                entry.response.statusCode.toString() to statusColor
            }

            else -> {
                "···" to UnveilTheme.colors.onSurfaceMuted
            }
        }
    Text(text = text, style = UnveilTheme.typography.label, color = color)
}

@Composable
private fun NetworkEntryDetail(entry: NetworkEntry) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
    ) {
        UnveilSectionHeader(title = "Request")
        UnveilValueRow(label = "Method", value = entry.request.method)
        UnveilValueRow(label = "URL", value = entry.request.url)

        if (entry.request.headers.isNotEmpty()) {
            UnveilSectionHeader(title = "Request Headers")
            entry.request.headers.forEach { (key, value) ->
                UnveilValueRow(label = key, value = value)
            }
        }

        if (entry.request.body != null) {
            UnveilSectionHeader(title = "Request Body")
            UnveilText(
                text = entry.request.body,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        when {
            entry.response != null -> {
                UnveilSectionHeader(title = "Response")
                UnveilValueRow(label = "Status", value = entry.response.statusCode.toString())
                UnveilValueRow(label = "Duration", value = "${entry.response.durationMs} ms")

                if (entry.response.headers.isNotEmpty()) {
                    UnveilSectionHeader(title = "Response Headers")
                    entry.response.headers.forEach { (key, value) ->
                        UnveilValueRow(label = key, value = value)
                    }
                }

                if (entry.response.body != null) {
                    UnveilSectionHeader(title = "Response Body")
                    UnveilText(
                        text = entry.response.body,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            entry.error != null -> {
                UnveilSectionHeader(title = "Error")
                UnveilText(
                    text = entry.error,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            else -> {
                UnveilSectionHeader(title = "Response")
                UnveilText(
                    text = "Request in flight…",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun methodColor(method: String): Color =
    when (method.uppercase()) {
        "GET" -> UnveilTheme.colors.primary
        "POST" -> UnveilTheme.colors.success
        "PUT", "PATCH" -> UnveilTheme.colors.warning
        "DELETE" -> UnveilTheme.colors.error
        else -> UnveilTheme.colors.onSurfaceMuted
    }

private val NetworkEntry.shortUrl: String
    get() {
        val withoutScheme = request.url.removePrefix("https://").removePrefix("http://")
        val slashIndex = withoutScheme.indexOf('/')
        return if (slashIndex >= 0) withoutScheme.substring(slashIndex) else withoutScheme
    }
