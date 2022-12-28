package com.arthur.examples.shorturl.data.remote.service

import com.arthur.examples.shorturl.data.DataResult
import retrofit2.Response

abstract class BaseRemoteDataSource {

    protected suspend fun <T> getResult(call: suspend () -> Response<T>): DataResult<T> {
        try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                body?.let {
                    return DataResult.Success(body)
                }
            }
            return error("${response.code()} ${response.message()}")
        } catch (e: Exception) {
            return error(e.message ?: e.toString())
        }
    }

    private fun <T> error(errorMessage: String): DataResult<T> =
        DataResult.Error("Api call failed $errorMessage")

}