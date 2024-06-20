package com.deenislamic.sdk.service.network.api

import com.deenislamic.sdk.service.network.response.quran.learning.quransikkhaacademy.ContentByIdResponse
import com.deenislamic.sdk.service.network.response.quran.learning.quransikkhaacademy.ContentListResponse
import com.deenislamic.sdk.service.network.response.quran.learning.quransikkhaacademy.CreateUserResponse
import com.deenislamic.sdk.service.network.response.quran.learning.quransikkhaacademy.GetUserResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

internal interface QuranShikkhaService {

    @POST("bot/my-order")
    suspend fun createUser(
        @Header("apikey") apikey: String,
        @Body parm: RequestBody
    ): CreateUserResponse

    @GET("bot/my-user/{userPhone}")
    suspend fun getUser(
        @Header("apikey") apikey: String,
        @Path("userPhone") userPhone: String
    ): GetUserResponse

    @GET("public/content?")
    suspend fun getContentList(
        @Header("accesstoken") accesstoken: String?,
        @Query("card_slug") card_slug: String,
        @Query("count") count: Int
    ): ContentListResponse

    @GET("public/content/{id}")
    suspend fun getContentByID(
        @Header("accesstoken") accesstoken: String?,
        @Path("id") id: String,
        @Query("hls") hls: String
    ): ContentByIdResponse



}