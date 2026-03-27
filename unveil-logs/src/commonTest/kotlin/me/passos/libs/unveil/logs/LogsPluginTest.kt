package me.passos.libs.unveil.logs

import me.passos.libs.unveil.UnveilIcon
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LogsPluginTest {

    private lateinit var plugin: LogsPlugin

    @BeforeTest
    fun setUp() {
        plugin = LogsPlugin()
    }

    @Test
    fun `plugin id is logs`() {
        assertEquals("logs", plugin.id)
    }

    @Test
    fun `plugin title is Logs`() {
        assertEquals("Logs", plugin.title)
    }

    @Test
    fun `plugin icon is the clipboard emoji`() {
        assertIs<UnveilIcon.Emoji>(plugin.icon)
        assertEquals("📋", (plugin.icon as UnveilIcon.Emoji).character)
    }

    @Test
    fun `plugin has a single Clear quick action`() {
        assertEquals(1, plugin.quickActions.size)
        assertEquals("Clear", plugin.quickActions[0].label)
    }

    @Test
    fun `default maxEntries is 100`() {
        assertEquals(100, plugin.store.maxEntries)
    }

    @Test
    fun `maxEntries is configurable via constructor`() {
        val plugin = LogsPlugin(maxEntries = 250)

        assertEquals(250, plugin.store.maxEntries)
    }

    // region — Sink forwarding

    @Test
    fun `sink adds an entry to the store when onLog is called`() {
        plugin.sink.onLog(level = LogLevel.Debug, tag = "MyTag", message = "hello")

        assertEquals(1, plugin.store.entries.size)
    }

    @Test
    fun `sink records the correct level`() {
        plugin.sink.onLog(level = LogLevel.Error, tag = "T", message = "boom")

        assertEquals(LogLevel.Error, plugin.store.entries[0].level)
    }

    @Test
    fun `sink records the correct tag`() {
        plugin.sink.onLog(level = LogLevel.Info, tag = "NetworkTag", message = "ok")

        assertEquals("NetworkTag", plugin.store.entries[0].tag)
    }

    @Test
    fun `sink records the correct message`() {
        plugin.sink.onLog(level = LogLevel.Warn, tag = "T", message = "watch out")

        assertEquals("watch out", plugin.store.entries[0].message)
    }

    @Test
    fun `sink records the error when provided`() {
        plugin.sink.onLog(level = LogLevel.Error, tag = "T", message = "crash", error = "NullPointerException")

        assertEquals("NullPointerException", plugin.store.entries[0].error)
    }

    @Test
    fun `sink stores null error when none is provided`() {
        plugin.sink.onLog(level = LogLevel.Debug, tag = "T", message = "fine")

        assertNull(plugin.store.entries[0].error)
    }

    @Test
    fun `sink assigns a non-blank id to each entry`() {
        plugin.sink.onLog(level = LogLevel.Info, tag = "T", message = "msg")

        assertTrue(plugin.store.entries[0].id.isNotBlank())
    }

    @Test
    fun `sink assigns unique ids to different entries`() {
        plugin.sink.onLog(level = LogLevel.Info, tag = "T", message = "first")
        plugin.sink.onLog(level = LogLevel.Info, tag = "T", message = "second")

        val ids = plugin.store.entries.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun `Clear quick action empties the store`() {
        plugin.sink.onLog(level = LogLevel.Debug, tag = "T", message = "msg")
        assertNotNull(plugin.store.entries.firstOrNull())

        plugin.quickActions[0].onToggle(true)

        assertTrue(plugin.store.entries.isEmpty())
    }

    // endregion
}
