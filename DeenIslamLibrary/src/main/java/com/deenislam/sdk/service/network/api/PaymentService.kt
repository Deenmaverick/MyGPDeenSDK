package com.deenislam.sdk.service.network.api

import com.deenislam.sdk.service.network.response.common.BasicResponse
import com.deenislam.sdk.service.network.response.payment.SSLPaymentResponse
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.POST

internal interface PaymentService {

    @POST("api/User/login")
    suspend fun login(@Body parm: RequestBody): BasicResponse

    @POST("deenOndemand/Subcription")
    suspend fun bKashSub(@Body parm: RequestBody): BasicResponse

    @POST("sslOndemand/Subcription")
    suspend fun sslSub(@Body parm: RequestBody): SSLPaymentResponse

    @POST("api/GPAY/SAVEGPAYRESPONSE")
    suspend fun saveGpayInfo(@Body parm: RequestBody): BasicResponse
}