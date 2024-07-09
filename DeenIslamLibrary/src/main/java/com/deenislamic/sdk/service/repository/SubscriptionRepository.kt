package com.deenislamic.sdk.service.repository;

import com.deenislamic.sdk.service.network.ApiCall
import com.deenislamic.sdk.service.network.AuthInterceptor
import com.deenislamic.sdk.service.network.api.AuthenticateService
import com.deenislamic.sdk.service.network.api.PaymentService
import com.deenislamic.sdk.utils.RequestBodyMediaType
import com.deenislamic.sdk.utils.toRequestBody
import org.json.JSONObject

internal class SubscriptionRepository(
    private val paymentService: PaymentService?,
    private val authInterceptor: AuthInterceptor?,
    private val authenticateService: AuthenticateService?
) : ApiCall {


    suspend fun checkSubscription(token:String,msisdn:String) = makeApicall {

        authInterceptor?.tempAccessToken = token

        val body = JSONObject()
        body.put("msisdn", msisdn)
        body.put("device", "gpsdk")

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        paymentService?.checkRecSubscription(requestBody)

    }

    suspend fun cancelAutoRenewal(token:String, msisdn:String, serviceId: Int) = makeApicall {

        authInterceptor?.tempAccessToken = token

        val body = JSONObject()
        body.put("msisdn", msisdn)
        body.put("device", "gpsdk")
        body.put("ServiceId",serviceId)

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        paymentService?.cancelAutoRenewal(requestBody)

    }

    suspend fun getPageData(language:String) = makeApicall {

        val body = JSONObject()
        body.put("language", language)
        body.put("device", "sdk")
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        authenticateService?.getSubscriptionPageData(requestBody)
    }
} 