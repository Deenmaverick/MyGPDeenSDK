package com.deenislamic.sdk.service.repository;

import com.deenislamic.sdk.service.network.ApiCall
import com.deenislamic.sdk.service.network.api.DeenService
import com.deenislamic.sdk.utils.RequestBodyMediaType
import com.deenislamic.sdk.utils.toRequestBody
import org.json.JSONObject

internal class PodcastRepository(
    private val deenService: DeenService?
) : ApiCall {

    suspend fun getPodcastHomePatch(language:String) = makeApicall {

        val body = JSONObject()
        body.put("language", language)
        body.put("device", "sdk")
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        deenService?.getPodcastHomePatch(requestBody)

    }

    suspend fun getPodcastContent(language:String,pid:Int) = makeApicall {

        val body = JSONObject()
        body.put("language", language)
        body.put("Id", pid)

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        deenService?.getPodcastContent(requestBody)

    }

    suspend fun getPodcastCat(language:String,cid:Int) = makeApicall {

        val body = JSONObject()
        body.put("language", language)
        body.put("Id", cid)

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        deenService?.getPodcastCategory(requestBody)

    }

    suspend fun getPodcastAllComment(pid: Int, language: String) = makeApicall {

        val body = JSONObject()
        body.put("Id", pid)
        body.put("language", language)
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        deenService?.getPodcastAllComment(requestBody)

    }

    suspend fun likeComment(cid:Int,isFav:Boolean,language:String) = makeApicall {

        val body = JSONObject()
        body.put("contentId", cid)
        body.put("isFavorite", isFav)
        body.put("language", language)
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        deenService?.likeLivePodcastComment(requestBody)

    }

    suspend fun addComment(pid:Int,comment:String) = makeApicall {

        val body = JSONObject()
        body.put("podcastId", pid)
        body.put("comment", comment)
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        deenService?.livePodcastAddComment(requestBody)

    }

} 