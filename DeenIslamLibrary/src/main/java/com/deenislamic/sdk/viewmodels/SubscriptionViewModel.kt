package com.deenislamic.sdk.viewmodels;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislamic.sdk.DeenSDKCore
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.SubscriptionResource
import com.deenislamic.sdk.service.network.ApiResource
import com.deenislamic.sdk.service.network.response.payment.recurring.CheckRecurringResponse
import com.deenislamic.sdk.service.network.response.subscription.PaymentType
import com.deenislamic.sdk.service.repository.PaymentRepository
import com.deenislamic.sdk.service.repository.SubscriptionRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


internal class SubscriptionViewModel(
    private val paymentRepository: PaymentRepository,
    private val repository: SubscriptionRepository,
) : ViewModel() {

    private val _subscriptionLiveData:MutableLiveData<SubscriptionResource> = MutableLiveData()
    val subscriptionLiveData:MutableLiveData<SubscriptionResource> get() = _subscriptionLiveData

    private val _autorenewLiveData:MutableLiveData<SubscriptionResource> = MutableLiveData()
    val autorenewLiveData:MutableLiveData<SubscriptionResource> get() = _autorenewLiveData


    suspend fun checkRecurringStatus(language: String){

        viewModelScope.launch {

            when (val loginResponse = paymentRepository.login()) {
                is ApiResource.Failure -> _subscriptionLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success -> {
                    if (loginResponse.value?.Success == true) {

                        if (loginResponse.value.Data.isNotEmpty())
                            processRecurringCheck(
                                msisdn = DeenSDKCore.GetDeenMsisdn(),
                                token = loginResponse.value.Data,
                                language
                            )
                        else
                            _subscriptionLiveData.value = CommonResource.API_CALL_FAILED
                    } else
                        CommonResource.API_CALL_FAILED
                }
            }
        }
    }

    private fun processRecurringCheck(msisdn: String, token: String, language: String){
        if (msisdn.isEmpty())
            _subscriptionLiveData.value = CommonResource.API_CALL_FAILED
        else {

            viewModelScope.launch {

                val sub = async {repository.checkSubscription(token = token, msisdn = msisdn)}.await()

                var subsData: CheckRecurringResponse?=null

                when (sub) {
                    is ApiResource.Failure -> _subscriptionLiveData.value =
                        CommonResource.API_CALL_FAILED

                    is ApiResource.Success -> {
                        if (sub.value?.Success == true && sub.value.Message.isNotEmpty())
                            subsData = sub.value
                        else
                            _subscriptionLiveData.value = CommonResource.API_CALL_FAILED
                    }
                }

                when(val response = repository.getPageData(language)){
                    is ApiResource.Failure -> _subscriptionLiveData.value = CommonResource.API_CALL_FAILED
                    is ApiResource.Success -> {
                        if(response.value?.Success == true){
                            if(subsData!=null)
                            _subscriptionLiveData.value = SubscriptionResource.CheckSubs(response.value.Data.copy(pageResponse = subsData))
                            else
                                _subscriptionLiveData.value = CommonResource.API_CALL_FAILED
                        }else
                            _subscriptionLiveData.value = CommonResource.API_CALL_FAILED
                    }
                }


            }
        }
    }

    suspend fun cancelAutoRenewal(planStatus: String, selectedPlan: Int) {

        viewModelScope.launch {


            when (val loginResponse = paymentRepository.login()) {
                is ApiResource.Failure -> _autorenewLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success -> {
                    if (loginResponse.value?.Success == true) {

                        if (loginResponse.value.Data.isNotEmpty())
                            processRenewCancelCheck(
                                msisdn = DeenSDKCore.GetDeenMsisdn(),
                                token = loginResponse.value.Data,
                                planStatus = planStatus,
                                selectedPlan = selectedPlan
                            )
                        else
                            _autorenewLiveData.value = CommonResource.API_CALL_FAILED
                    } else
                        CommonResource.API_CALL_FAILED
                }
            }
        }
    }

    suspend fun cancelAutoRenewal(
        planStatus: String,
        selectedPlan: Int,
        selectedPaymentType: PaymentType?
    ) {

        viewModelScope.launch {


            when (val loginResponse = paymentRepository.login()) {
                is ApiResource.Failure -> _autorenewLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success -> {
                    if (loginResponse.value?.Success == true) {

                        if (loginResponse.value.Data.isNotEmpty())
                            processRenewCancelCheck(
                                msisdn = DeenSDKCore.GetDeenMsisdn(),
                                token = loginResponse.value.Data,
                                planStatus = planStatus,
                                selectedPlan = selectedPlan,
                                selectedPaymentType
                            )
                        else
                            _autorenewLiveData.value = CommonResource.API_CALL_FAILED
                    } else
                        _autorenewLiveData.value = CommonResource.API_CALL_FAILED
                }
            }
        }
    }


    private fun processRenewCancelCheck(
        msisdn: String,
        token: String,
        planStatus: String,
        selectedPlan: Int,
        selectedPaymentType: PaymentType?
    ){
        if (msisdn.isEmpty())
            _autorenewLiveData.value = CommonResource.API_CALL_FAILED
        else {
            viewModelScope.launch {
                when (val sub = repository.cancelAutoRenewal(
                    token = token, msisdn = msisdn,
                    serviceId = selectedPlan,
                    selectedPaymentType = selectedPaymentType
                )) {
                    is ApiResource.Failure -> _autorenewLiveData.value =
                        CommonResource.API_CALL_FAILED

                    is ApiResource.Success -> {
                        if (sub.value?.Success == true && sub.value?.Message?.isNotEmpty() == true)
                            _autorenewLiveData.value = SubscriptionResource.AutoRenewCancel(planStatus)
                        else
                            _autorenewLiveData.value = CommonResource.API_CALL_FAILED
                    }
                }
            }
        }
    }

    private fun processRenewCancelCheck(
        msisdn: String,
        token: String,
        planStatus: String,
        selectedPlan: Int
    ){
        if (msisdn.isEmpty())
            _autorenewLiveData.value = CommonResource.API_CALL_FAILED
        else {
            viewModelScope.launch {
                when (val sub = repository.cancelAutoRenewal(token = token, msisdn = msisdn,selectedPlan)) {
                    is ApiResource.Failure -> _autorenewLiveData.value =
                        CommonResource.API_CALL_FAILED

                    is ApiResource.Success -> {
                        if (sub.value?.Success == true && sub.value.Message.isNotEmpty())
                            _autorenewLiveData.value = SubscriptionResource.AutoRenewCancel(planStatus)
                        else
                            _autorenewLiveData.value = CommonResource.API_CALL_FAILED
                    }
                }
            }
        }
    }

    fun clearAutoRenew(){
        _autorenewLiveData.value = CommonResource.CLEAR
    }
    fun clearCheckSub(){
        _subscriptionLiveData.value = CommonResource.CLEAR
    }

}