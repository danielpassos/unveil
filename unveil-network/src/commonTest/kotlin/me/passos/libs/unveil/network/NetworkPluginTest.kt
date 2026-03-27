package me.passos.libs.unveil.network

import me.passos.libs.unveil.UnveilIcon
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NetworkPluginTest {

    private lateinit var plugin: NetworkPlugin

    @BeforeTest
    fun setUp() {
        plugin = NetworkPlugin()
    }

    @Test
    fun `plugin id is network`() {
        assertEquals("network", plugin.id)
    }

    @Test
    fun `plugin title is Network`() {
        assertEquals("Network", plugin.title)
    }

    @Test
    fun `plugin icon is the globe emoji`() {
        assertIs<UnveilIcon.Emoji>(plugin.icon)
        assertEquals("🌐", (plugin.icon as UnveilIcon.Emoji).character)
    }

    @Test
    fun `quickActions contains a single Clear action`() {
        assertEquals(1, plugin.quickActions.size)
        assertEquals("Clear", plugin.quickActions[0].label)
    }

    @Test
    fun `the Clear quick action empties the store`() {
        plugin.interceptor.onRequestSent(buildRequest("r1"))
        assertEquals(1, plugin.store.entries.size)

        plugin.quickActions[0].onToggle(true)

        assertTrue(plugin.store.entries.isEmpty())
    }

    @Test
    fun `interceptor records a request as in-flight when onRequestSent is called`() {
        plugin.interceptor.onRequestSent(buildRequest("r1"))

        assertEquals(1, plugin.store.entries.size)
        assertEquals("r1", plugin.store.entries[0].request.id)
        assertTrue(plugin.store.entries[0].isInFlight)
    }

    @Test
    fun `interceptor completes the matching entry when onResponseReceived is called`() {
        plugin.interceptor.onRequestSent(buildRequest("r1"))
        val response = NetworkResponse(200, emptyMap(), null, 50L)

        plugin.interceptor.onResponseReceived("r1", response)

        assertEquals(response, plugin.store.entries[0].response)
        assertFalse(plugin.store.entries[0].isInFlight)
        assertNull(plugin.store.entries[0].error)
    }

    @Test
    fun `interceptor marks the matching entry as failed when onError is called`() {
        plugin.interceptor.onRequestSent(buildRequest("r1"))

        plugin.interceptor.onError("r1", "Connection refused")

        assertEquals("Connection refused", plugin.store.entries[0].error)
        assertTrue(plugin.store.entries[0].isError)
        assertNull(plugin.store.entries[0].response)
    }

    @Test
    fun `interceptor ignores onResponseReceived when the request id is not in the store`() {
        plugin.interceptor.onRequestSent(buildRequest("r1"))

        plugin.interceptor.onResponseReceived("unknown", NetworkResponse(200, emptyMap(), null, 50L))

        assertTrue(plugin.store.entries[0].isInFlight)
    }

    // region — Delay config

    @Test
    fun `delay is disabled by default`() {
        assertFalse(plugin.delayEnabled)
    }

    @Test
    fun `default delay duration is 1 second`() {
        assertEquals(1f, plugin.delaySeconds)
    }

    @Test
    fun `setting delayEnabled to true is reflected in delayConfig`() {
        plugin.delayEnabled = true

        assertTrue(plugin.delayConfig.enabled)
    }

    @Test
    fun `setting delaySeconds updates delayConfig delayMs proportionally`() {
        plugin.delaySeconds = 3f

        assertEquals(3000L, plugin.delayConfig.delayMs)
    }

    // endregion

    // region — Status override config

    @Test
    fun `status override is disabled by default`() {
        assertFalse(plugin.statusOverrideEnabled)
    }

    @Test
    fun `default status override code is 500`() {
        assertEquals(500, plugin.statusOverrideCode)
    }

    @Test
    fun `setting statusOverrideEnabled to true is reflected in statusOverrideConfig`() {
        plugin.statusOverrideEnabled = true

        assertTrue(plugin.statusOverrideConfig.enabled)
    }

    @Test
    fun `setting statusOverrideCode updates statusOverrideConfig`() {
        plugin.statusOverrideCode = 404

        assertEquals(404, plugin.statusOverrideConfig.statusCode)
    }

    // endregion

    private fun buildRequest(id: String) = NetworkRequest(
        id = id,
        method = "GET",
        url = "https://example.com/api",
        headers = emptyMap(),
        body = null,
        sentAt = 0L
    )
}
