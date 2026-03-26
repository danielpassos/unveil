package me.passos.libs.unveil.network

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NetworkEntryTest {
    private val request =
        NetworkRequest(
            id = "req-1",
            method = "GET",
            url = "https://example.com/api",
            headers = emptyMap(),
            body = null,
            sentAt = 0L
        )

    @Test
    fun `isInFlight is true when response and error are both null`() {
        val entry = NetworkEntry(request = request)

        assertTrue(entry.isInFlight)
        assertNull(entry.response)
        assertFalse(entry.isError)
    }

    @Test
    fun `isInFlight is false when a response is set`() {
        val entry =
            NetworkEntry(
                request = request,
                response = NetworkResponse(200, emptyMap(), null, 100L)
            )

        assertFalse(entry.isInFlight)
        assertFalse(entry.isError)
    }

    @Test
    fun `isError is true and isInFlight is false when an error is set`() {
        val entry = NetworkEntry(request = request, error = "timeout")

        assertFalse(entry.isInFlight)
        assertTrue(entry.isError)
    }
}
