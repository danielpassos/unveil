package me.passos.libs.unveil.sample.network

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess

suspend fun generateUuid(client: HttpClient): NetworkResult<String> =
    try {
        val response = client.get("https://httpbin.org/uuid")
        println(response.status)
        if (response.status.isSuccess()) {
            NetworkResult.Success(response.bodyAsText())
        } else {
            NetworkResult.HttpError(
                status = response.status.value,
                body = response.bodyAsText()
            )
        }
    } catch (e: Exception) {
        NetworkResult.NetworkError(e)
    }
