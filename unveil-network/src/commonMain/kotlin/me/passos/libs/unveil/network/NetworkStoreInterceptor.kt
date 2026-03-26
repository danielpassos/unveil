package me.passos.libs.unveil.network

internal class NetworkStoreInterceptor(
    private val store: NetworkStore
) : NetworkInterceptor {
    override fun onRequestSent(request: NetworkRequest) = store.record(request)

    override fun onResponseReceived(
        requestId: String,
        response: NetworkResponse
    ) = store.complete(requestId, response)

    override fun onError(
        requestId: String,
        message: String
    ) = store.fail(requestId, message)
}
