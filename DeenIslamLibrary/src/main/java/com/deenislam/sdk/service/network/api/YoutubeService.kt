package com.deenislam.sdk.service.network.api

import com.deenislam.sdk.service.network.response.youtube.YoutubeVideoResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

internal interface YoutubeService {

    @POST("player")
    suspend fun videoDetails(
        @Query("key") key:String,
        @Body body: RequestBody
    ): YoutubeVideoResponse
}