package com.deenislam.sdk.service.network

import com.deenislam.sdk.DeenSDKCore
import com.deenislam.sdk.service.database.dao.UserPrefDao
import okhttp3.Interceptor
import okhttp3.Response

internal class AuthInterceptor(
    private val userPrefDao: UserPrefDao?
):Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        /*val acceessToken =  runBlocking {
            val userData = userPrefDao?.select()

            userData?.let {
                if(it.isNotEmpty() && it[0]?.token?.isNotEmpty() == true)
                    it[0]?.token.toString()
                else Deen.token
            }

        }*/

        val acceessToken = "eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJBcHBsaWNhdGlvbiI6IkRlZW4gSXNsYW0iLCJuYW1lIjoiODgwMTczODQzOTIzNiIsInJvbGUiOiJTREsiLCJuYmYiOjE2OTIxNjU1NzYsImV4cCI6MTY5MjI1MTk3NiwiaWF0IjoxNjkyMTY1NTc2fQ.FlYlkXOJAYl4cFAeBmq3tgBRfsTyNKwWO72UvfBtg3m89C0Gd0EvLDqUvd5v4vHYywJ_HatgvviWNx_-VFfq4A"

        var request = chain.request()
        request = request.newBuilder().header("Authorization", "Bearer $acceessToken").build()
        return chain.proceed(request)
    }


}