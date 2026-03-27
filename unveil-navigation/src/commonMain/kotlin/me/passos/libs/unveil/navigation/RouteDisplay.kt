package me.passos.libs.unveil.navigation

/**
 * Returns a human-readable label for a navigation destination.
 *
 * Type-safe navigation serializes destinations as fully-qualified class names
 * (e.g. `com.example.AppScreen.Home?…`). This function detects that pattern and
 * returns only the simple class name. Regular path-style routes have their
 * argument placeholders substituted, matching [resolvedRoute] behaviour.
 */
internal fun NavigationEntry.displayRoute(): String {
    val base = route.substringBefore('?')
    return if (base.isFqnRoute()) base.substringAfterLast('.') else resolvedRoute()
}

/**
 * Returns a human-readable label for a back-stack entry.
 *
 * Behaves identically to [NavigationEntry.displayRoute]: simple class name for
 * type-safe routes, resolved path for string-based routes.
 */
internal fun StackEntry.displayRoute(): String {
    val base = route.substringBefore('?')
    return if (base.isFqnRoute()) base.substringAfterLast('.') else resolvedRoute()
}

/**
 * Returns true when the string looks like a Kotlin/Java fully-qualified class name:
 * contains dots, no path separators (`/`), and no placeholder braces (`{`).
 */
private fun String.isFqnRoute(): Boolean =
    contains('.') && !contains('/') && !contains('{')
