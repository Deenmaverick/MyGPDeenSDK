package com.deenislam.sdk.service.network

import okhttp3.Interceptor
import okhttp3.Response

internal class AuthInterceptor(
    private val acceessToken: String
):Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        var request = chain.request()
        request = request.newBuilder().header("Authorization", "Bearer $acceessToken").build()
        return chain.proceed(request)
    }


}