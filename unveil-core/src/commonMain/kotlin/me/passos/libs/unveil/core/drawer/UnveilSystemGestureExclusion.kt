package me.passos.libs.unveil.core.drawer

import androidx.compose.runtime.Composable

/**
 * Requests exclusion of a region from system gesture handling.
 *
 * Used to ensure that interactions within the specified edge area are handled
 * by Unveil rather than the underlying system.
 *
 * @param edgeZonePx Size of the region to be excluded, in pixels.
 */
@Composable
internal expect fun UnveilGestureExclusionEffect(edgeZonePx: Float)
