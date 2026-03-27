package me.passos.libs.unveil.sample.network

sealed interface NetworkResult<out T> {
    data class Success<T>(
        val data: T
    ) : NetworkResult<T>

    data class HttpError(
        val status: Int,
        val body: String? = null
    ) : NetworkResult<Nothing>

    data class NetworkError(
        val throwable: Throwable
    ) : NetworkResult<Nothing>
}
