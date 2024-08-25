package com.deenislamic.sdk.service.repository;

import com.deenislamic.sdk.DeenSDKCore
import com.deenislamic.sdk.service.network.ApiCall
import com.deenislamic.sdk.service.network.AuthInterceptor
import com.deenislamic.sdk.service.network.api.AuthenticateService
import com.deenislamic.sdk.service.network.api.NagadPaymentService
import com.deenislamic.sdk.service.network.api.PaymentService
import com.deenislamic.sdk.utils.RequestBodyMediaType
import com.deenislamic.sdk.utils.toRequestBody
import org.json.JSONObject

internal class PaymentRepository(
    private val paymentService: PaymentService?,
    private val nagadPaymentService: NagadPaymentService?,
    private val authInterceptor: AuthInterceptor?,
    private val dcbPaymentService : PaymentService? = null,
    private val authenticateService: AuthenticateService? = null
) : ApiCall {

    suspend fun login() = makeApicall {

        val body = JSONObject()
        body.put("username", "noorapp")
        body.put("password", "noorgakk")

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

        paymentService?.login(requestBody)

    }

    suspend fun bKashPayment(serviceID: String, msisdn: String, token: String,device:String = "gpsdk") = makeApicall {

        authInterceptor?.tempAccessToken = token

        val body = JSONObject()
        body.put("serviceID", serviceID)
        body.put("msisdn", msisdn)
        body.put("device", device)
        body.put("callBackUrl", "https://www.google.com")

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        paymentService?.bKashSub(requestBody)

    }

    suspend fun sslPayment(
        serviceID: Int,
        msisdn: String,
        token: String,
        reference: String? = null
    ) = makeApicall {

        authInterceptor?.tempAccessToken = token

        val body = JSONObject()
        body.put("serviceID", serviceID)
        body.put("msisdn", msisdn)
        body.put("device", "gpsdk")
        body.put("Reference", reference)
        body.put("callBackUrl", "https://www.google.com")

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        paymentService?.sslSub(requestBody)

    }

    suspend fun nagadPayment(serviceID: String, msisdn: String) = makeApicall {

        val body = JSONObject()
        body.put("serviceId", serviceID)
        body.put("msisdn", msisdn)

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        nagadPaymentService?.nagadSub(requestBody)
    }

    suspend fun getServicePaymentList(service: String) = makeApicall {

        val body = JSONObject()
        body.put("device", service)
        body.put("language", DeenSDKCore.GetDeenLanguage())

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        authenticateService?.getServicePaymentList(requestBody)
    }

    suspend fun deenRecurringPayment(serviceID: String, msisdn: String, token: String) = makeApicall {

        authInterceptor?.tempAccessToken = token

        val body = JSONObject()
        body.put("serviceID", serviceID)
        body.put("msisdn", msisdn)
        body.put("device", "gpsdk")
        body.put("callBackUrl", "https://www.google.com")

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        paymentService?.deenRecurringPayment(requestBody)

    }

    suspend fun bKashDonationPayment(amount: String, msisdn: String, token: String) = makeApicall {

        authInterceptor?.tempAccessToken = token

        val body = JSONObject()
        body.put("amount", amount)
        body.put("msisdn", msisdn)
        body.put("device", "App")
        body.put("callBackUrl", "https://www.google.com")

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        paymentService?.bKashDonation(requestBody)

    }

    suspend fun dcbGPCharge(msisdn: String, serviceId: String) = makeApicall {

        authInterceptor?.dcbPayment = true

        val body = JSONObject()
        body.put("MSISDN", msisdn)
        body.put("ServiceId", serviceId)
        body.put("PaymentSuccessRedirectUrl","https://payment.islamicservice.net/success")
        body.put("PaymentFailedRedirectUrl","https://payment.islamicservice.net/failed")
        body.put("paymentDenyRedirectUrl","https://payment.islamicservice.net/failed")
        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        dcbPaymentService?.dcbGPCharge(requestBody)

    }


} 