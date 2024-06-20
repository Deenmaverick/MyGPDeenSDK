package com.deenislamic.sdk.service.network.api

import com.deenislamic.sdk.service.network.response.NagadResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

internal interface NagadPaymentService {
    @POST("payment/create-payment")
    suspend fun nagadSub(@Body parm: RequestBody): NagadResponse
}