package com.deenislamic.sdk.service.network.api

import com.deenislamic.sdk.service.network.response.BasicResponse
import com.deenislamic.sdk.service.network.response.advertisement.AdvertisementResponse
import com.deenislamic.sdk.service.network.response.auth.login.LoginResponse
import com.deenislamic.sdk.service.network.response.dashboard.DashboardResponse
import com.deenislamic.sdk.service.network.response.subscription.SubscriptionPageResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

internal interface AuthenticateService {

    @POST("User/Loginsdk")
    suspend fun login(@Body parm: RequestBody): LoginResponse

  /*  @POST("Dashboard/GetDashboardData")
    suspend fun getDashboardData(@Body parm: RequestBody): DashboardResponse
*/
    @POST("Dashboard/GetDashboardDataV3")
    suspend fun getDashboardDataV3(@Body parm: RequestBody): DashboardResponse


    @POST("Tracker/TrackUser")
    suspend fun userTrack(@Body parm: RequestBody): BasicResponse

    @POST("Tracker/SessionTrack")
    suspend fun userSessionTrack(@Body parm: RequestBody): BasicResponse

    // Subscription
    @POST("DeenPayment/getPaymentMethod")
    suspend fun getSubscriptionPageData(
        @Body parm: RequestBody
    ): SubscriptionPageResponse

    @POST("Advertisement/GetAdvertisement")
    suspend fun getAdvertisement(
        @Body parm: RequestBody
    ): AdvertisementResponse

    @POST("Advertisement/SaveAdvertisementRecord")
    suspend fun saveAdvertisementrecord(
        @Body parm: RequestBody
    ): BasicResponse


}