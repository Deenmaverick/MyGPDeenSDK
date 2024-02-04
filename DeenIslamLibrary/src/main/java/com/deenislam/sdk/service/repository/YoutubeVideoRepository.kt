package com.deenislam.sdk.service.repository;

import android.util.Log
import com.deenislam.sdk.service.network.ApiCall
import com.deenislam.sdk.service.network.AuthInterceptor
import com.deenislam.sdk.service.network.api.YoutubeService
import com.deenislam.sdk.service.network.body.youtube.YoutubeBody
import com.deenislam.sdk.utils.RequestBodyMediaType
import com.deenislam.sdk.utils.YOUTUBE_VIDEO_KEY
import com.deenislam.sdk.utils.toRequestBody
import com.google.gson.Gson

internal class YoutubeVideoRepository(
    private val youtubeService: YoutubeService?
) : ApiCall {


    suspend fun getVideoDetails(videoid:String) = makeApicall {


        val requestBody = Gson().toJson(
            YoutubeBody(
            videoId = videoid
        )
        ).toString().toRequestBody(RequestBodyMediaType)

        youtubeService?.videoDetails(key = YOUTUBE_VIDEO_KEY,body = requestBody)


    }

} 