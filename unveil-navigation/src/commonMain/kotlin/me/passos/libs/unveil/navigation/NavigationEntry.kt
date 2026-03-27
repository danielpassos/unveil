package me.passos.libs.unveil.navigation

/**
 * A single navigation event captured by Unveil.
 *
 * Represents one entry in the navigation history, recording where the app navigated to,
 * the direction of travel, and any arguments that were supplied at the time.
 *
 * @property id Stable unique identifier used as a Compose list key.
 * @property route Route template as declared in the navigation graph (e.g. "profile/{userId}").
 * @property direction Whether this event was a forward push or a backward pop.
 * @property arguments Actual argument values resolved at navigation time, keyed by argument name.
 * @property timestamp Wall-clock time of capture in epoch milliseconds (UTC).
 */
data class NavigationEntry(
    val id: String,
    val route: String,
    val direction: NavigationDirection,
    val arguments: Map<String, String>,
    val timestamp: Long
)

/**
 * Returns [route] with `{key}` placeholders substituted by values from [NavigationEntry.arguments].
 *
 * Arguments that do not appear in the route template are appended as `?key=value` query parameters.
 */
internal fun NavigationEntry.resolvedRoute(): String {
    var resolved = route
    val unused = mutableMapOf<String, String>()
    for ((key, value) in arguments) {
        val placeholder = "{$key}"
        if (resolved.contains(placeholder)) {
            resolved = resolved.replace(placeholder, value)
        } else {
            unused[key] = value
        }
    }
    if (unused.isNotEmpty()) {
        val query = unused.entries.joinToString("&") { (k, v) -> "$k=$v" }
        resolved = "$resolved?$query"
    }
    return resolved
}
