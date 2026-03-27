package me.passos.libs.unveil.logs.kermit

import co.touchlab.kermit.Severity
import me.passos.libs.unveil.logs.LogLevel
import me.passos.libs.unveil.logs.LogsPlugin
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class KermitLogSinkTest {

    private lateinit var plugin: LogsPlugin
    private lateinit var sink: KermitLogSink

    @BeforeTest
    fun setUp() {
        plugin = LogsPlugin()
        sink = KermitLogSink(plugin)
    }

    private val firstEntry get() = plugin.store.entries[0]

    // region — Severity mapping

    @Test
    fun `Verbose severity maps to Verbose level`() {
        sink.log(Severity.Verbose, "msg", "Tag", null)
        assertEquals(LogLevel.Verbose, firstEntry.level)
    }

    @Test
    fun `Debug severity maps to Debug level`() {
        sink.log(Severity.Debug, "msg", "Tag", null)
        assertEquals(LogLevel.Debug, firstEntry.level)
    }

    @Test
    fun `Info severity maps to Info level`() {
        sink.log(Severity.Info, "msg", "Tag", null)
        assertEquals(LogLevel.Info, firstEntry.level)
    }

    @Test
    fun `Warn severity maps to Warn level`() {
        sink.log(Severity.Warn, "msg", "Tag", null)
        assertEquals(LogLevel.Warn, firstEntry.level)
    }

    @Test
    fun `Error severity maps to Error level`() {
        sink.log(Severity.Error, "msg", "Tag", null)
        assertEquals(LogLevel.Error, firstEntry.level)
    }

    @Test
    fun `Assert severity maps to Assert level`() {
        sink.log(Severity.Assert, "msg", "Tag", null)
        assertEquals(LogLevel.Assert, firstEntry.level)
    }

    // endregion

    // region — Field forwarding

    @Test
    fun `tag is forwarded to the log entry`() {
        sink.log(Severity.Debug, "msg", "MyComponent", null)
        assertEquals("MyComponent", firstEntry.tag)
    }

    @Test
    fun `message is forwarded to the log entry`() {
        sink.log(Severity.Info, "something happened", "Tag", null)
        assertEquals("something happened", firstEntry.message)
    }

    @Test
    fun `throwable message is captured as error`() {
        sink.log(Severity.Error, "crashed", "Tag", RuntimeException("bad state"))
        assertEquals("bad state", firstEntry.error)
    }

    @Test
    fun `null throwable results in null error`() {
        sink.log(Severity.Warn, "watch out", "Tag", null)
        assertNull(firstEntry.error)
    }

    @Test
    fun `each call adds an entry to the plugin store`() {
        sink.log(Severity.Debug, "first", "T", null)
        sink.log(Severity.Info, "second", "T", null)
        sink.log(Severity.Warn, "third", "T", null)

        assertEquals(3, plugin.store.entries.size)
    }

    @Test
    fun `entries are stored newest first`() {
        sink.log(Severity.Debug, "first", "T", null)
        sink.log(Severity.Info, "second", "T", null)

        assertEquals("second", plugin.store.entries[0].message)
        assertEquals("first", plugin.store.entries[1].message)
    }

    // endregion
}
