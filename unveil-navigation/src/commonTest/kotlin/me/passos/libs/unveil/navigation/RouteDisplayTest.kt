package me.passos.libs.unveil.navigation

import kotlin.test.Test
import kotlin.test.assertEquals

class RouteDisplayTest {

    // --- type-safe (FQN) routes ---

    @Test
    fun `displayRoute returns simple class name for top-level FQN route`() {
        val entry = navigationEntry(route = "com.example.HomeScreen")
        assertEquals("HomeScreen", entry.displayRoute())
    }

    @Test
    fun `displayRoute returns simple class name for nested FQN route`() {
        val entry = navigationEntry(route = "me.passos.libs.unveil.sample.AppScreen.Screen2")
        assertEquals("Screen2", entry.displayRoute())
    }

    @Test
    fun `displayRoute strips system query params from type-safe route`() {
        val route = "me.passos.libs.unveil.sample.AppScreen.Screen2" +
            "?android-support-nav:controller:deepLinkint=0"
        val entry = navigationEntry(route = route)
        assertEquals("Screen2", entry.displayRoute())
    }

    // --- string-based routes ---

    @Test
    fun `displayRoute returns plain route unchanged`() {
        val entry = navigationEntry(route = "home")
        assertEquals("home", entry.displayRoute())
    }

    @Test
    fun `displayRoute substitutes path arguments for string routes`() {
        val entry = navigationEntry(
            route = "profile/{userId}",
            arguments = mapOf("userId" to "42")
        )
        assertEquals("profile/42", entry.displayRoute())
    }

    @Test
    fun `displayRoute appends unused arguments as query params for string routes`() {
        val entry = navigationEntry(
            route = "search",
            arguments = mapOf("query" to "kotlin")
        )
        assertEquals("search?query=kotlin", entry.displayRoute())
    }

    // --- StackEntry ---

    @Test
    fun `StackEntry displayRoute returns simple class name for FQN`() {
        val entry = StackEntry(
            route = "com.example.AppGraph.Detail",
            arguments = emptyMap()
        )
        assertEquals("Detail", entry.displayRoute())
    }

    @Test
    fun `StackEntry displayRoute returns path route unchanged`() {
        val entry = StackEntry(route = "settings/notifications", arguments = emptyMap())
        assertEquals("settings/notifications", entry.displayRoute())
    }

    // helpers

    private fun navigationEntry(
        route: String,
        arguments: Map<String, String> = emptyMap(),
    ) = NavigationEntry(
        id = "test",
        route = route,
        direction = NavigationDirection.Push,
        arguments = arguments,
        timestamp = 0L,
    )
}
