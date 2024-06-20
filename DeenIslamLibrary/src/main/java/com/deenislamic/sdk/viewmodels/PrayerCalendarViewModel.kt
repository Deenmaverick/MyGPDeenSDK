package com.deenislamic.sdk.viewmodels;

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.prayer_time.PrayerCalendarResource
import com.deenislamic.sdk.service.network.ApiResource
import com.deenislamic.sdk.service.network.response.prayer_calendar.PrayerCalendarResponse
import com.deenislamic.sdk.service.repository.PrayerCalendarRespository
import kotlinx.coroutines.launch

internal class PrayerCalendarViewModel (
    private val repository: PrayerCalendarRespository
) : ViewModel() {

    private val _calendarLiveData:MutableLiveData<PrayerCalendarResource> = MutableLiveData()
    val  calendarLiveData:MutableLiveData<PrayerCalendarResource> get() = _calendarLiveData

    fun getMonthlyData(localtion:String, language:String)
    {
        viewModelScope.launch {
            processPrayerTimes(repository.getMonthlyData(localtion = localtion,language= language))
        }
    }
    private fun processPrayerTimes(response: ApiResource<PrayerCalendarResponse>)
    {
        when(response)
        {
            is ApiResource.Failure -> _calendarLiveData.value = CommonResource.API_CALL_FAILED
            is ApiResource.Success ->
            {
                if(response.value.Data.size>0)
                _calendarLiveData.value = PrayerCalendarResource.monthlyData(response.value.Data)
                else
                    _calendarLiveData.value = CommonResource.EMPTY
            }
        }
    }
}