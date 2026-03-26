package me.passos.libs.unveil.network.ktor

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import me.passos.libs.unveil.network.NetworkInterceptor
import me.passos.libs.unveil.network.NetworkRequest
import me.passos.libs.unveil.network.NetworkResponse
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class KtorNetworkInterceptorTest {

    private val capturedRequests = mutableListOf<NetworkRequest>()
    private val capturedResponses = mutableMapOf<String, NetworkResponse>()
    private val capturedErrors = mutableMapOf<String, String>()

    @BeforeTest
    fun setUp() {
        capturedRequests.clear()
        capturedResponses.clear()
        capturedErrors.clear()
    }

    private val testInterceptor = object : NetworkInterceptor {
        override fun onRequestSent(request: NetworkRequest) {
            capturedRequests.add(request)
        }

        override fun onResponseReceived(requestId: String, response: NetworkResponse) {
            capturedResponses[requestId] = response
        }

        override fun onError(requestId: String, message: String) {
            capturedErrors[requestId] = message
        }
    }

    private fun buildClient(status: HttpStatusCode = HttpStatusCode.OK): HttpClient {
        val interceptor = testInterceptor
        return HttpClient(MockEngine { respond("OK", status) }) {
            install(KtorNetworkPlugin) {
                this.interceptor = interceptor
            }
        }
    }

    @Test
    fun `sending a GET request forwards it to the interceptor`() = runTest {
        buildClient().get("https://example.com/api/users")

        assertEquals(1, capturedRequests.size)
        assertEquals("GET", capturedRequests[0].method)
    }

    @Test
    fun `sending a POST request captures the POST method`() = runTest {
        buildClient().post("https://example.com/api/users")

        assertEquals("POST", capturedRequests[0].method)
    }

    @Test
    fun `the captured request includes the full URL`() = runTest {
        buildClient().get("https://example.com/api/users")

        assertTrue(capturedRequests[0].url.contains("example.com"))
    }

    @Test
    fun `receiving a response forwards it to the interceptor`() = runTest {
        buildClient().get("https://example.com/api/users")

        assertEquals(1, capturedResponses.size)
    }

    @Test
    fun `the response body is null when the caller does not read it`() = runTest {
        buildClient().get("https://example.com/api/users")

        val requestId = capturedRequests[0].id
        assertNull(capturedResponses[requestId]?.body)
    }

    @Test
    fun `the captured response includes the HTTP status code`() = runTest {
        buildClient(HttpStatusCode.Created).get("https://example.com/api/users")

        val requestId = capturedRequests[0].id
        assertEquals(201, capturedResponses[requestId]?.statusCode)
    }

    @Test
    fun `the captured response includes a non-negative duration`() = runTest {
        buildClient().get("https://example.com/api/users")

        val requestId = capturedRequests[0].id
        val duration = capturedResponses[requestId]?.durationMs ?: -1L
        assertTrue(duration >= 0)
    }

    @Test
    fun `the response is correlated to the originating request by id`() = runTest {
        buildClient().get("https://example.com/api/users")

        val requestId = capturedRequests[0].id
        assertTrue(capturedResponses.containsKey(requestId))
    }

    @Test
    fun `multiple requests are each forwarded to the interceptor independently`() = runTest {
        val client = buildClient()
        client.get("https://example.com/api/users")
        client.get("https://example.com/api/posts")

        assertEquals(2, capturedRequests.size)
        assertEquals(2, capturedResponses.size)
    }

    @Test
    fun `each request receives a unique id`() = runTest {
        val client = buildClient()
        client.get("https://example.com/api/users")
        client.get("https://example.com/api/posts")

        assertNotEquals(capturedRequests[0].id, capturedRequests[1].id)
    }

    @Test
    fun `the captured request body is null when no body is set`() = runTest {
        buildClient().get("https://example.com/api/users")

        assertNull(capturedRequests[0].body)
    }

    @Test
    fun `the captured request includes a string body`() = runTest {
        buildClient().post("https://example.com/api/users") { setBody("hello") }

        assertEquals("hello", capturedRequests[0].body)
    }

    @Test
    fun `the captured request includes a byte array body`() = runTest {
        buildClient().post("https://example.com/api/users") { setBody("world".encodeToByteArray()) }

        assertEquals("world", capturedRequests[0].body)
    }

    @Test
    fun `the response body is forwarded when the caller reads it`() = runTest {
        val response = buildClient().get("https://example.com/api/users")
        response.bodyAsText()

        val requestId = capturedRequests[0].id
        assertNotNull(capturedResponses[requestId]?.body)
    }

    @Test
    fun `the response body matches the content returned by the server`() = runTest {
        val response = buildClient().get("https://example.com/api/users")
        response.bodyAsText()

        val requestId = capturedRequests[0].id
        assertEquals("OK", capturedResponses[requestId]?.body)
    }

    @Test
    fun `a connection error forwards the error message to the interceptor`() = runTest {
        val errorClient =
            HttpClient(MockEngine { throw Exception("Connection refused") }) {
                install(KtorNetworkPlugin) { this.interceptor = testInterceptor }
            }

        try {
            errorClient.get("https://example.com/api/users")
        } catch (_: Exception) {
        }

        assertEquals(1, capturedErrors.size)
        assertEquals("Connection refused", capturedErrors.values.first())
    }

    @Test
    fun `a connection error is correlated to the originating request by id`() = runTest {
        val errorClient =
            HttpClient(MockEngine { throw Exception("timeout") }) {
                install(KtorNetworkPlugin) { this.interceptor = testInterceptor }
            }

        try {
            errorClient.get("https://example.com/api/users")
        } catch (_: Exception) {
        }

        val requestId = capturedRequests[0].id
        assertTrue(capturedErrors.containsKey(requestId))
    }
}
