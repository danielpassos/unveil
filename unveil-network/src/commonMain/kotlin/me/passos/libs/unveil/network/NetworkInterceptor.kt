package me.passos.libs.unveil.network

/**
 * Entry point through which network adapters report traffic to Unveil.
 *
 * Implementations are provided by [NetworkPlugin] and passed to a framework-specific
 * adapter (e.g. the Ktor adapter) that calls these methods as requests flow through
 * the HTTP client pipeline.
 *
 * Each request is identified by [NetworkRequest.id] so that the response or error
 * can be correlated with the originating request.
 */
interface NetworkInterceptor {
    /**
     * Called when a request has been dispatched.
     *
     * @param request The request that was sent.
     */
    fun onRequestSent(request: NetworkRequest)

    /**
     * Called when a response has been received for a previously dispatched request.
     * The response body is always null at this point; it is forwarded separately via
     * [onBodyReceived] once the caller consumes it.
     *
     * @param requestId The [NetworkRequest.id] of the originating request.
     * @param response The received response.
     */
    fun onResponseReceived(
        requestId: String,
        response: NetworkResponse
    )

    /**
     * Called when the response body has been read by the caller.
     *
     * This is a separate callback from [onResponseReceived] so that body capture does
     * not block the response from being recorded, body reading may happen long after
     * the response headers arrive.
     *
     * @param requestId The [NetworkRequest.id] of the originating request.
     * @param body The decoded response body text, or null if the body was empty.
     */
    fun onBodyReceived(
        requestId: String,
        body: String?
    ) {}

    /**
     * Called when a request ended with an error instead of a response.
     *
     * @param requestId The [NetworkRequest.id] of the originating request.
     * @param message Human-readable description of the failure.
     */
    fun onError(
        requestId: String,
        message: String
    )
}
