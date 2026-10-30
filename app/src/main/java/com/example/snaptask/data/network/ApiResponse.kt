package com.example.snaptask.data.network

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: T?,
    @SerializedName("errors") val errors: List<String>? = null,
    @SerializedName("meta") val meta: MetaData? = null,
    @SerializedName("error_code") val errorCode: String? = null,
)

data class MetaData(
    @SerializedName("total") val total: Int? = null,
    @SerializedName("page") val page: Int? = null,
    @SerializedName("per_page") val perPage: Int? = null,
    @SerializedName("last_page") val lastPage: Int? = null,
)

sealed class Resource<T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val message: String, val errorCode: String? = null) : Resource<T>()
    class Loading<T> : Resource<T>()
}

