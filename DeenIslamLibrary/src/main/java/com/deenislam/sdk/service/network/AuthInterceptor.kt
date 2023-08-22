package com.deenislam.sdk.service.network

import com.deenislam.sdk.DeenSDKCore
import com.deenislam.sdk.service.database.dao.UserPrefDao
import okhttp3.Interceptor
import okhttp3.Response

internal class AuthInterceptor():Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        /*val acceessToken =  runBlocking {
            val userData = userPrefDao?.select()

            userData?.let {
                if(it.isNotEmpty() && it[0]?.token?.isNotEmpty() == true)
                    it[0]?.token.toString()
                else Deen.token
            }

        }*/

        val acceessToken = DeenSDKCore.token

        var request = chain.request()
        request = request.newBuilder()
            .header("device", "blsdk_android")
            .header("Authorization", "Bearer $acceessToken")
            .build()
        return chain.proceed(request)
    }


}