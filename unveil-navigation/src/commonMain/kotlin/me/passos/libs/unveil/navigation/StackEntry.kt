package me.passos.libs.unveil.navigation

/**
 * One entry in the live back stack at the moment of a navigation event.
 *
 * Each instance represents a destination that is currently on the stack,
 * along with the arguments it was launched with.
 *
 * @property route Route template as declared in the navigation graph.
 * @property arguments Argument values associated with this back stack entry, keyed by argument name.
 */
data class StackEntry(
    val route: String,
    val arguments: Map<String, String>,
)

/**
 * Returns [route] with `{key}` placeholders substituted by values from [StackEntry.arguments].
 *
 * Arguments that do not appear in the route template are appended as `?key=value` query parameters.
 */
internal fun StackEntry.resolvedRoute(): String {
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
