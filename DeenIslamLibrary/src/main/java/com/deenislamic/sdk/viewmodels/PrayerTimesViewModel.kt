package com.deenislamic.sdk.viewmodels

import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deenislamic.sdk.service.database.entity.PrayerNotification
import com.deenislamic.sdk.service.database.entity.PrayerTimes
import com.deenislamic.sdk.service.models.CommonResource
import com.deenislamic.sdk.service.models.prayer_time.PrayerNotificationResource
import com.deenislamic.sdk.service.models.prayer_time.PrayerTimeResource
import com.deenislamic.sdk.service.models.ramadan.StateModel
import com.deenislamic.sdk.service.network.ApiResource
import com.deenislamic.sdk.service.network.response.prayertimes.PrayerTimesResponse
import com.deenislamic.sdk.service.network.response.prayertimes.calendartracker.PrayerTrackerResponse
import com.deenislamic.sdk.service.network.response.prayertimes.tracker.Data
import com.deenislamic.sdk.service.network.response.prayertimes.tracker.PrayerTrackResponse
import com.deenislamic.sdk.service.repository.PrayerTimesRepository
import com.google.gson.Gson
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

    private val _monthlyPrayerTrackLiveData:MutableLiveData<PrayerNotificationResource> = MutableLiveData()
    val monthlyPrayerTrackLiveData:MutableLiveData<PrayerNotificationResource> get() = _monthlyPrayerTrackLiveData


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

    fun reloadPrayerTracker(prayerTrackerResponse: PrayerTrackerResponse)
    {
        _prayerTimes.value = PrayerTimeResource.prayerTracker(prayerTrackerResponse)
    }

    fun setPrayerTrackDateWise(language: String, prayer_tag: String, bol: Boolean, prayerdate: String, prayerTrackerResponse: PrayerTrackerResponse)
    {
        viewModelScope.launch {

/*              val newDateFormat = LocalDate.parse(prayerdate, DateTimeFormatter.ofPattern("dd/MM/yyyy")).
            format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))*/

            val response = prayerTimesRepository.setPrayerTimeTrackDateWise(language = language,prayer_tag = prayer_tag, isPrayed = bol,
                prayerdate = prayerdate
            )

            when(response)
            {
                is ApiResource.Failure -> _prayerTimesNotification.value = PrayerNotificationResource.prayerTrackFailed
                is ApiResource.Success ->
                {
                    if(response.value?.Success == true)
                        _prayerTimesNotification.value = PrayerNotificationResource.prayerTrackSuccess(response.value.Success)
                    else
                        _prayerTimesNotification.value = PrayerNotificationResource.prayerTrackFailed
                }
            }

            modifyPrayerTimeTracker(language, prayer_tag,bol, prayerdate, prayerTrackerResponse)
        }
    }

    private fun modifyPrayerTimeTracker(language: String, prayer_tag: String, bol: Boolean, prayerdate: String,prayerTrackerResponse: PrayerTrackerResponse) {
//        _prayerTimes.value = PrayerTimeResource.prayerTracker(response.value)
        val updatedTrackers = prayerTrackerResponse.Data.tracker.map { tracker ->
            if (tracker.TrackingDate.startsWith(prayerdate)) {
                when (prayer_tag) {
                    "Fajr" -> tracker.copy(Fajr = bol)
                    "Zuhr" -> tracker.copy(Zuhr = bol)
                    "Asar" -> tracker.copy(Asar = bol)
                    "Maghrib" -> tracker.copy(Maghrib = bol)
                    "Isha" -> tracker.copy(Isha = bol)
                    else -> tracker
                }
            } else {
                tracker
            }
        }
        _prayerTimes.value = PrayerTimeResource.prayerTracker(prayerTrackerResponse.copy(Data = prayerTrackerResponse.Data.copy(tracker = updatedTrackers)))
    }

    fun getPrayerTimeTracker(localtion:String,language:String,requiredDate:String)
    {
        viewModelScope.launch {
            when(val response = prayerTimesRepository.getPrayerTracking(localtion,language,requiredDate)){
                is ApiResource.Failure -> _prayerTimes.value = CommonResource.API_CALL_FAILED
                is ApiResource.Success ->
                    if(response.value?.Success == true)
                    {
                        _prayerTimes.value = PrayerTimeResource.prayerTracker(response.value)
                    }

                    else
                        _prayerTimes.value = PrayerTimeResource.prayerTimeEmpty
            }
        }
    }

    fun setPrayerTrackFromMonthlyTracker(tracker: Data) {
        _monthlyPrayerTrackLiveData.value = PrayerNotificationResource.prayerTrackData(tracker)
    }

}