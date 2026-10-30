package com.example.snaptask.data.repository

import com.example.snaptask.data.network.ApiResponse
import com.example.snaptask.data.network.Resource
import retrofit2.Response

suspend fun <T> safeApiCall(call: suspend () -> Response<ApiResponse<T>>): Resource<T> {
    return try {
        val response = call()
        val body = response.body()
        when {
            response.isSuccessful && body?.success == true && body.data != null ->
                Resource.Success(body.data)
            response.isSuccessful && body?.success == true ->
                @Suppress("UNCHECKED_CAST")
                Resource.Success(Unit as T)
            else -> Resource.Error(
                message = body?.message ?: "Request failed (${response.code()})",
                errorCode = body?.errorCode,
            )
        }
    } catch (e: Exception) {
        Resource.Error(e.message ?: "Unknown network error")
    }
}

