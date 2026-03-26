package me.passos.libs.unveil.network.ktor

import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.plugin
import io.ktor.client.statement.HttpResponseContainer
import io.ktor.client.statement.HttpResponsePipeline
import io.ktor.util.AttributeKey
import io.ktor.util.date.getTimeMillis
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readRemaining
import kotlinx.io.readByteArray
import me.passos.libs.unveil.network.NetworkRequest
import me.passos.libs.unveil.network.NetworkResponse
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

private val requestIdKey = AttributeKey<String>("UnveilRequestId")
private val startTimeKey = AttributeKey<Long>("UnveilStartTime")

@OptIn(ExperimentalUuidApi::class)
private fun newRequestId() = Uuid.random().toString()

/**
 * Ktor client plugin that forwards HTTP traffic to an Unveil
 * [NetworkInterceptor][me.passos.libs.unveil.network.NetworkInterceptor].
 *
 * Install this plugin when configuring the Ktor HttpClient and supply the interceptor
 * exposed by [NetworkPlugin](me.passos.libs.unveil.network.NetworkPlugin):
 *
 * ```kotlin
 * val httpClient = HttpClient {
 *     install(KtorNetworkPlugin) {
 *         interceptor = networkPlugin.interceptor
 *     }
 * }
 * ```
 *
 * Captured per request: method, URL, request headers, request body (String/ByteArray),
 * response status, response headers, response body, and round-trip duration.
 *
 * Network-level errors (connection failures, timeouts) are forwarded via
 * [NetworkInterceptor.onError][me.passos.libs.unveil.network.NetworkInterceptor.onError].
 *
 * The response body is captured at [HttpResponsePipeline.Receive] the earliest pipeline
 * phase, so it is always a raw [ByteReadChannel] regardless of whether content negotiation
 * or other plugins are installed.
 */
val KtorNetworkPlugin =
    createClientPlugin("UnveilNetwork", ::KtorNetworkPluginConfig) {
        val networkInterceptor = pluginConfig.interceptor

        @Suppress("TooGenericExceptionCaught")
        client.plugin(HttpSend).intercept { request ->
            try {
                execute(request)
            } catch (cause: Throwable) {
                val id = request.attributes.getOrNull(requestIdKey)
                if (id != null) {
                    networkInterceptor.onError(id, cause.message ?: "Unknown error")
                }
                throw cause
            }
        }

        onRequest { request, body ->
            val id = newRequestId()
            val sentAt = getTimeMillis()

            request.attributes.put(requestIdKey, id)
            request.attributes.put(startTimeKey, sentAt)

            val bodyText: String? =
                when (body) {
                    is String -> body
                    is ByteArray -> body.decodeToString()
                    else -> null
                }

            networkInterceptor.onRequestSent(
                NetworkRequest(
                    id = id,
                    method = request.method.value,
                    url = request.url.buildString(),
                    headers =
                        request.headers
                            .build()
                            .entries()
                            .associate { (key, values) -> key to (values.firstOrNull() ?: "") },
                    body = bodyText,
                    sentAt = sentAt
                )
            )
        }

        onResponse { response ->
            val id = response.call.attributes.getOrNull(requestIdKey) ?: return@onResponse
            val startTime = response.call.attributes.getOrNull(startTimeKey) ?: 0L

            networkInterceptor.onResponseReceived(
                id,
                NetworkResponse(
                    statusCode = response.status.value,
                    headers =
                        response.headers
                            .entries()
                            .associate { (key, values) -> key to (values.firstOrNull() ?: "") },
                    body = null,
                    durationMs = getTimeMillis() - startTime
                )
            )
        }

        // Intercept at the earliest response pipeline phase so the body is always a raw
        // ByteReadChannel — before default transforms and content negotiation run at Parse
        // and Transform. Bytes are read, forwarded to the interceptor, and a fresh channel
        // carrying the same bytes is returned so downstream processing is unaffected.
        client.responsePipeline.intercept(HttpResponsePipeline.Receive) { (info, body) ->
            if (body !is ByteReadChannel) return@intercept

            val id = context.attributes.getOrNull(requestIdKey)
            val startTime = context.attributes.getOrNull(startTimeKey)

            if (id == null || startTime == null) return@intercept

            val bytes = body.readRemaining().readByteArray()
            val bodyText = bytes.decodeToString().ifEmpty { null }

            networkInterceptor.onResponseReceived(
                id,
                NetworkResponse(
                    statusCode = context.response.status.value,
                    headers =
                        context.response.headers
                            .entries()
                            .associate { (key, values) -> key to (values.firstOrNull() ?: "") },
                    body = bodyText,
                    durationMs = getTimeMillis() - startTime
                )
            )

            proceedWith(HttpResponseContainer(info, ByteReadChannel(bytes)))
        }
    }
