package com.deenislamic.sdk.service.repository;

import com.deenislamic.sdk.service.network.ApiCall
import com.deenislamic.sdk.service.network.api.YoutubeService
import com.deenislamic.sdk.service.network.body.youtube.YoutubeBody
import com.deenislamic.sdk.utils.RequestBodyMediaType
import com.deenislamic.sdk.utils.YOUTUBE_VIDEO_KEY
import com.deenislamic.sdk.utils.toRequestBody
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