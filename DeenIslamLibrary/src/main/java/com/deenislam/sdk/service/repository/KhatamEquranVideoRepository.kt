package com.deenislam.sdk.service.repository;

import com.deenislam.sdk.DeenSDKCore
import com.deenislam.sdk.service.network.ApiCall
import com.deenislam.sdk.service.network.api.DeenService
import com.deenislam.sdk.service.network.response.khatamquran.KhatamQuranVideosResponse
import com.deenislam.sdk.utils.RequestBodyMediaType
import com.deenislam.sdk.utils.toRequestBody
import com.google.gson.Gson
import org.json.JSONObject
import java.io.File
import java.io.FileReader

internal class KhatamEquranVideoRepository(
    private val deenService: DeenService?
) :
    ApiCall {
    suspend fun getKhatamQuranVideos(language: String) = makeApicall {
        val destinationFolder = File(DeenSDKCore.appContext?.filesDir, "khatamquran/videos.json")

        if (!destinationFolder.exists()) {
            val body = JSONObject()
            body.put("language", language)

            val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
            deenService?.getKhatamQuranVideos(parm = requestBody)

        } else {
         readFromFile()
        }

    }
    suspend fun getRecentKhatamQuranVideos(language: String) = makeApicall {

        val body = JSONObject()
        body.put("language", language)

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        deenService?.getRecentKhatamQuranVideos(parm = requestBody)
    }
    suspend fun addKhatamQuranContentHistory(contentID: Int, totalDuration: Int,  duration: Int, language: String) = makeApicall {

        val body = JSONObject()
        body.put("contentID", contentID)
        body.put("totalDuration", totalDuration)
        body.put("duration", duration)
        body.put("language", language)

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        deenService?.addKhatamQuranContentHistory(parm = requestBody)
    }

    private fun readFromFile(): KhatamQuranVideosResponse {
        val file = File(DeenSDKCore.appContext?.filesDir, "khatamquran/videos.json")

        if (!file.exists()) {
            // Handle the case when the file doesn't exist or is empty
            return KhatamQuranVideosResponse(arrayListOf(), success = false)
        }

        try {
            val reader = FileReader(file)

            // Define the type of your data
            //val listType: Type = object : TypeToken<List<CommonCardData>>() {}.type

            // Use Gson to deserialize the JSON data into a list
           /* val gson = Gson()
            return gson.fromJson(reader, listType)*/

            return   Gson().fromJson(reader.readText(), KhatamQuranVideosResponse::class.java)
        } catch (e: Exception) {
            // Handle exceptions (e.g., IOException or JsonSyntaxException)
            e.printStackTrace()

        }

        // Return an empty list if there's an error
        return KhatamQuranVideosResponse(arrayListOf(), success = false)
    }
} 