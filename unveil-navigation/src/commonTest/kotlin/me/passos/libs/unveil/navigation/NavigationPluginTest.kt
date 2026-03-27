package me.passos.libs.unveil.navigation

import me.passos.libs.unveil.UnveilIcon
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class NavigationPluginTest {

    private lateinit var plugin: NavigationPlugin

    @BeforeTest
    fun setUp() {
        plugin = NavigationPlugin()
    }

    @Test
    fun `plugin id is navigation`() {
        assertEquals("navigation", plugin.id)
    }

    @Test
    fun `plugin title is Navigation`() {
        assertEquals("Navigation", plugin.title)
    }

    @Test
    fun `plugin icon is the compass emoji`() {
        assertIs<UnveilIcon.Emoji>(plugin.icon)
        assertEquals("🧭", (plugin.icon as UnveilIcon.Emoji).character)
    }

    @Test
    fun `plugin default maxHistoryEntries is 50`() {
        assertEquals(50, plugin.store.maxHistoryEntries)
    }

    @Test
    fun `maxHistoryEntries is configurable via constructor`() {
        val plugin = NavigationPlugin(maxHistoryEntries = 100)

        assertEquals(100, plugin.store.maxHistoryEntries)
    }

    @Test
    fun `plugin has a single Clear quick action`() {
        assertEquals(1, plugin.quickActions.size)
        assertEquals("Clear", plugin.quickActions[0].label)
    }

    @Test
    fun `Clear quick action empties history`() {
        plugin.observer.onNavigated(
            NavigationEntry(
                id = "1",
                route = "home",
                direction = NavigationDirection.Push,
                arguments = emptyMap(),
                timestamp = 0L,
            ),
            listOf(StackEntry(route = "home", arguments = emptyMap()))
        )

        plugin.quickActions[0].onToggle(true)

        assertTrue(plugin.store.history.isEmpty())
    }
}
