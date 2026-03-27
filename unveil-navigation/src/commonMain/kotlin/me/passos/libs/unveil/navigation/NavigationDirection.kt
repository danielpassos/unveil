package me.passos.libs.unveil.navigation

/**
 * Represents the direction of a navigation event.
 *
 * Used to distinguish whether navigation advanced to a new destination or
 * returned to a previous one.
 */
enum class NavigationDirection {
    /** Indicates navigation to a new destination. */
    Push,

    /** Indicates navigation to a previous destination. */
    Pop
}
