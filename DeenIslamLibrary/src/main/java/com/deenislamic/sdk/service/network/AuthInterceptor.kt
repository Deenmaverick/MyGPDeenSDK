package com.deenislamic.sdk.service.network

import com.deenislamic.sdk.DeenSDKCore
import okhttp3.Interceptor
import okhttp3.Response

internal class AuthInterceptor :Interceptor {

    var isEnabled = true
    var tempAccessToken = ""
    override fun intercept(chain: Interceptor.Chain): Response {

        /*val acceessToken =  runBlocking {
            val userData = userPrefDao?.select()

            userData?.let {
                if(it.isNotEmpty() && it[0]?.token?.isNotEmpty() == true)
                    it[0]?.token.toString()
                else Deen.token
            }

        }*/

        val acceessToken = DeenSDKCore.GetDeenToken()

        var request = chain.request()
        request = request.newBuilder()
            .header("device", "mygpdeen")
            .header("version", "v1.0")
            .header("client", "1.2")
            .header("deviceid", "")
            .header("devicemodel", "")
            .header("Authorization", "Bearer ${
                tempAccessToken.ifEmpty { acceessToken }
            }")
            .build()
        //return chain.proceed(request)
        tempAccessToken = ""
        return if (isEnabled) {
            chain.proceed(request)
        } else {
            chain.proceed(chain.request())
        }
    }


}