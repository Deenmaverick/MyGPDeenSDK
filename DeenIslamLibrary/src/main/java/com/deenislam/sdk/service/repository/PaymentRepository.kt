package com.deenislam.sdk.service.repository;

import com.deenislam.sdk.service.network.ApiCall
import com.deenislam.sdk.service.network.AuthInterceptor
import com.deenislam.sdk.service.network.api.NagadPaymentService
import com.deenislam.sdk.service.network.api.PaymentService
import com.deenislam.sdk.utils.RequestBodyMediaType
import com.deenislam.sdk.utils.toRequestBody
import org.json.JSONObject

internal class PaymentRepository(
    private val paymentService: PaymentService?,
    private val nagadPaymentService: NagadPaymentService?,
    private val authInterceptor: AuthInterceptor?
) : ApiCall {

    suspend fun login() = makeApicall {

        val body = JSONObject()
        body.put("username", "noorapp")
        body.put("password", "noorgakk")

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)

        paymentService?.login(requestBody)

    }

    suspend fun bKashPayment(serviceID: Int, msisdn: String, token: String) = makeApicall {

        authInterceptor?.tempAccessToken = token

        val body = JSONObject()
        body.put("serviceID", serviceID)
        body.put("msisdn", msisdn)
        body.put("device", "blsdk")
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
        body.put("device", "blsdk")
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

    suspend fun googlePaySave(
        service: String,
        msisdn: String,
        transactionNo: String,
        startDate: String,
        token: String
    ) = makeApicall {

        authInterceptor?.tempAccessToken = token
        val body = JSONObject()
        body.put("service", service)
        body.put("msisdn", msisdn)
        body.put("transactionNo", transactionNo)
        body.put("startDate", startDate)

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        paymentService?.saveGpayInfo(requestBody)
    }

    suspend fun deenRecurringPayment(serviceID: Int, msisdn: String, token: String) = makeApicall {

        authInterceptor?.tempAccessToken = token

        val body = JSONObject()
        body.put("serviceID", serviceID)
        body.put("msisdn", msisdn)
        body.put("device", "blsdk")
        body.put("callBackUrl", "https://www.google.com")

        val requestBody = body.toString().toRequestBody(RequestBodyMediaType)
        paymentService?.deenRecurringPayment(requestBody)

    }
} 