package me.passos.libs.unveil.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.passos.libs.unveil.ui.theme.UnveilTheme

/**
 * Displays a section header within Unveil.
 *
 * Provides contextual grouping for related content and
 * may expose an optional action associated with the section.
 *
 * @param title Title associated with the section.
 * @param modifier Modifier applied to the header.
 * @param actionLabel Optional label for a secondary action.
 * @param onAction Invoked when the action is requested.
 */
@Composable
fun UnveilSectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = UnveilTheme.typography.sectionTitle,
            color = UnveilTheme.colors.onSurfaceMuted
        )
        Spacer(Modifier.weight(1f))

        if (actionLabel != null && onAction != null) {
            // TODO: clickable Text action
        }
    }
}

/**
 * Displays a row with a toggleable state.
 *
 * Used to represent a boolean configuration or feature that can be enabled or disabled.
 *
 * @param checked Current toggle state.
 * @param label Primary label describing the toggle.
 * @param modifier Modifier applied to the row.
 * @param description Optional secondary description providing additional context.
 * @param onCheckedChange Invoked when the toggle state changes.
 */
@Composable
fun UnveilToggleRow(
    checked: Boolean,
    label: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = UnveilTheme.typography.body
            )
            if (description != null) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = description,
                    style = UnveilTheme.typography.bodySmall,
                    color = UnveilTheme.colors.onSurfaceMuted
                )
            }
        }
        Spacer(Modifier.width(16.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

/**
 * Displays a pair of related values.
 *
 * Used to present read-only information where a label is associated with a value.
 *
 * @param label Label describing the value.
 * @param value Value associated with the label.
 * @param modifier Modifier applied to the row.
 */
@Composable
fun UnveilValueRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            modifier = Modifier.weight(0.4f),
            text = label,
            style = UnveilTheme.typography.body
        )
        SelectionContainer {
            Text(
                modifier = Modifier.weight(0.6f),
                text = value,
                style = UnveilTheme.typography.mono
            )
        }
    }
}

/**
 * Represents an action that can be triggered by the user.
 *
 * Used to initiate operations within Unveil.
 *
 * @param label Text describing the action.
 * @param enabled Whether the action is currently available.
 * @param onClick Invoked when the action is triggered.
 * @param modifier Modifier applied to the component.
 */
@Composable
fun UnveilButton(
    label: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        modifier = modifier,
        enabled = enabled,
        onClick = onClick
    ) {
        Text(text = label)
    }
}

/**
 * Allows the user to input and edit text.
 *
 * Used for configurable values or user-provided input within Unveil.
 *
 * @param value Current text value.
 * @param placeholder Hint displayed when the value is empty.
 * @param singleLine Whether the input is restricted to a single line.
 * @param onValueChange Invoked when the text changes.
 * @param modifier Modifier applied to the input field.
 */
@Composable
fun UnveilTextField(
    value: String,
    placeholder: String = "",
    singleLine: Boolean = true,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        placeholder = { Text(text = placeholder) },
        singleLine = singleLine,
        onValueChange = onValueChange,
        modifier = modifier
    )
}

/**
 * Displays a simple piece of text.
 *
 * Used for lightweight textual content within Unveil.
 *
 * @param text Text to display.
 * @param modifier Modifier applied to the text.
 */
@Composable
fun UnveilText(
    text: String,
    modifier: Modifier = Modifier
) {
    BasicText(
        modifier = modifier,
        text = text,
        style = UnveilTheme.typography.label.copy(color = UnveilTheme.colors.onSurface)
    )
}
