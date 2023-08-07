package com.deenislam.sdk.service.network

import com.deenislam.sdk.Deen
import com.deenislam.sdk.service.database.dao.UserPrefDao
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

internal class AuthInterceptor(
    private val userPrefDao: UserPrefDao?
):Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val acceessToken =  runBlocking {
            val userData = userPrefDao?.select()

            userData?.let {
                if(it.isNotEmpty() && it[0]?.token?.isNotEmpty() == true)
                    it[0]?.token.toString()
                else Deen.token
            }

        }

        var request = chain.request()
        request = request.newBuilder().header("Authorization", "Bearer $acceessToken").build()
        return chain.proceed(request)
    }


}