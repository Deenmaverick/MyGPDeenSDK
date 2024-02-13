package com.deenislam.sdk.service.network.api

import com.deenislam.sdk.service.network.response.common.BasicResponse
import com.deenislam.sdk.service.network.response.dashboard.DashboardResponse
import com.deenislam.sdk.service.network.response.quran.learning.digital_quran_class.DigitalQuranClassResponse
import com.deenislam.sdk.service.network.response.quran.learning.digital_quran_class.QuranClassSecureUrlResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

internal interface DashboardService {

    @POST("Dashboard/GetDashboardDataV2")
    suspend fun getDashboardDataV2(@Body parm: RequestBody): DashboardResponse


    @POST("Dashboard/GetDashboardDataV3")
    suspend fun getDashboardDataV3(@Body parm: RequestBody): DashboardResponse

    @POST("Dashboard/saveFavoriteFeature")
    suspend fun saveFavMenu(@Body parm: RequestBody): BasicResponse

    // Quran Learning

    @POST("Courses/GetQuranClassPatch")
    suspend fun getHomePatch(@Body parm: RequestBody): DashboardResponse

    @POST("Courses/CoursesContentById")
    suspend fun getDigitalQuranClass(@Body parm: RequestBody): DigitalQuranClassResponse

    @POST("Profile/GetEncodedUrl")
    suspend fun getQuranClassSecureUrl(
        @Query("url") url: String,
    ): QuranClassSecureUrlResponse
}