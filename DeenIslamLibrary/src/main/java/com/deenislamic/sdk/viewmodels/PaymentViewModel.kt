package com.deenislamic.sdk.viewmodels;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislamic.sdk.DeenSDKCore
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.PaymentResource
import com.deenislamic.sdk.service.network.ApiResource
import com.deenislamic.sdk.service.repository.PaymentRepository
import kotlinx.coroutines.launch


internal class PaymentViewModel(
    private val paymentRepository: PaymentRepository
) : ViewModel() {

    private val _paymentLiveData: MutableLiveData<PaymentResource> = MutableLiveData()
    val paymentLiveData: MutableLiveData<PaymentResource> get() = _paymentLiveData

    private val _paymentIPNLiveData: MutableLiveData<PaymentResource> = MutableLiveData()
    val paymentIPNLiveData: MutableLiveData<PaymentResource> get() = _paymentIPNLiveData

    var isIPNCalled = false

    private val _paymentListLiveData: MutableLiveData<PaymentResource> = MutableLiveData()
    val paymentListLiveData: MutableLiveData<PaymentResource> get() = _paymentListLiveData



    //bKash Payment
    suspend fun bKashPayment(
        serviceID: String,
        device:String = "gpsdk"
    ) {
        viewModelScope.launch {

            when (val loginResponse = paymentRepository.login()) {
                is ApiResource.Failure -> _paymentLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success -> {
                    if (loginResponse.value?.Success == true) {

                        if (loginResponse.value.Data.isNotEmpty())
                            processBkashSub(
                                serviceID = serviceID,
                                msisdn = DeenSDKCore.GetDeenMsisdn(),
                                token = loginResponse.value.Data,
                                device
                            )
                        else
                            _paymentLiveData.value = CommonResource.API_CALL_FAILED
                    } else
                        CommonResource.API_CALL_FAILED
                }
            }
        }
    }


    private suspend fun processBkashSub(
        serviceID: String,
        msisdn: String,
        token: String,
        device: String
    ) {
        if (msisdn.isEmpty())
            _paymentLiveData.value = CommonResource.API_CALL_FAILED
        else {

            viewModelScope.launch {
                when(val sub = paymentRepository.bKashPayment(serviceID, msisdn, token,device)) {
                    is ApiResource.Failure -> _paymentLiveData.value =
                        CommonResource.API_CALL_FAILED

                    is ApiResource.Success -> {
                        if (sub.value?.Success == true && sub.value.Message.isNotEmpty())
                            _paymentLiveData.value = PaymentResource.PaymentUrl(sub.value.Message)
                        else
                            _paymentLiveData.value = CommonResource.API_CALL_FAILED
                    }
                }
            }
        }
    }


    //SSL PAYMENT
    suspend fun sslPayment(
        serviceID: Int, reference: String? = null
    ) {
        viewModelScope.launch {


            when (val loginResponse = paymentRepository.login()) {
                is ApiResource.Failure -> _paymentLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success -> {
                    if (loginResponse.value?.Success == true) {

                        if (loginResponse.value.Data.isNotEmpty())
                            processSSLSub(
                                serviceID = serviceID,
                                msisdn = DeenSDKCore.GetDeenMsisdn(),
                                token = loginResponse.value.Data.toString(),
                                reference = reference
                            )
                        else
                            _paymentLiveData.value = CommonResource.API_CALL_FAILED
                    } else
                        CommonResource.API_CALL_FAILED
                }
            }
        }
    }

    private suspend fun processSSLSub(
        serviceID: Int,
        msisdn: String,
        token: String,
        reference: String? = null
    ) {
        if (msisdn.isEmpty())
            _paymentLiveData.value = CommonResource.API_CALL_FAILED
        else {

            viewModelScope.launch {
                when (val sub = paymentRepository.sslPayment(serviceID, msisdn, token, reference)) {
                    is ApiResource.Failure -> _paymentLiveData.value =
                        CommonResource.API_CALL_FAILED

                    is ApiResource.Success -> {
                        if (sub.value?.errorCode == "200")
                            _paymentLiveData.value =
                                PaymentResource.PaymentUrl(sub.value.GatewayPageURL)
                        else
                            _paymentLiveData.value = CommonResource.API_CALL_FAILED
                    }
                }
            }
        }
    }

    suspend fun nagadPayment(
        serviceID: String
    ) {
        viewModelScope.launch {

            when (val loginResponse = paymentRepository.login()) {
                is ApiResource.Failure -> _paymentLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success -> {
                    if (loginResponse.value?.Success == true) {

                        if (loginResponse.value.Data.isNotEmpty())
                            processNagadSub(
                                serviceID = serviceID,
                                msisdn = DeenSDKCore.GetDeenMsisdn()
                            )
                        else
                            _paymentLiveData.value = CommonResource.API_CALL_FAILED
                    } else
                        CommonResource.API_CALL_FAILED
                }
            }
        }
    }

    private suspend fun processNagadSub(serviceID: String, msisdn: String) {
        if (msisdn.isEmpty())
            _paymentLiveData.value = CommonResource.API_CALL_FAILED
        else {

            viewModelScope.launch {
                when (val sub = paymentRepository.nagadPayment(serviceID, msisdn)) {
                    is ApiResource.Failure -> _paymentLiveData.value =
                        CommonResource.API_CALL_FAILED

                    is ApiResource.Success -> {
                        if (sub.value?.success == true && sub.value.data.isNotEmpty())
                            _paymentLiveData.value =
                                PaymentResource.PaymentUrlNagad(sub.value.success, sub.value.data)
                        else
                            _paymentLiveData.value = sub.value?.let {
                                PaymentResource.PaymentUrlNagad(
                                    it.success,
                                    it.message
                                )
                            }
                    }
                }
            }
        }
    }

    suspend fun getServicePaymentList(serviceID:String) {
        viewModelScope.launch {

            when(val response = paymentRepository.getServicePaymentList(serviceID)){
                is ApiResource.Failure -> _paymentListLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success -> {
                    if(response.value?.Success == true){
                            _paymentListLiveData.value = PaymentResource.ServicePaymentList(response.value.Data)
                    }else
                        _paymentListLiveData.value = CommonResource.API_CALL_FAILED
                }
            }
        }
    }



    fun ipnCallback(status: Int) {
        isIPNCalled = true
        when (status) {
            200 -> _paymentIPNLiveData.value = PaymentResource.PaymentIPNSuccess
            400 -> _paymentIPNLiveData.value = PaymentResource.PaymentIPNFailed
            100 -> _paymentIPNLiveData.value = PaymentResource.PaymentIPNCancle
        }

    }

    fun clearIPN() {
        isIPNCalled = false
        _paymentIPNLiveData.value = CommonResource.CLEAR
    }

    fun clearPayment() {
        _paymentLiveData.value = CommonResource.CLEAR
    }


    //Deen recurring payment
    suspend fun recurringPayment(
        serviceID: String
    ) {
        viewModelScope.launch {


            when (val loginResponse = paymentRepository.login()) {
                is ApiResource.Failure -> _paymentLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success -> {
                    if (loginResponse.value?.Success == true) {

                        if (loginResponse.value.Data.isNotEmpty())
                            processRecurringSub(
                                serviceID = serviceID,
                                msisdn = DeenSDKCore.GetDeenMsisdn(),
                                token = loginResponse.value.Data
                            )
                        else
                            _paymentLiveData.value = CommonResource.API_CALL_FAILED
                    } else
                        CommonResource.API_CALL_FAILED
                }
            }
        }
    }


    private suspend fun processRecurringSub(serviceID: String, msisdn: String, token: String) {
        if (msisdn.isEmpty())
            _paymentLiveData.value = CommonResource.API_CALL_FAILED
        else {

            viewModelScope.launch {
                when (val sub = paymentRepository.deenRecurringPayment(serviceID, msisdn, token)) {
                    is ApiResource.Failure -> _paymentLiveData.value =
                        CommonResource.API_CALL_FAILED

                    is ApiResource.Success -> {
                        if (sub.value?.Success == true && sub.value.Message.isNotEmpty())
                            _paymentLiveData.value = PaymentResource.PaymentUrl(sub.value.Data)
                        else
                            _paymentLiveData.value = CommonResource.API_CALL_FAILED
                    }
                }
            }
        }
    }


    suspend fun bKashDonationPayment(
        amount: String
    ) {
        viewModelScope.launch {


            when (val loginResponse = paymentRepository.login()) {
                is ApiResource.Failure -> _paymentLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success -> {
                    if (loginResponse.value?.Success == true) {

                        if (loginResponse.value.Data.isNotEmpty())
                            processBkashDonation(
                                amount = amount,
                                msisdn = DeenSDKCore.GetDeenMsisdn(),
                                token = loginResponse.value.Data
                            )
                        else
                            _paymentLiveData.value = CommonResource.API_CALL_FAILED
                    } else
                        CommonResource.API_CALL_FAILED
                }
            }
        }
    }

    private suspend fun processBkashDonation(amount: String, msisdn: String, token: String) {
        if (msisdn.isEmpty())
            _paymentLiveData.value = CommonResource.API_CALL_FAILED
        else {

            viewModelScope.launch {
                when (val sub = paymentRepository.bKashDonationPayment(amount, msisdn, token)) {
                    is ApiResource.Failure -> _paymentLiveData.value =
                        CommonResource.API_CALL_FAILED

                    is ApiResource.Success -> {
                        if (sub.value?.Success == true && sub.value.Message.isNotEmpty())
                            _paymentLiveData.value = PaymentResource.PaymentUrl(sub.value.Message,true)
                        else
                            _paymentLiveData.value = CommonResource.API_CALL_FAILED
                    }
                }
            }
        }
    }

    suspend fun dcbGPCharging(serviceID: String) {
        viewModelScope.launch {

            when(val response = paymentRepository.dcbGPCharge(DeenSDKCore.GetDeenMsisdn(),serviceID)){
                is ApiResource.Failure -> _paymentLiveData.value =
                    CommonResource.API_CALL_FAILED
                is ApiResource.Success -> {
                    if (response.value?.data?.paymentUrl.toString().isNotEmpty())
                        _paymentLiveData.value = PaymentResource.PaymentUrl(response.value?.data?.paymentUrl.toString())
                    else
                        _paymentLiveData.value = CommonResource.API_CALL_FAILED

                }
            }
        }
    }


}