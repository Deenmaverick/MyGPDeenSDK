package com.deenislam.sdk.viewmodels

import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislam.sdk.service.database.entity.PrayerNotification
import com.deenislam.sdk.service.database.entity.PrayerTimes
import com.deenislam.sdk.service.models.CommonResource
import com.deenislam.sdk.service.models.prayer_time.PrayerNotificationResource
import com.deenislam.sdk.service.models.prayer_time.PrayerTimeResource
import com.deenislam.sdk.service.models.ramadan.StateModel
import com.deenislam.sdk.service.network.ApiResource
import com.deenislam.sdk.service.network.response.prayertimes.PrayerTimesResponse
import com.deenislam.sdk.service.network.response.prayertimes.tracker.PrayerTrackResponse
import com.deenislam.sdk.service.repository.PrayerTimesRepository
import kotlinx.coroutines.launch


internal class PrayerTimesViewModel(
    private val prayerTimesRepository: PrayerTimesRepository
):ViewModel() {

    private val _prayerTimes:MutableLiveData<PrayerTimeResource> = MutableLiveData()
    val prayerTimes:MutableLiveData<PrayerTimeResource> get() = _prayerTimes

    private val _prayerTimesNotification:MutableLiveData<PrayerNotificationResource> = MutableLiveData()
    val prayerTimesNotification:MutableLiveData<PrayerNotificationResource> get() = _prayerTimesNotification

    private val _selecteStateLiveData:MutableLiveData<PrayerTimeResource> = MutableLiveData()
    val selecteStateLiveData:MutableLiveData<PrayerTimeResource> get() = _selecteStateLiveData


    var listState: Parcelable? = null

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

            val getStatus = prayerTimesRepository.getNotificationData(
                date = "",
                prayer_tag = "Notification",
                finalState = 0
            )

            if(getStatus?.state == 1)
            _prayerTimesNotification.value = PrayerNotificationResource.notificationData(prayerTimesRepository.getNotificationData(date,prayer_tag))
            else
                _prayerTimesNotification.value = PrayerNotificationResource.NotificationStateRequired

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

    fun setPrayerTrack(language:String,prayer_tag: String,bol:Boolean)
    {
        viewModelScope.launch {

            val response = prayerTimesRepository.setPrayerTimeTrack(language = language,prayer_tag = prayer_tag, isPrayed = bol)

            when(response)
            {
                is ApiResource.Failure -> _prayerTimesNotification.value = PrayerNotificationResource.prayerTrackFailed
                is ApiResource.Success ->
                {
                    if(response.value?.Success == true)
                        processPrayerTrackData(prayerTimesRepository.getPrayerTimeTrack())
                    else
                        _prayerTimesNotification.value = PrayerNotificationResource.prayerTrackFailed
                }
            }
        }
    }

    private fun processPrayerTrackData(response: ApiResource<PrayerTrackResponse?>)
    {
        when(response)
        {
            is ApiResource.Failure -> _prayerTimesNotification.value = PrayerNotificationResource.prayerTrackFailed
            is ApiResource.Success ->
            {
                if(response.value?.Success == true)
                    _prayerTimesNotification.value = PrayerNotificationResource.prayerTrackData(response.value.Data)
                else
                    _prayerTimesNotification.value = PrayerNotificationResource.prayerTrackFailed

            }
        }
    }

    fun updatePrayerTrack(date:String,prayer_tag: String)
    {
        viewModelScope.launch {
            _prayerTimesNotification.value = PrayerNotificationResource.dateWiseNotificationData(prayerTimesRepository.updatePrayerTrackAuto(date=date,prayer_tag=prayer_tag) as java.util.ArrayList<PrayerNotification>)
        }
    }

    fun clearPrayerNotificationLiveData()
    {
        _prayerTimesNotification.value = CommonResource.CLEAR
    }

    fun updateSelectedState(state: StateModel)
    {
        _selecteStateLiveData.value = PrayerTimeResource.selectedState(state)
    }

}