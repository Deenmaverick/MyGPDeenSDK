package com.deenislam.sdk.service.repository;

import com.deenislam.sdk.service.network.ApiCall
import com.deenislam.sdk.service.network.AuthInterceptor
import com.deenislam.sdk.service.network.api.AuthenticateService
import com.deenislam.sdk.service.network.api.DeenService
import com.deenislam.sdk.service.network.api.PaymentService
import com.deenislam.sdk.utils.RequestBodyMediaType
import com.deenislam.sdk.utils.toRequestBody
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
        body.put("device", "blsdk")

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        paymentService?.checkRecSubscription(requestBody)

    }

    suspend fun cancelAutoRenewal(token:String, msisdn:String, serviceId: Int) = makeApicall {

        authInterceptor?.tempAccessToken = token

        val body = JSONObject()
        body.put("msisdn", msisdn)
        body.put("device", "blsdk")
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