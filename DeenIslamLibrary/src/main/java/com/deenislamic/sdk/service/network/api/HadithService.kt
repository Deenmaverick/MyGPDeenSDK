package com.deenislamic.sdk.service.network.api

import com.deenislamic.sdk.service.network.response.BasicResponse
import com.deenislamic.sdk.service.network.response.hadith.HadithResponse
import com.deenislamic.sdk.service.network.response.hadith.chapter.HadithChapterResponse
import com.deenislamic.sdk.service.network.response.hadith.preview.HadithPreviewResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

internal interface HadithService {

    @POST("Hadith/AllHadithBook")
    suspend fun getCollectionList(
        @Body parm: RequestBody
    ): HadithResponse

    @POST("Hadith/HadithChapterByBookId")
    suspend fun getChapterByCollection(
        @Body parm: RequestBody
    ): HadithChapterResponse

    @POST("Hadith/HadithByChapterFromBook")
    suspend fun getHadithPreview(
        @Body parm: RequestBody
    ): HadithPreviewResponse

    @POST("Hadith/AddFavoriteHadith")
    suspend fun setHadithFav(
        @Body parm: RequestBody
    ): BasicResponse

    @POST("Hadith/FavHadithByChapterFromBook")
    suspend fun getFavHadith(
        @Body parm: RequestBody
    ): HadithPreviewResponse


}