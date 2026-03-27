package me.passos.libs.unveil.network.ktor

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import me.passos.libs.unveil.network.NetworkPlugin
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class KtorNetworkInterceptorTest {
    private lateinit var networkPlugin: NetworkPlugin

    @BeforeTest
    fun setUp() {
        networkPlugin = NetworkPlugin()
    }

    private fun buildClient(serverStatus: HttpStatusCode = HttpStatusCode.OK): HttpClient =
        HttpClient(MockEngine { respond("OK", serverStatus) }) {
            install(KtorNetworkPlugin) {
                plugin = networkPlugin
            }
        }

    private fun buildClientWithResponseHeaders(responseHeaders: Headers): HttpClient =
        HttpClient(MockEngine { respond("OK", HttpStatusCode.OK, responseHeaders) }) {
            install(KtorNetworkPlugin) {
                plugin = networkPlugin
            }
        }

    private val entries get() = networkPlugin.store.entries
    private val firstEntry get() = entries[0]

    // region — Request capture

    @Test
    fun `sending a GET request forwards it to the interceptor`() =
        runTest {
            buildClient().get("https://example.com/api/users")

            assertEquals(1, entries.size)
            assertEquals("GET", firstEntry.request.method)
        }

    @Test
    fun `sending a POST request captures the POST method`() =
        runTest {
            buildClient().post("https://example.com/api/users")

            assertEquals("POST", firstEntry.request.method)
        }

    @Test
    fun `the captured request includes the full URL`() =
        runTest {
            buildClient().get("https://example.com/api/users")

            assertTrue(firstEntry.request.url.contains("example.com"))
        }

    @Test
    fun `the captured request includes headers`() =
        runTest {
            buildClient().get("https://example.com/api/users") {
                header("X-Api-Key", "secret")
            }

            assertEquals("secret", firstEntry.request.headers["X-Api-Key"])
        }

    @Test
    fun `the captured request body is null when no body is set`() =
        runTest {
            buildClient().get("https://example.com/api/users")

            assertNull(firstEntry.request.body)
        }

    @Test
    fun `the captured request includes a string body`() =
        runTest {
            buildClient().post("https://example.com/api/users") { setBody("hello") }

            assertEquals("hello", firstEntry.request.body)
        }

    @Test
    fun `the captured request includes a byte array body`() =
        runTest {
            buildClient().post("https://example.com/api/users") { setBody("world".encodeToByteArray()) }

            assertEquals("world", firstEntry.request.body)
        }

    // endregion

    // region — Response capture

    @Test
    fun `receiving a response completes the entry`() =
        runTest {
            buildClient().get("https://example.com/api/users")

            assertNotNull(firstEntry.response)
        }

    @Test
    fun `the captured response includes the HTTP status code`() =
        runTest {
            buildClient(HttpStatusCode.Created).get("https://example.com/api/users")

            assertEquals(201, firstEntry.response?.statusCode)
        }

    @Test
    fun `the captured response includes a non-negative duration`() =
        runTest {
            buildClient().get("https://example.com/api/users")

            assertTrue((firstEntry.response?.durationMs ?: -1L) >= 0)
        }

    @Test
    fun `the response body is null when the caller does not read it`() =
        runTest {
            buildClient().get("https://example.com/api/users")

            assertNull(firstEntry.response?.body)
        }

    @Test
    fun `the response body is forwarded when the caller reads it`() =
        runTest {
            buildClient().get("https://example.com/api/users").bodyAsText()

            assertNotNull(firstEntry.response?.body)
        }

    @Test
    fun `the response body matches the content returned by the server`() =
        runTest {
            buildClient().get("https://example.com/api/users").bodyAsText()

            assertEquals("OK", firstEntry.response?.body)
        }

    @Test
    fun `the captured response includes headers returned by the server`() =
        runTest {
            buildClientWithResponseHeaders(headersOf("X-Request-Id" to listOf("abc123")))
                .get("https://example.com/api/users")

            assertEquals("abc123", firstEntry.response?.headers?.get("X-Request-Id"))
        }

    // endregion

    // region — Multiple requests

    @Test
    fun `multiple requests are each captured independently`() =
        runTest {
            val client = buildClient()
            client.get("https://example.com/api/users")
            client.get("https://example.com/api/posts")

            assertEquals(2, entries.size)
        }

    @Test
    fun `each request receives a unique id`() =
        runTest {
            val client = buildClient()
            client.get("https://example.com/api/users")
            client.get("https://example.com/api/posts")

            assertNotEquals(entries[0].request.id, entries[1].request.id)
        }

    // endregion

    // region — Errors

    @Test
    fun `a connection error marks the entry as failed`() =
        runTest {
            val errorClient =
                HttpClient(MockEngine { throw Exception("Connection refused") }) {
                    install(KtorNetworkPlugin) { plugin = networkPlugin }
                }

            try {
                errorClient.get("https://example.com/api/users")
            } catch (_: Exception) {
            }

            assertTrue(firstEntry.isError)
            assertEquals("Connection refused", firstEntry.error)
        }

    @Test
    fun `a connection error is correlated to the originating request`() =
        runTest {
            val errorClient =
                HttpClient(MockEngine { throw Exception("timeout") }) {
                    install(KtorNetworkPlugin) { plugin = networkPlugin }
                }

            try {
                errorClient.get("https://example.com/api/users")
            } catch (_: Exception) {
            }

            assertEquals(firstEntry.request.id, entries.first { it.isError }.request.id)
        }

    // endregion

    // region — Delay

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    @Test
    fun `when delay is enabled the virtual clock advances by at least the configured duration`() =
        runTest {
            networkPlugin.delayEnabled = true
            networkPlugin.delaySeconds = 1f

            buildClient().get("https://example.com/api/users")

            assertTrue(
                testScheduler.currentTime >= 1000L,
                "Expected virtual time >= 1000ms but was ${testScheduler.currentTime}ms"
            )
        }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    @Test
    fun `when delay is disabled the virtual clock does not advance`() =
        runTest {
            networkPlugin.delayEnabled = false

            buildClient().get("https://example.com/api/users")

            assertEquals(0L, testScheduler.currentTime)
        }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    @Test
    fun `the virtual clock advances proportionally to the configured delay duration`() =
        runTest {
            networkPlugin.delayEnabled = true
            networkPlugin.delaySeconds = 2f

            buildClient().get("https://example.com/api/users")

            assertTrue(
                testScheduler.currentTime >= 2000L,
                "Expected virtual time >= 2000ms but was ${testScheduler.currentTime}ms"
            )
        }

    @Test
    fun `when both delay and status override are enabled the delay is still applied`() =
        runTest {
            networkPlugin.delayEnabled = true
            networkPlugin.delaySeconds = 1f
            networkPlugin.statusOverrideEnabled = true
            networkPlugin.statusOverrideCode = 500

            buildClient().get("https://example.com/api/users")

            assertTrue(
                testScheduler.currentTime >= 1000L,
                "Expected virtual time >= 1000ms but was ${testScheduler.currentTime}ms"
            )
        }

    // endregion

    // region — Status override

    @Test
    fun `when status override is enabled the panel records the overridden status code`() =
        runTest {
            networkPlugin.statusOverrideEnabled = true
            networkPlugin.statusOverrideCode = 500

            buildClient(serverStatus = HttpStatusCode.OK).get("https://example.com/api/users")

            assertEquals(500, firstEntry.response?.statusCode)
        }

    @Test
    fun `when status override is enabled the caller receives the overridden status code`() =
        runTest {
            networkPlugin.statusOverrideEnabled = true
            networkPlugin.statusOverrideCode = 500

            val response =
                buildClient(serverStatus = HttpStatusCode.OK)
                    .get("https://example.com/api/users")

            assertEquals(500, response.status.value)
        }

    @Test
    fun `when status override is disabled the caller receives the real status code`() =
        runTest {
            networkPlugin.statusOverrideEnabled = false

            val response =
                buildClient(serverStatus = HttpStatusCode.Created)
                    .get("https://example.com/api/users")

            assertEquals(201, response.status.value)
        }

    // endregion
}
