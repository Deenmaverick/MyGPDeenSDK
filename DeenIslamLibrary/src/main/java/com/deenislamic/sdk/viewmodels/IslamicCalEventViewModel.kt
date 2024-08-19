package com.deenislamic.sdk.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.IslamicCalEventResource
import com.deenislamic.sdk.service.network.ApiResource
import com.deenislamic.sdk.service.repository.IslamicCalendarEventRepository
import kotlinx.coroutines.launch


internal class IslamicCalEventViewModel(
    private val repository: IslamicCalendarEventRepository
): ViewModel() {
    private val _islamicCalEventData: MutableLiveData<IslamicCalEventResource> = MutableLiveData()
    val islamicCalEventData: MutableLiveData<IslamicCalEventResource> get() = _islamicCalEventData

    suspend fun getIslamicCalEvent(language: String) {
        viewModelScope.launch {

            val response = repository.getIslamicCalendarEvent(language)

            when(response) {
                is ApiResource.Failure -> _islamicCalEventData.value = CommonResource.API_CALL_FAILED

                is ApiResource.Success ->
                {
                    if(response.value?.Success == true)
                        _islamicCalEventData.value = IslamicCalEventResource.IslamicCalendarEvents(
                            listOf(response.value.Data) )
                    else
                        _islamicCalEventData.value = CommonResource.EMPTY
                }
            }
        }
    }

    suspend fun getIslamicCalendar(date:String,language: String) {
        viewModelScope.launch {

            val response = repository.getIslamicCalendar(date,language)

            when(response)
            {
                is ApiResource.Failure -> _islamicCalEventData.value = CommonResource.API_CALL_FAILED

                is ApiResource.Success ->
                {
                    if(response.value?.Success == true)
                        _islamicCalEventData.value = IslamicCalEventResource.IslamicCalendar(response.value.Data)
                    else
                        _islamicCalEventData.value = CommonResource.EMPTY
                }
            }
        }
    }
}