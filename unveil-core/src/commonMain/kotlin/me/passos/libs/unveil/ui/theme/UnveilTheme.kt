@file:Suppress("ktlint:compose:compositionlocal-allowlist")

package me.passos.libs.unveil.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Defines the color palette used by Unveil.
 *
 * Provides all color tokens required to render the Unveil UI in a consistent
 * and self-contained way, independent of the host application's theme.
 */
@Immutable
internal data class UnveilColors(
    val surface: Color,
    val surfaceVariant: Color,
    val onSurface: Color,
    val onSurfaceMuted: Color,
    val primary: Color,
    val onPrimary: Color,
    val error: Color,
    val success: Color,
    val warning: Color,
    val scrim: Color,
    val divider: Color,
    val chipActive: Color,
    val chipIdle: Color,
    val chipOnActive: Color,
    val chipOnIdle: Color
)

/**
 * Default color palette used by Unveil.
 *
 * Provides a complete set of color tokens applied
 * when no custom palette is supplied.
 */
private val DefaultDarkColors =
    UnveilColors(
        surface = Color(0xFF0F0F11),
        surfaceVariant = Color(0xFF1A1A1E),
        onSurface = Color(0xFFE8E8ED),
        onSurfaceMuted = Color(0xFF6E6E82),
        primary = Color(0xFF7B81F5),
        onPrimary = Color(0xFFFFFFFF),
        error = Color(0xFFF47067),
        success = Color(0xFF4ADE80),
        warning = Color(0xFFF59E0B),
        scrim = Color(0x99000000),
        divider = Color(0xFF232329),
        chipActive = Color(0xFF7B81F5),
        chipIdle = Color(0xFF252529),
        chipOnActive = Color(0xFFFFFFFF),
        chipOnIdle = Color(0xFF9898A8)
    )

/**
 * Defines the typography used by Unveil.
 *
 * Provides text styles required to render the Unveil UI in a consistent
 * and self-contained way, independent of the host application's typography.
 */
@Immutable
internal data class UnveilTypography(
    val drawerTitle: TextStyle,
    val sectionTitle: TextStyle,
    val body: TextStyle,
    val bodySmall: TextStyle,
    val label: TextStyle,
    val chip: TextStyle,
    val mono: TextStyle
)

/**
 * Default typography used by Unveil.
 *
 * Provides a complete set of text styles applied when no
 * custom typography is supplied.
 */
private val DefaultTypography =
    UnveilTypography(
        drawerTitle = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.SemiBold),
        sectionTitle = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 0.5.sp),
        body = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Normal),
        bodySmall = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal),
        label = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium),
        chip = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium),
        mono = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal)
        // TODO: mono should use a monospace font — inject via UnveilTheme.typography
    )

/**
 * CompositionLocal that provides the active [UnveilColors] to the Unveil composition tree.
 */
internal val LocalUnveilColors = staticCompositionLocalOf { DefaultDarkColors }

/**
 * CompositionLocal that provides the active [UnveilTypography] to the Unveil composition tree.
 */
internal val LocalUnveilTypography = staticCompositionLocalOf { DefaultTypography }

/**
 * Provides access to the theme used by Unveil.
 *
 * Exposes color and typography values that are scoped to the Unveil UI
 * and independent of the host application's theme.
 */
internal object UnveilTheme {
    val colors: UnveilColors
        @Composable get() = LocalUnveilColors.current

    val typography: UnveilTypography
        @Composable get() = LocalUnveilTypography.current
}

/**
 * Provides the Unveil theme to its content.
 *
 * Supplies color and typography values that are used by Unveil components
 * within the given composition.
 *
 * @param colors Color palette to be provided.
 * @param typography Typography to be provided.
 * @param content Composable content that consumes the theme.
 */
@Composable
internal fun UnveilThemeProvider(
    colors: UnveilColors = DefaultDarkColors,
    typography: UnveilTypography = DefaultTypography,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalUnveilColors provides colors,
        LocalUnveilTypography provides typography,
        content = content
    )
}
