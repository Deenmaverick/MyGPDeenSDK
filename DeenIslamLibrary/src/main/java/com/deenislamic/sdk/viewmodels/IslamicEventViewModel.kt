package com.deenislamic.sdk.viewmodels;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.IslamicEventsResource
import com.deenislamic.sdk.service.network.ApiResource
import com.deenislamic.sdk.service.repository.IslamicEventRepository
import kotlinx.coroutines.launch


internal class IslamicEventViewModel(
    private val repository: IslamicEventRepository
) : ViewModel() {

    private val _islamicEventLiveData: MutableLiveData<IslamicEventsResource> =
        MutableLiveData()
    val islamicEvent: MutableLiveData<IslamicEventsResource> get() = _islamicEventLiveData

    fun getIslamicEvents(language: String) {
        viewModelScope.launch {

            when (val response = repository.getIslamicEevents(language = language)) {
                is ApiResource.Failure -> _islamicEventLiveData.value =
                    CommonResource.API_CALL_FAILED

                is ApiResource.Success -> {
                    if (response.value?.Success == true) {
                        if (response.value.data.isNotEmpty()) {
                            _islamicEventLiveData.value =
                                IslamicEventsResource.islamicEvents(response.value.data)
                        } else {
                            _islamicEventLiveData.value = CommonResource.EMPTY
                        }

                    } else {
                        _islamicEventLiveData.value = CommonResource.API_CALL_FAILED
                    }

                }
            }
        }
    }
}