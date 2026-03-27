package me.passos.libs.unveil.crash

import me.passos.libs.unveil.UnveilIcon
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class CrashPluginTest {

    private lateinit var plugin: CrashPlugin

    @BeforeTest
    fun setUp() {
        plugin = CrashPlugin()
    }

    @Test
    fun `plugin id is crash`() {
        assertEquals("crash", plugin.id)
    }

    @Test
    fun `plugin title is Crash`() {
        assertEquals("Crash", plugin.title)
    }

    @Test
    fun `plugin icon is the explosion emoji`() {
        assertIs<UnveilIcon.Emoji>(plugin.icon)
        assertEquals("💥", (plugin.icon as UnveilIcon.Emoji).character)
    }

    @Test
    fun `plugin exposes five crash actions`() {
        assertEquals(5, plugin.actions.size)
    }

    @Test
    fun `each crash action has a non-blank label`() {
        assertTrue(plugin.actions.all { it.label.isNotBlank() })
    }

    @Test
    fun `each crash action has a non-blank description`() {
        assertTrue(plugin.actions.all { it.description.isNotBlank() })
    }

    @Test
    fun `crash action labels are unique`() {
        val labels = plugin.actions.map { it.label }
        assertEquals(labels.size, labels.toSet().size)
    }
}
