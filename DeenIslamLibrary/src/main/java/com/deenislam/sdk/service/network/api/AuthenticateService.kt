package com.deenislam.sdk.service.network.api

import com.deenislam.sdk.service.network.response.BasicResponse
import com.deenislam.sdk.service.network.response.auth.login.LoginResponse
import com.deenislam.sdk.service.network.response.dashboard.DashboardResponse
import com.deenislam.sdk.service.network.response.subscription.SubscriptionPageResponse
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

}