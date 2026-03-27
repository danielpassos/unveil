package me.passos.libs.unveil.navigation

/**
 * Direction of a navigation event.
 *
 * Indicates whether a navigation event moved forward in the stack ([Push])
 * or returned to a previous destination ([Pop]).
 */
enum class NavigationDirection {
    Push,
    Pop
}
