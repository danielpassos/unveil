package me.passos.libs.unveil.network

/**
 * Pairs a [NetworkRequest] with its eventual [NetworkResponse] or error.
 *
 * An entry progresses through three states:
 * - In-flight: [response] and [error] are both null.
 * - Completed: [response] is populated, [error] is null.
 * - Failed: [error] is populated, [response] is null.
 *
 * @property request The outgoing request.
 * @property response The received response, or null while in-flight or on error.
 * @property error Human-readable error message, or null if the request is in-flight or succeeded.
 */
data class NetworkEntry(
    val request: NetworkRequest,
    val response: NetworkResponse? = null,
    val error: String? = null
) {
    /**
     * Whether the request has not yet received a response or error.
     */
    val isInFlight: Boolean get() = response == null && error == null

    /**
     * Whether the request ended with an error.
     */
    val isError: Boolean get() = error != null
}

/**
 * Represents a single outgoing HTTP request captured by Unveil.
 *
 * @property id Unique identifier used to correlate the request with its response.
 * @property method HTTP method (e.g. GET, POST).
 * @property url Full URL of the request.
 * @property headers Request headers at the time of sending.
 * @property body Request body, or null if absent or not yet captured.
 * @property sentAt Unix epoch millisecond when the request was dispatched.
 */
data class NetworkRequest(
    val id: String,
    val method: String,
    val url: String,
    val headers: Map<String, String>,
    val body: String?,
    val sentAt: Long
)

/**
 * Represents the HTTP response received for a [NetworkRequest].
 *
 * @property statusCode HTTP status code.
 * @property headers Response headers.
 * @property body Response body, or null if absent or not yet captured.
 * @property durationMs Round-trip duration in milliseconds from request dispatch to response receipt.
 */
data class NetworkResponse(
    val statusCode: Int,
    val headers: Map<String, String>,
    val body: String?,
    val durationMs: Long
)
