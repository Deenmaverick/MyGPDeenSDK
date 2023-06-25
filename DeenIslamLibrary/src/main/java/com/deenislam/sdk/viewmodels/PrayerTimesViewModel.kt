package com.deenislam.sdk.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislam.sdk.service.database.entity.PrayerNotification
import com.deenislam.sdk.service.database.entity.PrayerTimes
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.prayer_time.PrayerNotificationResource
import com.deenislam.sdk.service.models.prayer_time.PrayerTimeResource
import com.deenislam.sdk.service.network.ApiResource
import com.deenislam.sdk.service.network.response.prayertimes.PrayerTimesResponse
import com.deenislam.sdk.service.repository.PrayerTimesRepository
import kotlinx.coroutines.launch


internal class PrayerTimesViewModel(
    private val prayerTimesRepository: PrayerTimesRepository
):ViewModel() {

    private val _prayerTimes:MutableLiveData<PrayerTimeResource> = MutableLiveData()
    val prayerTimes:MutableLiveData<PrayerTimeResource> get() = _prayerTimes

    private val _prayerTimesNotification:MutableLiveData<PrayerNotificationResource> = MutableLiveData()
    val prayerTimesNotification:MutableLiveData<PrayerNotificationResource> get() = _prayerTimesNotification

    fun getPrayerTimes(localtion:String,language:String,requiredDate:String)
    {
        viewModelScope.launch {
            processPrayerTimeResponse(prayerTimesRepository.getPrayerTimes(localtion,language,requiredDate) as ApiResource<PrayerTimesResponse>)
        }
    }

    private fun processPrayerTimeResponse(response: ApiResource<PrayerTimesResponse>)
    {
        when(response)
        {
            is ApiResource.Failure -> _prayerTimes.value = CommonResource.API_CALL_FAILED
            is ApiResource.Success ->
                if(response.value.Success)
                _prayerTimes.value = PrayerTimeResource.postPrayerTime(response.value)
            else
                _prayerTimes.value = PrayerTimeResource.prayerTimeEmpty

        }
    }

    fun getNotificationData(date: String, prayer_tag: String)
    {
        viewModelScope.launch {
            _prayerTimesNotification.value = PrayerNotificationResource.notificationData(prayerTimesRepository.getNotificationData(date,prayer_tag))
        }
    }

    fun setNotificationData(
        date: String,
        prayer_tag: String,
        state: Int = 0,
        sound_file: String = "",
        prayerTimesResponse: PrayerTimesResponse?
    )
    {
        viewModelScope.launch {
            _prayerTimesNotification.value = PrayerNotificationResource.setNotification(prayerTimesRepository.updatePrayerNotification(date,prayer_tag = prayer_tag, state =state, sound_file = sound_file,prayerTimesResponse),
                prayerTimesRepository.getDateWiseNotificationData(date) as ArrayList<PrayerNotification>)
        }
    }

    fun clearLiveData()
    {
        _prayerTimesNotification.value = CommonResource.EMPTY
    }

    fun cachePrayerTime(date:String, data: PrayerTimes)
    {
        viewModelScope.launch {
            prayerTimesRepository.chachePrayerTimes(date,data)
        }
    }

    fun getDateWisePrayerNotificationData(date:String)
    {
        viewModelScope.launch {
            _prayerTimesNotification.value = PrayerNotificationResource.dateWiseNotificationData(prayerTimesRepository.getDateWiseNotificationData(date) as ArrayList<PrayerNotification>)
        }
    }

    fun setPrayerTrack(date:String,prayer_tag: String,bol:Boolean)
    {
        viewModelScope.launch {
            _prayerTimesNotification.value = PrayerNotificationResource.dateWiseNotificationData(prayerTimesRepository.setPrayerTrack(date=date,prayer_tag=prayer_tag,bol=bol) as ArrayList<PrayerNotification>)
        }
    }

}