package com.deenislam.sdk.viewmodels;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislam.sdk.DeenSDKCore
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.SubscriptionResource
import com.deenislam.sdk.service.network.ApiResource
import com.deenislam.sdk.service.repository.PaymentRepository
import com.deenislam.sdk.service.repository.SubscriptionRepository
import kotlinx.coroutines.launch


internal class SubscriptionViewModel(
    private val paymentRepository: PaymentRepository,
    private val repository: SubscriptionRepository,
) : ViewModel() {

    private val _subscriptionLiveData:MutableLiveData<SubscriptionResource> = MutableLiveData()
    val subscriptionLiveData:MutableLiveData<SubscriptionResource> get() = _subscriptionLiveData

    private val _autorenewLiveData:MutableLiveData<SubscriptionResource> = MutableLiveData()
    val autorenewLiveData:MutableLiveData<SubscriptionResource> get() = _autorenewLiveData


    suspend fun checkRecurringStatus(){

        viewModelScope.launch {


            when (val loginResponse = paymentRepository.login()) {
                is ApiResource.Failure -> _subscriptionLiveData.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success -> {
                    if (loginResponse.value?.Success == true) {

                        if (loginResponse.value.Data.isNotEmpty())
                            processRecurringCheck(
                                msisdn = DeenSDKCore.GetDeenMsisdn(),
                                token = loginResponse.value.Data
                            )
                        else
                            _subscriptionLiveData.value = CommonResource.API_CALL_FAILED
                    } else
                        CommonResource.API_CALL_FAILED
                }
            }
        }
    }

    private fun processRecurringCheck(msisdn: String, token: String){
        if (msisdn.isEmpty())
            _subscriptionLiveData.value = CommonResource.API_CALL_FAILED
        else {

            viewModelScope.launch {
                when (val sub = repository.checkSubscription(token = token, msisdn = msisdn)) {
                    is ApiResource.Failure -> _subscriptionLiveData.value =
                        CommonResource.API_CALL_FAILED

                    is ApiResource.Success -> {
                        if (sub.value?.Success == true && sub.value.Message.isNotEmpty())
                            _subscriptionLiveData.value = SubscriptionResource.CheckSubs(sub.value)
                        else
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