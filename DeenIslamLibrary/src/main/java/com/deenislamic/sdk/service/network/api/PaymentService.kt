package com.deenislamic.sdk.service.network.api

import com.deenislamic.sdk.service.network.response.common.BasicResponse
import com.deenislamic.sdk.service.network.response.payment.DCBChargeResponse
import com.deenislamic.sdk.service.network.response.payment.SSLPaymentResponse
import com.deenislamic.sdk.service.network.response.payment.recurring.CheckRecurringResponse
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

    @POST("DeenRecurring/BkashUserStatusBySubID")
    suspend fun checkRecSubscription(@Body parm: RequestBody): CheckRecurringResponse

    @POST("DeenRecurring/CancelSubscription")
    suspend fun cancelAutoRenewal(@Body parm: RequestBody): BasicResponse

    @POST("DeenRecurring/InitiateRecurringPayment")
    suspend fun deenRecurringPayment(@Body parm: RequestBody): BasicResponse

    @POST("deenApp/Donation")
    suspend fun bKashDonation(@Body parm: RequestBody): BasicResponse

    @POST("grameenphone/checkout/create-checkout-session/deen")
    suspend fun dcbGPCharge(@Body parm: RequestBody): DCBChargeResponse

    @POST("gpdcb/CancelSubscription")
    suspend fun cancelGPDcbAutoRenewal(@Body parm: RequestBody): BasicResponse


}