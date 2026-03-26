package me.passos.libs.unveil.network

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NetworkStoreTest {

    private lateinit var store: NetworkStore

    @BeforeTest
    fun setUp() {
        store = NetworkStore()
    }

    @Test
    fun `entries is empty before any requests are recorded`() {
        assertTrue(store.entries.isEmpty())
    }

    @Test
    fun `record adds the request as a new entry`() {
        store.record(buildRequest("req-1"))

        assertEquals(1, store.entries.size)
        assertEquals("req-1", store.entries[0].request.id)
    }

    @Test
    fun `record prepends entries so the newest appears first`() {
        store.record(buildRequest("req-1"))
        store.record(buildRequest("req-2"))

        assertEquals("req-2", store.entries[0].request.id)
        assertEquals("req-1", store.entries[1].request.id)
    }

    @Test
    fun `record creates an entry with no response or error`() {
        store.record(buildRequest("req-1"))

        assertTrue(store.entries[0].isInFlight)
    }

    @Test
    fun `complete sets the response on the matching entry`() {
        store.record(buildRequest("req-1"))
        val response = NetworkResponse(200, emptyMap(), null, 100L)

        store.complete("req-1", response)

        assertEquals(response, store.entries[0].response)
        assertNull(store.entries[0].error)
    }

    @Test
    fun `complete is a no-op when the request id is not found`() {
        store.record(buildRequest("req-1"))

        store.complete("unknown", NetworkResponse(200, emptyMap(), null, 100L))

        assertNull(store.entries[0].response)
    }

    @Test
    fun `complete only updates the entry with the matching id`() {
        store.record(buildRequest("req-1"))
        store.record(buildRequest("req-2"))
        val response = NetworkResponse(200, emptyMap(), null, 50L)

        store.complete("req-1", response)

        assertEquals(response, store.entries[1].response)
        assertNull(store.entries[0].response)
    }

    @Test
    fun `fail sets the error message on the matching entry`() {
        store.record(buildRequest("req-1"))

        store.fail("req-1", "Connection refused")

        assertEquals("Connection refused", store.entries[0].error)
        assertNull(store.entries[0].response)
    }

    @Test
    fun `fail is a no-op when the request id is not found`() {
        store.record(buildRequest("req-1"))

        store.fail("unknown", "error")

        assertNull(store.entries[0].error)
    }

    @Test
    fun `clear removes all entries from the store`() {
        store.record(buildRequest("req-1"))
        store.record(buildRequest("req-2"))

        store.clear()

        assertTrue(store.entries.isEmpty())
    }

    private fun buildRequest(id: String) = NetworkRequest(
        id = id,
        method = "GET",
        url = "https://example.com",
        headers = emptyMap(),
        body = null,
        sentAt = 0L
    )
}
