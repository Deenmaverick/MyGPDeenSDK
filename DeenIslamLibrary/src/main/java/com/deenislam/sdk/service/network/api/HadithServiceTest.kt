package com.deenislam.sdk.service.network.api

import com.deenislam.sdk.service.network.response.hadith.preview.HadithPreviewResponse
import retrofit2.http.GET
import retrofit2.http.Path

internal interface HadithServiceTest {

    @GET("{lang}/{bookname}/{chapter}")
    suspend fun getHadithPreview(
        @Path("lang") language: String,
        @Path("bookname") bookname: String,
        @Path("chapter") chapter: Int
    ): HadithPreviewResponse
}