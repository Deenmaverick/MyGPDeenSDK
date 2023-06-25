package com.deenislam.sdk.service.network.api

import com.deenislam.sdk.service.network.response.auth.login.LoginResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

internal interface AuthenticateService {

    @POST("User/Loginsdk")
    suspend fun login(@Body parm: RequestBody): LoginResponse

}