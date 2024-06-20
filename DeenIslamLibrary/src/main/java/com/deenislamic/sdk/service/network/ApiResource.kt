package com.deenislamic.sdk.service.network

import okhttp3.ResponseBody

internal sealed class ApiResource<out T>
{
    data class Success<out T>(val value:T) : ApiResource<T>()
    data class Failure(
        val isNetworkError: Boolean,
        val errorCode: Int?,
        val errorResponse: ResponseBody?
    ): ApiResource<Nothing>()
}